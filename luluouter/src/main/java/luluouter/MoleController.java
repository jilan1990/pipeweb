package luluouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import luluouter.controller.model.RestMole;
import luluouter.msg.inner.InnerMsgClientMaster;
import luluouter.msg.model.Mole;
import luluouter.msg.model.Pigeon;
import luluouter.msg.outer.ProxyServerMaster;

@RestController
public class MoleController {

    @RequestMapping("/moles")
    public List<Mole> getMoles() {
        List<Mole> result = new ArrayList<Mole>();

        Set<Mole> set = ProxyServerMaster.getInstance().getMoles();
        result.addAll(set);

        return result;
    }

    @RequestMapping(value = "/addmole", method = RequestMethod.POST)
    public Map addMoles(@RequestBody RestMole restMole) {
        Map result = new HashMap();

//        if (params == null || params.isEmpty()) {
//            result.put("result", "failed");
//            return result;
//        }

        // RestMole restMole = JSON.parseObject(params, RestMole.class);

        int coverPort = restMole.getCoverPort();
        String pigeonCode = restMole.getPigeonCode();
        String pigeonAddress = restMole.getPigeonAddress();
        String moleIp = restMole.getMoleIp();
        int molePort = restMole.getMolePort();

        Pigeon pigeon = new Pigeon(pigeonAddress, pigeonCode);
        boolean existPigeon = InnerMsgClientMaster.getInstance().hasPigeon(pigeon);
        if (!existPigeon) {
            result.put("result", "failed");
            return result;
        }

        Mole mole = new Mole(pigeon, coverPort, moleIp, molePort);

        boolean success = ProxyServerMaster.getInstance().lurkMole(mole);
        result.put("result", "success");

        return result;
    }
}
