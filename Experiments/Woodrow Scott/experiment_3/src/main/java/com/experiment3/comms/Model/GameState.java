package com.experiment3.comms.Model;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Random;

public class GameState {

    //Singelton
    static ArrayList<GameState> _gameStates;
    static GameState getInstance(String id){
        for (GameState g : _gameStates) {
            if (g._gameId==id) return g;
        }

        return null;
    }

    private String _gameId;
    private ArrayList<WebSocketSession> _clients;
    private WebSocketSession _host;

    public String getGameId(){
        return _gameId;
    }

    private GameState(){
        Random r = new Random();
        _gameId = Integer.toString(r.nextInt());
    }

    public void addClient(WebSocketSession client){
        if (_clients.contains(client)) {return;}

        _clients.add(client);
    }

    public void removeClient(WebSocketSession client){
        _clients.remove(client);
    }

    public void broadcast(WebSocketSession sender, String msg) throws Exception{

        TextMessage textMessage = new TextMessage(msg);

        for (WebSocketSession s: _clients) {
            if (s == sender) {continue;}

            s.sendMessage(textMessage);
        }
    }
}

class Instruction{
    String target;

    public Instruction(String raw){
        String args [] = raw.split(" ");
    }
}

enum InstructionCode{
    MOVE
}
