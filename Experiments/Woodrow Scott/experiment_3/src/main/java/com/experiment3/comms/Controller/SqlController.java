package com.experiment3.comms.Controller;

import com.experiment3.comms.Handlers.JpaHandler;
import com.experiment3.comms.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value="/sql")
public class SqlController {
    @Autowired
    private JpaHandler jpaHandler;

    @GetMapping(path="/add")
    public @ResponseBody String addUser(@RequestParam String name,
                                        @RequestParam String email){

        User user = jpaHandler.findByUserName(name);
        if (user != null){
            return "User already exists";
        }

        user = new User(name, email);
        jpaHandler.save(user);

        return "Saved " + user.toString();
    }

    @GetMapping("/all")
    @CrossOrigin(origins = "http://localhost:4200")
    public @ResponseBody Iterable<User> getAllUsers(){
        List<User> list = jpaHandler.findAll();

        return list;
    }

    @GetMapping("/update")
    public @ResponseBody String updateUser(
            @RequestParam String name,
            @RequestParam String email
    ){
        User user = jpaHandler.findByUserName(name);

        if (email != null) {user.setEmail(email);}

        jpaHandler.save(user);
        user = jpaHandler.findByUserName(name);

        return user.toString();
    }

}
