package com.server.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/api")
public class ApiController {

    //Statistics
    @GetMapping(value="/stat/active_users")
    @ResponseBody
    public Integer getActiveUserCount(){
        //TODO return number of user sessions
        return 5;
    }

    @GetMapping(value="/stat/current_users")
    @ResponseBody
    public Integer getCurrentUserCount(){
        //TODO return number of user accounts
        return 9;
    }

    @GetMapping(value="/stat/active_games")
    @ResponseBody
    public Integer getActiveGameCount(){
        //TODO return number of games in session
        return 230;
    }

    @GetMapping(value="/stat/games_played")
    @ResponseBody
    public Integer getCurrentGameCount(){
        //TODO return number of games played
        return 40;
    }

}
