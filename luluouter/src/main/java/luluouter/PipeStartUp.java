package luluouter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import luluouter.config.ConfigLoad;
import luluouter.data.inner.InnerDataServer;
import luluouter.msg.inner.InnerMsgServer;

@Component
public class PipeStartUp implements ApplicationRunner {

    private static final String MSG_PORT_KEY = "msg_port";
    private static final int MSG_PORT_KEY_DEFAULT = 5273;

    private static final String DATA_PORT_KEY = "data_port";
    private static final int DATA_PORT_KEY_DEFAULT = 5266;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }

    private void init() {

        ConfigLoad configLoad = new ConfigLoad();
        Map<String, Object> configs = configLoad.loadConfig();
        if (configs == null) {
            configs = new HashMap<String, Object>();
            configs.put(MSG_PORT_KEY, MSG_PORT_KEY_DEFAULT);
            configs.put(DATA_PORT_KEY, DATA_PORT_KEY_DEFAULT);
        }

        int msg_port = (int) configs.get(MSG_PORT_KEY);
        int data_port = (int) configs.get(DATA_PORT_KEY);
        
        System.out.println("InnerDataServer ...\n");
        InnerDataServer innerDataServer = new InnerDataServer(data_port);
        startThread(innerDataServer);

        System.out.println("InnerMsgServer ...\n");
        InnerMsgServer innerServer = new InnerMsgServer(msg_port);
        startThread(innerServer);
	}

    private void startThread(Runnable runnable) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }
}
