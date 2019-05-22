package com.ws.demo.Controller;

import com.ws.demo.Model.SocketModel;
import com.ws.demo.Model.SocketReceiveModel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class StompController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @MessageMapping("/socket/send")
    @SendTo("/socket/receive")
    public SocketReceiveModel socketReceive(SocketModel socketModel) throws Exception{
        Thread.sleep(1000);
        return new SocketReceiveModel("Received: " + HtmlUtils.htmlEscape(socketModel.getName()));
    }
}
