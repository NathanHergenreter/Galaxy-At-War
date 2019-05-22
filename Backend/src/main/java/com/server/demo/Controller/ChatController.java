package com.server.demo.Controller;

import com.server.demo.Entities.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// May want to do something like this
// https://stackoverflow.com/questions/28387157/multiple-rooms-in-spring-using-stomp


//@Autowired
//private SimpleMessagingTemplate brokerMessagingTemplate;

@Controller
@RequestMapping(value="/chat")
public class ChatController {

    private final SimpMessagingTemplate template;

    @Autowired
    ChatController(SimpMessagingTemplate template){
        this.template = template;
    }

    @GetMapping("/dm")
    public void directMessageUser(){

    }

    @GetMapping("/conversations")
    public List<String> getConversations(@RequestParam(name="username") String username){

        //TODO return list of all conversations for this user


        return null;
    }

    @DeleteMapping("/delete_msg")
    @ResponseBody
    public void deleteMessage(@RequestParam(name="convo_id") String convoId,
                              @RequestParam(name="msg_id") int msgId){

        //TODO delete a message from a conversation
    }
//
//    @MessageMapping("/send/message")
//    public void onReceiveMessage(String message){
//        this.template.convertAndSend("/chat", message);
//    }


/*
    @MessageMapping(".send")
    @SendTo("/public")
    public Chat sendMessage(@Payload Chat chat){
        return chat;
    }
*/

    @MessageMapping(".adduser")
    @SendTo("/public")
    public Chat addUser(@Payload Chat chat, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", chat.getSenderName());
        return chat;
    }

}
