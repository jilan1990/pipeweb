package luluinner.upper;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import luluinner.msg.upper.OuterMsgServer;
import luluinner.util.Constants;

public class OuterMsgMaster {


    private static final OuterMsgMaster INSTANCE = new OuterMsgMaster();

    private OuterMsgMaster() {
    }

    public static OuterMsgMaster getInstance() {
        return INSTANCE;
    }

    public void init(Map<String, Object> configs) {

        OuterMsgServer outerMsgServer = executeConnect(configs);
        if (outerMsgServer == null) {
            System.out.println("OuterMsgMaster.init.failed:" + configs);
            return;
        }
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (outerMsgServer.isTimeOut()) {
                System.out.println("OuterMsgMaster.restart.outerMsgServer:" + configs);
                executeConnect(configs);
            }
        }, Constants.RESTART_PERIOD, Constants.RESTART_PERIOD, TimeUnit.MILLISECONDS);
    }

    private OuterMsgServer executeConnect(Map<String, Object> configs) {

        String outer_ip = (String) configs.get("outer_ip");
        int outer_msg_port = (Integer) configs.get("outer_msg_port");

        OuterMsgServer outerServer = null;
        try {
            Socket socket = new Socket(outer_ip, outer_msg_port);
            System.out.println("OuterMsgMaster.executeConnect" + socket.getRemoteSocketAddress());

            outerServer = new OuterMsgServer(socket, configs);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(outerServer);
            executor.shutdown();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return outerServer;
    }
}
