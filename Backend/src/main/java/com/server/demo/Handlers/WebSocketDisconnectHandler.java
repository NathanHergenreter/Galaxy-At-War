package com.server.demo.Handlers;

import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.server.demo.Repositories.ActiveWebSocketUserRepository;

public class WebSocketDisconnectHandler<S> implements ApplicationListener<SessionDisconnectEvent> {
	private ActiveWebSocketUserRepository repository;

	public WebSocketDisconnectHandler(ActiveWebSocketUserRepository repository) {
		super();
		this.repository = repository;
	}

	public void onApplicationEvent(SessionDisconnectEvent event) {
		repository.deleteAll();

	}
}