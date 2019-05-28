package luluouter.msg.outer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import luluouter.msg.inner.InnerMsgClient;

public class ProxyServerMaster {

    private static final ProxyServerMaster INSTANCE = new ProxyServerMaster();

    private Map<Integer, ProxyServer> proxyServers = new ConcurrentHashMap<Integer, ProxyServer>();

    public static ProxyServerMaster getInstance() {
        return INSTANCE;
    }

    private ProxyServerMaster() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            execute();
        }, 1, 1, TimeUnit.MINUTES);
        // executor.shutdown();
    }

    private void execute() {
        for (Map.Entry<Integer, ProxyServer> entry : proxyServers.entrySet()) {
            ProxyServer proxyServer = entry.getValue();
            proxyServer.heartBeat();
        }
    }

    public void addInnerClient(int proxyPort, InnerMsgClient innerClient) {
        ProxyServer proxyServer = proxyServers.get(proxyPort);
        if (proxyServer == null) {
            proxyServer = new ProxyServer(proxyPort);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.submit(proxyServer);
            executor.shutdown();

            proxyServers.put(proxyPort, proxyServer);
        }
        proxyServer.addInnerClient(innerClient);
    }

    public void removeInnerMsgClient(int port, String key) {
        ProxyServer proxyServer = proxyServers.get(port);
        if (proxyServer == null) {
            return;
        }
        proxyServer.removeInnerMsgClient(key);
    }
}
