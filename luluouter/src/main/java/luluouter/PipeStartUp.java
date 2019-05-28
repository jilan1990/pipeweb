package luluouter;

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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }

    private void init() {

        ConfigLoad configLoad = new ConfigLoad();
        Map<String, Object> configs = configLoad.loadConfig();
        if (configs == null) {
            return;
        }

        int msg_port = (int) configs.get("msg_port");
        int data_port = (int) configs.get("data_port");
        
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
