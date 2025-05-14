package com.example.linelessbackend.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import com.example.linelessbackend.security.JwtUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import io.jsonwebtoken.Claims;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

@Component
public class WebSocketAuthHandler implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthHandler(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Get token from Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            String token = null;
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            if (token != null) {
                String username = jwtUtils.extractUsername(token);
                
                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtils.validateToken(token, userDetails)) {
                        // Extract role from token
                        Claims claims = jwtUtils.extractAllClaims(token);
                        String role = claims.get("role", String.class);
                        
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));
                        accessor.setUser(auth);
                    }
                }
            }
        }
        return message;
    }
} 