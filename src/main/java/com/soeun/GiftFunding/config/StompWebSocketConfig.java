package com.soeun.GiftFunding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/stomp/chat")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomPrincipalHandshakeHandler());
             //   .withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            //클라이언트 -> 서버로 발행하는 메세지에 대한 endpoint 설정
            registry.setApplicationDestinationPrefixes("/pub");

            //서버 -> 클라이언트로 발행하는 메세지에 대한 endpoint 설정
            registry.enableSimpleBroker("/sub");
        }
    }
}