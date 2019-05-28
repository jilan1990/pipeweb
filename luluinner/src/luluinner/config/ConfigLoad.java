package luluinner.config;

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
            String outer_ip = prop.getProperty("outer_ip");
            if (outer_ip == null) {
                return null;
            }
            result.put("outer_ip", outer_ip);

            String outer_msg_port = prop.getProperty("outer_msg_port", "5273");
            result.put("outer_msg_port", Integer.parseInt(outer_msg_port));

            String outer_data_port = prop.getProperty("outer_data_port", "5266");
            result.put("outer_data_port", Integer.parseInt(outer_data_port));

            String inner_ip = prop.getProperty("inner_ip", "127.0.0.1");
            result.put("inner_ip", inner_ip);

            String inner_port = prop.getProperty("inner_port", "80");
            result.put("inner_port", Integer.parseInt(inner_port));

            return result;
        } catch (IOException e) {
            return null;
        }
    }

}
