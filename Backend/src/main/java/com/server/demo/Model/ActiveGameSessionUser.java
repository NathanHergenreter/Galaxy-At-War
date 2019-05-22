package com.server.demo.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ActiveGameSessionUser {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;
    private String sessionId;
    private Integer gameId;

    public ActiveGameSessionUser(){

    }

    public ActiveGameSessionUser(String username, String sessionId){
        this.username = username;
        this.sessionId = sessionId;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getSessionId(){
        return sessionId;
    }

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    public Integer getGameId(){
        return gameId;
    }

    public void setGameId(Integer gameId){
        this.gameId = gameId;
    }

}
