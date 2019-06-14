package luluinner;

import java.util.Date;
import java.util.Map;

import luluinner.config.ConfigMaster;
import luluinner.msg.upper.OuterMsgServer;

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
        if (configs == null) {
            return;
        }
        String outer_ip = (String) configs.get("outer_ip");
        int outer_msg_port = (Integer) configs.get("outer_msg_port");
        int outer_data_port = (Integer) configs.get("outer_data_port");

        System.out.println("OuterMsgMaster.begin:" + configs);
        OuterMsgServer outerMsgServer = new OuterMsgServer(outer_ip, outer_msg_port, pigeonCode, outer_data_port);

        outerMsgServer.init();
    }
}
