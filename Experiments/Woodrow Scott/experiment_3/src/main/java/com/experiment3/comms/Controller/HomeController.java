package com.experiment3.comms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/")
public class HomeController {

    @GetMapping
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("html/home");
        mv.getModel().put("data", "Populated with data");

        return mv;
    }

}
