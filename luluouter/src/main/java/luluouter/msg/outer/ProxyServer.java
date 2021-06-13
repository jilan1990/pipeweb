package luluouter.msg.outer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
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

                    if (innerMsgClient != null) {
                        innerMsgClient.createPipes(outerClient, mole);
                    }
                });
                executor.shutdown();
            }
        } catch (Exception e) {
            System.out.println("ProxyServer.init: " + e.getMessage());
        } finally {
            System.out.println("ProxyServer.finally:" + mole);
            ProxyServerMaster.getInstance().removeMole(mole);
        }
    }

    public void stop() {
        stop = true;
        if (Objects.nonNull(serverSocket)) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("ProxyServer.stop:mole" + mole);
            }
        }
    }

}
