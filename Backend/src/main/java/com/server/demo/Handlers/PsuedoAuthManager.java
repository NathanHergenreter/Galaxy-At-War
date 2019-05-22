package com.server.demo.Handlers;

import com.server.demo.Entities.User;

import java.util.ArrayList;
import java.util.List;

public class PsuedoAuthManager {

    ArrayList<User> sessionAuths;

    public PsuedoAuthManager(){
        sessionAuths = new ArrayList<User>();
    }
}
