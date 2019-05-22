package com.server.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.server.demo.Entities.Messages;
import com.server.demo.Model.ActiveWebSocketUser;
import com.server.demo.Repositories.ActiveWebSocketUserRepository;
import com.server.demo.Service.IChatService;

@Controller
@RequestMapping("messages")
@CrossOrigin
public class MessageController {

	@Autowired
	private IChatService chatService;

	@Autowired
	private ActiveWebSocketUserRepository repo;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/chat/private/send-message")
	@SendTo("/topic/chat")
	public void greeting(String message, @Header("simpSessionId") String sessionId) throws Exception {
		Messages m = new Gson().fromJson(message, Messages.class);
		if (m.getReceiver() == null) {
			throw new IllegalArgumentException("Reciever must be set for private messaging");
		}
		List<ActiveWebSocketUser> users = repo.findByUsername(m.getReceiver().getName());
		chatService.addMessage(m);
		simpMessagingTemplate.convertAndSend("/topic/chat/" + users.get(users.size() - 1).getSessionId(),
				new Gson().toJson(m));
	}

	@GetMapping
	@ResponseBody
	public List<Messages> getMessages() {
		return chatService.getAll();
	}

	@MessageMapping("/chat/public/send-message")
	@SendTo("/topic/chat")
	public Messages greeting(String message) throws Exception {
		Messages m = new Gson().fromJson(message, Messages.class);
		return chatService.addMessage(m);
	}

}
