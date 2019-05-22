package com.server.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/")
public class HomeController {

    @GetMapping
    public ModelAndView home(HttpSession session){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("html/home");
        mv.getModel().put("data", "Populated with data");
        mv.getModel().put("session_id", session.getId());
        return mv;
    }

}
