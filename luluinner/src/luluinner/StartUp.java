package luluinner;

import java.util.Date;
import java.util.Map;

import luluinner.config.ConfigLoad;
import luluinner.upper.OuterMsgMaster;

public class StartUp {

    public static void main(String[] args) {

        long ts = System.currentTimeMillis();
        String pigeonCode = String.valueOf(new Date());
        if (args != null && args.length > 0) {
            pigeonCode += "|" + args[0];
        }
        pigeonCode += "|" + ts;

        ConfigLoad configLoad = new ConfigLoad();
        Map<String, Object> configs = configLoad.loadConfig();
        if (configs == null) {
            return;
        }

        OuterMsgMaster.getInstance().init(configs);
    }

}
