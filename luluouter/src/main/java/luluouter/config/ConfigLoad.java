package luluouter.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoad {
    private String path = "conf/pipe.properties";

    public Map<String, Object> loadConfig() {
        File file = new File(path);
        Map<String, Object> configs = getConfig(file);
        return configs;
    }

    private Map<String, Object> getConfig(File file) {

        Map<String, Object> result = new HashMap<String, Object>();
        String encoding = "GBK";

        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); //
                BufferedReader bufferedReader = new BufferedReader(read);) {
            Properties prop = new Properties();
            prop.load(bufferedReader);

            String msg_port = prop.getProperty("msg_port", "5273");
            result.put("msg_port", Integer.parseInt(msg_port));

            String data_port = prop.getProperty("data_port", "5266");
            result.put("data_port", Integer.parseInt(data_port));

            return result;
        } catch (IOException e) {
            return null;
        }
    }

}
