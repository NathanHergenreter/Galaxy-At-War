package com.experiment3.comms.Configuration;

import com.experiment3.comms.Handlers.GameSignalHandler;
import com.experiment3.comms.Handlers.SocketMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public WebSocketHandler messageHandler(){
        return new SocketMessageHandler();
    }

    @Bean
    public WebSocketHandler gameHandler(){return new GameSignalHandler();}

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler(), "/socket-message");
        registry.addHandler(gameHandler(), "/game-action");
    }
}
