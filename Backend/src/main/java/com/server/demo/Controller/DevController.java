package com.server.demo.Controller;

/*Note: this is only meant for feature testing*/

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/dev")
public class DevController {

    @GetMapping("/socket")
    public String getTestSocketPage(){
        return "dev/socketdev";
    }

    @PostMapping(value="/post")
    @ResponseBody
    public ResponseEntity.BodyBuilder postData(HttpSession session, @RequestParam(name="data") String data){

        String id = (String)session.getAttribute("user_name");
        return ResponseEntity.status(HttpStatus.OK);
    }

}
