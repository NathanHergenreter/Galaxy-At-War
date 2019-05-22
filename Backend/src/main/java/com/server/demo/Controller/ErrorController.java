package com.server.demo.Controller;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    private ErrorAttributes errorAttributes;
    private final static String ERROR_PATH = "/error";

    public ErrorController(ErrorAttributes errorAttributes){
        this.errorAttributes = errorAttributes;
    }

    /*@RequestMapping("/error")
    @ResponseBody
    public String genericError(){
        return "unknown_error";
    }*/

    @Override
    public String getErrorPath() {
        return null;
    }
}
