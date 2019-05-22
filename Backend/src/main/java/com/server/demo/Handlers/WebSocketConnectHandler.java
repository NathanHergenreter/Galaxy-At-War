package com.server.demo.Handlers;

import java.util.Arrays;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import com.server.demo.Model.ActiveWebSocketUser;
import com.server.demo.Repositories.ActiveWebSocketUserRepository;

public class WebSocketConnectHandler<S> implements ApplicationListener<SessionConnectEvent> {

	private ActiveWebSocketUserRepository repository;
	private SimpMessageSendingOperations messagingTemplate;

	public WebSocketConnectHandler(SimpMessageSendingOperations messagingTemplate,
			ActiveWebSocketUserRepository repository) {
		super();
		this.messagingTemplate = messagingTemplate;
		this.repository = repository;
	}

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		MessageHeaders headers = event.getMessage().getHeaders();
		String username = StringUtils.substringBetween(headers.get("nativeHeaders").toString(), "[", "]");
		String sessionID = SimpMessageHeaderAccessor.getSessionId(headers);
		repository.save(new ActiveWebSocketUser(username, Calendar.getInstance(), sessionID));
		messagingTemplate.convertAndSend("/topic/friends/signin", Arrays.asList(username));
	}
}