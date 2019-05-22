package net.sqltest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

//Controller for the home page
@Controller
//@RequestMapping(value="/")
public class HomeController {

	/*
    @GetMapping
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("html/home");
        mv.getModel().put("data", "Populated with data");

        return mv;
    }
    */
	
	//Will print "Welcome" on the home page
	//@RequestMapping("/")
	//@ResponseBody
	String home()
	{
		return "Welcome";
	}
}
