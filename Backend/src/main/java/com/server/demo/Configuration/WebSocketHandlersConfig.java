package com.server.demo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.ExpiringSession;

import com.server.demo.Handlers.WebSocketConnectHandler;
import com.server.demo.Handlers.WebSocketDisconnectHandler;
import com.server.demo.Repositories.ActiveWebSocketUserRepository;

@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {

	@Bean
	public WebSocketConnectHandler<S> webSocketConnectHandler(SimpMessageSendingOperations messagingTemplate,
			ActiveWebSocketUserRepository repository) {
		return new WebSocketConnectHandler<S>(messagingTemplate, repository);
	}

	@Bean
	public WebSocketDisconnectHandler<S> webSocketDisconnectHandler(ActiveWebSocketUserRepository repository) {
		return new WebSocketDisconnectHandler<S>(repository);
	}
}
