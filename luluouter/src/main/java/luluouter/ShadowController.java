package luluouter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import luluouter.controller.model.Shadow;

@RestController
public class ShadowController {
    @RequestMapping("/shadows")
    public Shadow greeting() {
        Shadow shadow = new Shadow();
        shadow.setIp("fuck");
        return shadow;
    }
}
