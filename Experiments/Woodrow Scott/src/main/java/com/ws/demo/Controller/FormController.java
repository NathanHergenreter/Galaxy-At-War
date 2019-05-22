package com.ws.demo.Controller;

import com.ws.demo.Model.TestModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/form")
@Controller
public class FormController {

    @RequestMapping(value="/namenum", method=RequestMethod.GET)
    public String getForm(){
        return "html/namenumform";
    }

    @RequestMapping(value = "namenum", method = RequestMethod.POST)
    @ResponseBody
    public String submitForm(@ModelAttribute("testModel")TestModel testModel){
        return "{" + testModel.name + "}" + " {"+testModel.number+"}";
    }
}
