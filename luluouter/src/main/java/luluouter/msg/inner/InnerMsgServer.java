package luluouter.msg.inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class InnerMsgServer implements Runnable {

    private int port;

    public InnerMsgServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("InnerMsgServer.listening:" + port);
            while (true) {    
                //
                Socket client = serverSocket.accept();
                //
                InnerMsgClient innerClient = new InnerMsgClient(client);
                innerClient.init();

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.submit(innerClient);
                executor.shutdown();
            }    
        } catch (Exception e) {    
            System.out.println("InnerServer.init: " + e.getMessage());
        }
    }
}
