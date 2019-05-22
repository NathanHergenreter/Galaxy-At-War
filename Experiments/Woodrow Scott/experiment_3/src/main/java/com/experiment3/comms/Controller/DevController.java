package com.experiment3.comms.Controller;

/*Note: this is only meant for feature testing*/

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/dev")
public class DevController {

    @GetMapping("/socket")
    public String getTestSocketPage(){
        return "dev/socketdev";
    }

}
