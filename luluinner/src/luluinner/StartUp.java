package luluinner;

import java.util.Map;

import luluinner.config.ConfigLoad;
import luluinner.upper.OuterMsgMaster;

public class StartUp {

    public static void main(String[] args) {

        ConfigLoad configLoad = new ConfigLoad();
        Map<String, Object> configs = configLoad.loadConfig();
        if (configs == null) {
            return;
        }

        OuterMsgMaster.getInstance().init(configs);
    }

}
