package com.ws.demo.Controller;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@Controller
public class Api {

    @RequestMapping("/")
    public String meh(){
        return "Eh";
    }

    @ExceptionHandler(InvalidApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidApiException(){
        return "html/status/404";
    }

    @RequestMapping(value = "/{number}", method= RequestMethod.GET)
    public String getTeapot(@PathVariable("number") final int code, final Model model){
        if (code != 418){
            throw new InvalidApiException();
        }

        return "html/status/418";
    }

}

class InvalidApiException extends RuntimeException{

}