package com.ws.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//@RequestMapping("/api")
@Controller
public class BasicController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/list")
    public ModelAndView home(final Model model){

        List dataList = IntStream.range(0, 7).mapToObj(i->getData(i)).collect(Collectors.toList());
        model.addAttribute("dataList", dataList);

        return new ModelAndView("html/home");
    }

    @GetMapping("/test")
    public String Test(){
        return "static";
    }


    private Data getData(int i){
        return new Data(i);
    }
}

class Data{
    String a, b;

    public Data(int i){
        a = ""+i;
        b = ""+i*2;
    }
}
