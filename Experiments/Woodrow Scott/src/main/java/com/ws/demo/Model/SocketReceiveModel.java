package com.ws.demo.Model;

public class SocketReceiveModel {
    private String content;

    public SocketReceiveModel(){

    }

    public SocketReceiveModel(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

}
