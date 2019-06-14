package luluouter.msg.outer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import luluouter.msg.model.Mole;

public class ProxyServerMaster {

    private static final ProxyServerMaster INSTANCE = new ProxyServerMaster();

    private Map<Integer, Mole> cover2Mole = new ConcurrentHashMap<Integer, Mole>();
    private Map<Mole, ProxyServer> proxyServers = new ConcurrentHashMap<Mole, ProxyServer>();

    public static ProxyServerMaster getInstance() {
        return INSTANCE;
    }

    private ProxyServerMaster() {
    }

    public boolean lurkMole(Mole mole) {
        try {
            ServerSocket serverSocket = new ServerSocket(mole.getCoverPort());
            ProxyServer proxyServer = new ProxyServer(mole, serverSocket);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.submit(proxyServer);
            executor.shutdown();

            cover2Mole.put(mole.getCoverPort(), mole);
            proxyServers.put(mole, proxyServer);

            return true;
        } catch (IOException e) {
            System.out.println("lurkMole failed:" + mole);
            e.printStackTrace();
            return false;
        }
    }

    public void deprecatedMole(int coverPort) {
        Mole mole = cover2Mole.remove(coverPort);
        if (Objects.isNull(mole)) {
            System.out.println("deprecatedMole failed:" + coverPort);
            return;
        }
        ProxyServer proxyServer = proxyServers.remove(mole);
        if (proxyServer == null) {
            System.out.println("deprecatedMole failed:" + mole);
            return;
        }
        proxyServer.stop();
    }

    public Set<Mole> getMoles() {
        return proxyServers.keySet();
    }

    public void removeMole(Mole mole) {
        cover2Mole.remove(mole.getCoverPort());
        proxyServers.remove(mole);
    }
}
