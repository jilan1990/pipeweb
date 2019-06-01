package luluouter.msg.inner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import luluouter.controller.model.Pigeon;

public class InnerMsgClientMaster {

    private static final InnerMsgClientMaster INSTANCE = new InnerMsgClientMaster();

    private Map<Pigeon, InnerMsgClient> innerMsgClients = new ConcurrentHashMap<Pigeon, InnerMsgClient>();

    public static InnerMsgClientMaster getInstance() {
        return INSTANCE;
    }

    private InnerMsgClientMaster() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            execute();
        }, 1, 1, TimeUnit.MINUTES);
        // executor.shutdown();
    }

    private void execute() {
        for (Map.Entry<Pigeon, InnerMsgClient> entry : innerMsgClients.entrySet()) {
            InnerMsgClient proxyServer = entry.getValue();
            proxyServer.heartBeat();
        }
    }

    public InnerMsgClient getInnerMsgClient(Pigeon pigeon) {
        return innerMsgClients.get(pigeon);
    }

    public void removeInnerMsgClient(Pigeon shadow) {
        innerMsgClients.remove(shadow);
    }

    public void addInnerMsgClient(Pigeon shadow, InnerMsgClient innerMsgClient) {
        innerMsgClients.put(shadow, innerMsgClient);
    }

    public Set<Pigeon> getPigeons() {
        return innerMsgClients.keySet();
    }
}
