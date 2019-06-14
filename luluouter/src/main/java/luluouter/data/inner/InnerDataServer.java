package luluouter.data.inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class InnerDataServer implements Runnable {
    private int port;

    public InnerDataServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("InnerDataServer.listening:" + port);
            while (true) {    
                //
                Socket client = serverSocket.accept();
                System.out.println("InnerDataServer.accept:" + client.getRemoteSocketAddress());
                //
                InnerDataClient innerClient = new InnerDataClient(client);
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.submit(innerClient);
                executor.shutdown();
            }    
        } catch (Exception e) {
            System.out.println("InnerServer.init: " + port);
            e.printStackTrace();
        }
    }
}
