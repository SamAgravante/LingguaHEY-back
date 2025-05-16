package edu.cit.lingguahey.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.LessonActivityEntity;
import edu.cit.lingguahey.Entity.UserActivity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.ClassroomUserRepository;
import edu.cit.lingguahey.Repository.LessonActivityRepository;
import edu.cit.lingguahey.Repository.UserActivityRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.config.JwtService;
import edu.cit.lingguahey.token.Token;
import edu.cit.lingguahey.token.TokenRepository;
import edu.cit.lingguahey.token.TokenType;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ClassroomUserRepository classroomUserRepo;
    private final LessonActivityRepository activityRepo;
    private final UserActivityRepository userActivityRepo;

    public AuthenticationService(UserRepository repository, TokenRepository tokenRepository, 
        PasswordEncoder passwordEncoder, JwtService jwtService, 
        AuthenticationManager authenticationManager, ClassroomUserRepository classroomUserRepo,
        LessonActivityRepository activityRepo, UserActivityRepository userActivityRepo) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.classroomUserRepo = classroomUserRepo;
        this.activityRepo = activityRepo;
        this.userActivityRepo = userActivityRepo;
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
            .idNumber(request.getIdNumber())
            .totalPoints(request.getTotalPoints())
            .subscriptionStatus(request.isSubscriptionStatus())
            .role(request.getRole())
            .build();
        //assign activities to user
        //var allActivities = activityRepo.findAll();
        //user.setActivities(allActivities);
        
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
        //asssign activities on login
        assignNewActivitiesToUser(user);
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

    private void assignNewActivitiesToUser(UserEntity user) {
        var classroomUser = classroomUserRepo.findByUser_UserId(user.getUserId());
        if (classroomUser.isPresent()) {
            int classroomId = classroomUser.get().getClassroom().getClassroomID();
            var activities = activityRepo.findByLessonClassroom_ClassroomID(classroomId);
            for (LessonActivityEntity activity : activities) {
                boolean alreadyAssigned = userActivityRepo
                    .findByUser_UserIdAndActivity_ActivityId(user.getUserId(), activity.getActivityId())
                    .isPresent();
                if (!alreadyAssigned) {
                    UserActivity userActivity = new UserActivity(user, activity);
                    userActivityRepo.save(userActivity);
                }
            }
        }
    }

}
