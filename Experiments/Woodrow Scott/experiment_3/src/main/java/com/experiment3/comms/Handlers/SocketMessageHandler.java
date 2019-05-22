package com.experiment3.comms.Handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class SocketMessageHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)throws Exception{
        System.out.println("Session " + session.getId() + " closed");

        SessionCollector.getInstance().remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        System.out.println("Session " + session.getId() + "created");
        session.sendMessage(new TextMessage("Connection Established"));

        SessionCollector.getInstance().add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception{
        System.out.println("Message received: " + textMessage.getPayload());

        SessionCollector.getInstance().broadcast(session, textMessage.getPayload());
    }

}

class SessionCollector{

    static SessionCollector _sessionCollection;
    static SessionCollector getInstance(){
        if (_sessionCollection == null){
            _sessionCollection = new SessionCollector();
        }

        return _sessionCollection;
    }

    private SessionCollector(){
        _sessions = new ArrayList<>();
    }

    private List<WebSocketSession> _sessions;

    public void add(WebSocketSession session){
        if (_sessions.contains(session)){
            return;
        }

        _sessions.add(session);
    }

    public void remove(WebSocketSession session){
        _sessions.remove(session);
    }

    public void broadcast(WebSocketSession host, String message) throws Exception{

        TextMessage msg = new TextMessage(host.getId() + " : " +message);

        for (WebSocketSession s: _sessions) {
            if (s == host) {continue;}

            s.sendMessage(msg);
        }
    }
}
