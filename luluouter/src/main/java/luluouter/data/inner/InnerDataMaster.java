package luluouter.data.inner;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import luluouter.data.model.Pipe;
import luluouter.data.util.SocketUtil;

public class InnerDataMaster {

    private static final InnerDataMaster INSTANCE = new InnerDataMaster();
    private Map<Long, Socket> outerClients = new ConcurrentHashMap<Long, Socket>();
    private Map<Long, Socket> innerDataSockets = new ConcurrentHashMap<Long, Socket>();

    AtomicLong index = new AtomicLong();

    public static InnerDataMaster getInstance() {
        return INSTANCE;
    }

    private InnerDataMaster() {

    }

    public long addOuterClient(Socket outerClient) {
        long theIndex = index.incrementAndGet();
        outerClients.put(theIndex, outerClient);
        return theIndex;
    }

    public Socket removeOuterClient(long index) {
        return outerClients.remove(index);
    }

    public void addInnerDataSocket(long index, Socket innerDataSocket) {
        innerDataSockets.put(index, innerDataSocket);
    }

    public void startPipes(long index) {
        Socket innerDataSocket = innerDataSockets.remove(index);
        Socket outerClient = outerClients.remove(index);
        startPipes(outerClient, innerDataSocket);
    }

    private void startPipes(Socket outerClient, Socket innerDataSocket) {
        try {
            startPipe(new Pipe(outerClient.getInputStream(), innerDataSocket.getOutputStream(),
                    "outerClient->innerDataSocket"));
            startPipe(new Pipe(innerDataSocket.getInputStream(), outerClient.getOutputStream(),
                    "innerDataSocket->outerClient"));
        } catch (IOException e) {
            e.printStackTrace();
            SocketUtil.close(outerClient);
            SocketUtil.close(innerDataSocket);
        }
    }

    private void startPipe(Pipe pipe) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(pipe);
        executor.shutdown();
    }

    public void destroyOuterClient(long index) {
        Socket outerClient = outerClients.remove(index);
        try {
            outerClient.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
