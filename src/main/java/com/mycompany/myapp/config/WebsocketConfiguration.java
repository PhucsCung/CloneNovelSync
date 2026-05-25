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
        // 1. CỬA CHÍNH: Dành cho WebSocket thuần túy (ĐƯỜNG DẪN MỚI)
        registry.addEndpoint("/websocket/stomp").setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000");

        // 2. CỬA PHỤ: Dành cho SockJS (Đường dẫn cũ)
        registry.addEndpoint("/websocket/tracker").setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000").withSockJS();
    }
}
