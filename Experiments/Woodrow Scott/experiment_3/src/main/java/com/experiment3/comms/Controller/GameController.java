package com.experiment3.comms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/game")
public class GameController {

    @GetMapping("/comms")
    public String guiComms(){
        return "/dev/socketdev";
    }

}
