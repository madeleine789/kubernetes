package misows.pi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class MasterController {

    @Autowired
    private PiService service;

    @RequestMapping(path = "/pi/{precision}")
    public String getPi(@PathVariable int precision){
        return service.computePi(precision);
    }

    @RequestMapping(path = "/")
    public String root(){
        return "Pi service";
    }

}
