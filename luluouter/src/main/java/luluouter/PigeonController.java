package luluouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import luluouter.msg.inner.InnerMsgClientMaster;
import luluouter.msg.model.Pigeon;

@RestController
public class PigeonController {
    @RequestMapping("/pigeons")
    public List<Pigeon> greeting() {
        List<Pigeon> result = new ArrayList<Pigeon>();

        Set<Pigeon> set = InnerMsgClientMaster.getInstance().getPigeons();
        result.addAll(set);

        return result;
    }
}
