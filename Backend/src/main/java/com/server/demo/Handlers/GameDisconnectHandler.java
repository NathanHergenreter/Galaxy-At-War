package com.server.demo.Handlers;

import com.server.demo.Repositories.ActiveGameSocketUserRepository;
import com.server.demo.Repositories.ActiveWebSocketUserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class GameDisconnectHandler<S> implements ApplicationListener<SessionDisconnectEvent> {

    private ActiveGameSocketUserRepository repository;

    public GameDisconnectHandler(ActiveGameSocketUserRepository repository){
        super();
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        repository.deleteAll();
    }
}
