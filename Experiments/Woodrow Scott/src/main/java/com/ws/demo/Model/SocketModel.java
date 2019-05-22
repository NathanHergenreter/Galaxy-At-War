package com.ws.demo.Model;

public class SocketModel {
    private String name;

    public SocketModel(){

    }

    public SocketModel(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
