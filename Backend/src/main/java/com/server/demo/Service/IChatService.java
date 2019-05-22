package com.server.demo.Service;

import java.util.List;

import com.server.demo.Entities.Messages;

public interface IChatService {

	List<Messages> getAll();

	Messages addMessage(Messages message);

}
