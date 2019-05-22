package com.experiment3.comms.Handlers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value="/game/api")
public class GameSignalHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception{
        CommDistributor.getInstance().remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        CommDistributor.getInstance().add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception{
        CommDistributor.getInstance().broadcast(session, textMessage.getPayload());
    }
}

//TODO replace with GameState when completed
class CommDistributor{

    static CommDistributor _commDistributor;
    static CommDistributor getInstance(){
        if (_commDistributor == null){
            _commDistributor = new CommDistributor();
        }

        return _commDistributor;
    }

    private CommDistributor(){
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
