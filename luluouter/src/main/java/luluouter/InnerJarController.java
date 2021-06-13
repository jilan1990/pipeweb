package luluouter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InnerJarController {
    private static final String fileName = "luluinner.jar";

    @GetMapping("/luluinner.jar")
    public void greeting(HttpServletRequest req, HttpServletResponse res) {
        String reqAddr = req.getRemoteAddr();
        System.out.println("/download begin from=" + reqAddr);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;

        try {
            os = res.getOutputStream();
            File file = new File(fileName);
            System.out.println(file.getAbsolutePath());
            bis = new BufferedInputStream(new FileInputStream(file));
            int len = bis.read(buff);
            while (len != -1) {
                os.write(buff, 0, len);
                os.flush();
                len = bis.read(buff);
            }
        } catch (IOException e) {
            System.out.println("/download failed from=" + reqAddr);
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("/download success from=" + reqAddr);
    }

}
