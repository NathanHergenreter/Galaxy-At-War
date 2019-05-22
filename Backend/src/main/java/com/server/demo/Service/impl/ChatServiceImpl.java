package com.server.demo.Service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.server.demo.Entities.Messages;
import com.server.demo.Entities.User;
import com.server.demo.Model.UserManager;
import com.server.demo.Repositories.MessagesRepository;
import com.server.demo.Service.IChatService;

@Service
public class ChatServiceImpl implements IChatService {

	@Autowired
	private MessagesRepository messagesRepo;

	@Autowired
	private UserManager userManager;

	@Override
	public List<Messages> getAll() {
		List<Messages> messages = messagesRepo.findAll();
		messages.forEach(message -> {

			if (message.getSender() != null) {
				User sender = new User();
				sender.setName(message.getSender().getName());
				message.setSender(sender);
			}
			if (message.getReceiver() != null) {
				User receiver = new User();
				receiver.setName(message.getReceiver().getName());
				message.setReceiver(receiver);
			}
		});
		return messages;
	}

	@Override
	public Messages addMessage(Messages message) {
		User sender = userManager.getUser(message.getSender().getName());
		message.getSender().setId(sender.getId());
		if (message.getReceiver() != null && !StringUtils.isEmpty(message.getReceiver().getName())) {
			User receiver = userManager.getUser(message.getReceiver().getName());
			if (receiver != null)
				message.getReceiver().setId(receiver.getId());
		} else {
			message.setReceiver(null);
		}
		return messagesRepo.save(message);
	}

}
