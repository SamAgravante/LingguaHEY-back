package edu.cit.alibata.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.UserEntity;
import edu.cit.alibata.Repository.UserRepository;
import edu.cit.alibata.config.JwtService;
import edu.cit.alibata.token.Token;
import edu.cit.alibata.token.TokenRepository;
import edu.cit.alibata.token.TokenType;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    //register
    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        var user = UserEntity.builder()
            .firstName(request.getFirstName())
            .middleName(request.getMiddleName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .subscriptionStatus(request.isSubscriptionStatus())
            .role(request.getRole())
            .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        //save token to db for logout
        saveUserToken(savedUser, jwtToken);
        //
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }
    
    //login
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );
        var user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        //save token to db for logout
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        //
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    //save token to db
    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .revoked(false)
            .expired(false)
            .build();
        tokenRepository.save(token);
    }

}
