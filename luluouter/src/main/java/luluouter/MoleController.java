package luluouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import luluouter.controller.model.RestMole;
import luluouter.controller.model.RestMoleResponse;
import luluouter.msg.inner.InnerMsgClientMaster;
import luluouter.msg.model.Mole;
import luluouter.msg.model.Pigeon;
import luluouter.msg.outer.ProxyServerMaster;

@RestController
public class MoleController {

    @RequestMapping("/moles")
    public List<RestMoleResponse> getMoles() {
        List<RestMoleResponse> result = new ArrayList<RestMoleResponse>();

        Set<Mole> set = ProxyServerMaster.getInstance().getMoles();

        for (Mole mole : set) {
            RestMoleResponse restMoleResponse = new RestMoleResponse();
            restMoleResponse.setCoverPort(mole.getCoverPort());
            restMoleResponse.setPigeonCode(mole.getPigeon().getCode());
            restMoleResponse.setPigeonAddress(mole.getPigeon().getAddress());
            restMoleResponse.setMoleIp(mole.getIp());
            restMoleResponse.setMolePort(mole.getPort());

            restMoleResponse.setStatus(InnerMsgClientMaster.getInstance().hasPigeon(mole.getPigeon()));

            result.add(restMoleResponse);
        }

        return result;
    }

    @RequestMapping(value = "/addmole", method = RequestMethod.POST)
    public Map addMoles(@RequestBody RestMole restMole) {
        Map result = new HashMap();

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
        if (!success) {
            result.put("result", "failed");
            return result;
        }
        result.put("result", "success");

        return result;
    }

    @RequestMapping(value = "/delmole", method = RequestMethod.DELETE)
    public Map delMole(@RequestParam(value = "coverPort") int coverPort) {
        Map result = new HashMap();

        ProxyServerMaster.getInstance().deprecatedMole(coverPort);

        result.put("result", "success");

        return result;
    }
}
