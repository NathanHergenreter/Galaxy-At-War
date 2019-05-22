package com.server.demo.Configuration;

        import com.server.demo.Handlers.GameConnectHandler;
        import com.server.demo.Handlers.GameDisconnectHandler;
        import com.server.demo.Handlers.WebSocketConnectHandler;
        import com.server.demo.Handlers.WebSocketDisconnectHandler;
        import com.server.demo.Repositories.ActiveGameSocketUserRepository;
        import com.server.demo.Repositories.ActiveWebSocketUserRepository;
        import org.springframework.context.ApplicationListener;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.messaging.simp.SimpMessageSendingOperations;
        import org.springframework.session.ExpiringSession;
        import org.springframework.web.socket.messaging.SessionConnectEvent;

@Configuration
public class GameSocketHandlerConfig <S extends ExpiringSession> {

    @Bean
    public GameConnectHandler<S> gameConnectHandler(SimpMessageSendingOperations messagingTemplate,
                                                    ActiveGameSocketUserRepository repository){
        return new GameConnectHandler<S>(messagingTemplate, repository);
    }

    @Bean
    public GameDisconnectHandler<S> gameDisconnectHandler(ActiveGameSocketUserRepository repository) {
        return new GameDisconnectHandler<S>(repository);
    }
}
