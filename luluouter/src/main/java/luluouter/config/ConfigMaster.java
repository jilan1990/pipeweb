package luluouter.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import luluouter.data.util.Constants;

public class ConfigMaster {

    private static final ConfigMaster INSTANCE = new ConfigMaster();

    private String path = "conf/pipe.properties";
    private Map<String, Object> configs = new HashMap<String, Object>();

    private ConfigMaster() {
        init();
    }

    public static ConfigMaster getInstance() {
        return INSTANCE;
    }

    public void init() {
        getConfig(path);
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    private void getConfig(String path) {

        String encoding = "GBK";
        File file = new File(path);
        if (!file.exists()) {
            configs.put(Constants.OUTER_MSG_PORT_KEY, Constants.OUTER_MSG_PORT_DEFAULT);
            configs.put(Constants.OUTER_DATA_PORT_KEY, Constants.OUTER_DATA_PORT_DEFAULT);
            return;
        }

        try (InputStream is = Files.newInputStream(Paths.get(path));
                InputStreamReader read = new InputStreamReader(is, encoding);
                BufferedReader bufferedReader = new BufferedReader(read);) {
            Properties prop = new Properties();
            prop.load(bufferedReader);

            String outer_msg_port = prop.getProperty(Constants.OUTER_MSG_PORT_KEY,
                    String.valueOf(Constants.OUTER_MSG_PORT_DEFAULT));
            configs.put(Constants.OUTER_MSG_PORT_KEY, Integer.parseInt(outer_msg_port));

            String outer_data_port = prop.getProperty(Constants.OUTER_DATA_PORT_KEY,
                    String.valueOf(Constants.OUTER_DATA_PORT_DEFAULT));
            configs.put(Constants.OUTER_DATA_PORT_KEY, Integer.parseInt(outer_data_port));

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
