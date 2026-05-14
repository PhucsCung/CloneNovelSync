package com.mycompany.myapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // prefix cho tin nhắn gửi từ server xuống client
        config.enableSimpleBroker("/topic", "/queue");
        // prefix cho tin nhắn client gửi lên server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để client kết nối qua thư viện SockJS
        registry.addEndpoint("/websocket/tracker")
            .setAllowedOrigins("*")
            .withSockJS();
    }
}
