package luluouter.msg.outer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import luluouter.msg.inner.InnerMsgClient;
import luluouter.msg.inner.InnerMsgClientMaster;
import luluouter.msg.model.Mole;

public class ProxyServer implements Runnable {

    private volatile boolean stop = true;
    private Mole mole;
    private ServerSocket serverSocket;

    public ProxyServer(Mole mole, ServerSocket serverSocket) {
        this.mole = mole;
        this.serverSocket = serverSocket;
        stop = false;
    }

    @Override
    public void run() {
        try (ServerSocket closeSocket = serverSocket) {
            System.out.println("ProxyServer.listening:" + mole);

            while (!stop) {
                Socket outerClient = closeSocket.accept();
                System.out.println("ProxyServer.accept:" + outerClient.getRemoteSocketAddress());

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.submit(() -> {
                    InnerMsgClient innerMsgClient = InnerMsgClientMaster.getInstance()
                            .getInnerMsgClient(mole.getPigeon());

                    innerMsgClient.createPipes(outerClient);
                });
                executor.shutdown();
            }
        } catch (Exception e) {
            System.out.println("ProxyServer.init: " + e.getMessage());
        } finally {
            System.out.println("ProxyServer.finally:" + mole);
        }
    }

    public void stop() {
        stop = true;
    }

}
