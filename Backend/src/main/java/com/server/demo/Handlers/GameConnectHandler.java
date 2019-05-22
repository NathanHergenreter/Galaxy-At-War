package com.server.demo.Handlers;

import com.server.demo.Model.ActiveGameSessionUser;
import com.server.demo.Repositories.ActiveGameSocketUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectEvent;

public class GameConnectHandler<S> implements ApplicationListener<SessionConnectEvent> {

    private ActiveGameSocketUserRepository repository;
    private SimpMessageSendingOperations messagingTemplate;

    public GameConnectHandler(SimpMessageSendingOperations messagingTemplate,
                              ActiveGameSocketUserRepository repository){
        super();
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String username = StringUtils.substringBetween(headers.get("nativeHeaders").toString(), "[", "]");
        String sessionID = SimpMessageHeaderAccessor.getSessionId(headers);
        repository.save(new ActiveGameSessionUser(username, sessionID));
    }
}
