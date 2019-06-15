package luluinner;

import java.util.Date;
import java.util.Map;

import luluinner.config.ConfigMaster;
import luluinner.msg.upper.OuterMsgServer;
import luluinner.util.Constants;

public class StartUp {

    public static void main(String[] args) {

        long ts = System.currentTimeMillis();
        String pigeonCode = String.valueOf(new Date());
        if (args != null && args.length > 0) {
            pigeonCode += "|" + args[0];
        }
        pigeonCode += "|" + ts;

        ConfigMaster configLoad = ConfigMaster.getInstance();

        configLoad.setPigeonCode(pigeonCode);

        Map<String, Object> configs = configLoad.getConfigs();

        String outer_ip = (String) configs.get(Constants.OUTER_IP_KEY);
        int outer_msg_port = (Integer) configs.get(Constants.OUTER_MSG_PORT_KEY);
        int outer_data_port = (Integer) configs.get(Constants.OUTER_DATA_PORT_KEY);

        System.out.println("OuterMsgMaster.begin:" + configs);
        OuterMsgServer outerMsgServer = new OuterMsgServer(outer_ip, outer_msg_port, pigeonCode, outer_data_port);

        outerMsgServer.init();
    }
}
