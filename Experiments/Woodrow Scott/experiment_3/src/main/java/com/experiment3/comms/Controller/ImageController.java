package com.experiment3.comms.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value="/img")
public class ImageController {

    @GetMapping("/stone")
    public void getStoneImage(){

    }

}
