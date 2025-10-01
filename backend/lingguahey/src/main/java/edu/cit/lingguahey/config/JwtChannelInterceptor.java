package edu.cit.lingguahey.config;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import edu.cit.lingguahey.token.TokenRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtServ;
    private final UserDetailsService userDetailsServ;
    private final TokenRepository tokenRepo;
    
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
            if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
                System.err.println("WS Auth Error: Missing 'Authorization' header.");
                return message;
            }

            String authHeader = authorizationHeaders.get(0);
            if (!authHeader.startsWith("Bearer ")) {
                System.err.println("WS Auth Error: Invalid 'Authorization' header format.");
                return message;
            }

            String jwt = authHeader.substring(7);
            String userEmail = jwtServ.extractUsername(jwt);

            if (userEmail != null) {
                try {
                    UserDetails userDetails = this.userDetailsServ.loadUserByUsername(userEmail);

                    boolean isTokenValid = tokenRepo.findByToken(jwt)
                            .map(t -> !t.isExpired() && !t.isRevoked())
                            .orElse(false);

                    if (jwtServ.isTokenValid(jwt, userDetails) && isTokenValid) {
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        accessor.setUser(authToken);
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("WebSocket authenticated successfully for user: " + userEmail);

                    } else {
                        System.err.println("WS Auth Error: Token invalid or user not found.");
                    }
                } catch (Exception e) {
                    System.err.println("WS Auth Exception during token processing: " + e.getMessage());
                }
            }
        }

        return message;
    }
}
