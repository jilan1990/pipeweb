package luluouter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import luluouter.config.ConfigMaster;

@Controller
public class DownloadController {
    String fileName = "luluinner.sh";

    @GetMapping("/luluinner.sh")
    public void greeting(HttpServletRequest req, HttpServletResponse res) {
        String localIp = req.getLocalAddr();
        String reqAddr = req.getRemoteAddr();

        System.out.println("/download begin " + localIp + " from=" + reqAddr);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try {
            ConfigMaster configLoad = ConfigMaster.getInstance();
            Map<String, Object> config = configLoad.getConfigs();
            config.put("outer_ip", localIp);

            StringBuffer sb = new StringBuffer();
            sb.append("folder=\"conf\"");
            sb.append("\n");
            sb.append("if [ ! -d \"$folder\" ]; then");
            sb.append("\n");
            sb.append("  mkdir \"$folder\"");
            sb.append("\n");
            sb.append("fi");
            sb.append("\n");
            sb.append("\n");
            
            sb.append("cat > conf/pipe.properties << \"EOF\"");
            sb.append("\n");
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("\n");
            }
            sb.append("EOF");
            sb.append("\n");
            sb.append("rm luluinner.jar");
            sb.append("\n");
            sb.append("wget http://" + localIp + ":1072/luluinner.jar");
            sb.append("\n");

            sb.append("java -jar luluinner.jar");
            sb.append("\n");

            String str = sb.toString();
            byte[] buff = str.getBytes();

            OutputStream os = res.getOutputStream();
            File file = new File(fileName);
            System.out.println(file.getAbsolutePath());

            os.write(buff, 0, buff.length);
            os.flush();

        } catch (IOException e) {
            System.out.println("/download failed from=" + reqAddr);
            e.printStackTrace();
        }
        System.out.println("/download success from=" + reqAddr);
    }

}
