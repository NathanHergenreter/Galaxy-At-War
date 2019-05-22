package com.server.demo.Model;

public class PsuedoUser {
    public String username;
    public Integer authToken;

    public PsuedoUser(String username, Integer authToken){
        this.username = username;
        this.authToken = authToken;
    }
}
