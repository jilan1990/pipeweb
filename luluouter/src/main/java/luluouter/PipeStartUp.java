package luluouter;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import luluouter.config.ConfigMaster;
import luluouter.data.inner.InnerDataServer;
import luluouter.data.util.Constants;
import luluouter.msg.inner.InnerMsgServer;

@Component
public class PipeStartUp implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }

    private void init() {

        ConfigMaster configLoad = ConfigMaster.getInstance();
        configLoad.init();
        Map<String, Object> configs = configLoad.getConfigs();

        int outer_msg_port = (Integer) configs.get(Constants.OUTER_MSG_PORT_KEY);
        int outer_data_port = (Integer) configs.get(Constants.OUTER_DATA_PORT_KEY);
        
        System.out.println("InnerDataServer ...\n");
        InnerDataServer innerDataServer = new InnerDataServer(outer_data_port);
        startThread(innerDataServer);

        System.out.println("InnerMsgServer ...\n");
        InnerMsgServer innerServer = new InnerMsgServer(outer_msg_port);
        startThread(innerServer);
	}

    private void startThread(Runnable runnable) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }
}
