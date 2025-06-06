package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Create
    public UserEntity postUserEntity(UserEntity user){
        if (user.getSubscriptionStatus() == false){
            user.setSubscriptionStatus(false);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    // Read All Users
    public List<UserEntity> getAllUserEntity(){
        return userRepo.findAll();
    }

    // Read Single User
    public UserEntity getUserEntity(int userId){
        return userRepo.findById(userId).get();
    }

    // Update
    @Transactional
    public UserEntity putUserEntity(int userId, UserEntity newUser){
        try {
            UserEntity user = userRepo.findById(userId).get();

            userRepo.findByEmail(newUser.getEmail()).ifPresent(existingUser ->{
                if (existingUser.getUserId() != userId) {
                    throw new IllegalArgumentException("Email already in use");
                }
            });

            user.setFirstName(newUser.getFirstName());
            user.setMiddleName(newUser.getMiddleName());
            user.setLastName(newUser.getLastName());
            user.setEmail(newUser.getEmail());
            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                if (!passwordEncoder.matches(newUser.getPassword(), user.getPassword()) && !newUser.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                }
            }
            user.setIdNumber(newUser.getIdNumber());
            user.setTotalPoints(newUser.getTotalPoints());
            user.setSubscriptionStatus(newUser.getSubscriptionStatus());
            user.setProfilePic(newUser.getProfilePic());
            return userRepo.save(user);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("User "+ userId + "not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteUserEntity(int userId){
        if (userRepo.findById(userId) != null){
            userRepo.deleteById(userId);
            return "User " +userId+ "Deleted Successfully!";
        } else {
            return "User " +userId+ "not found!";
        }
    }

    // Update Subscription Status
    public void updateSubscriptionStatus(Integer userId, boolean subscriptionStatus) {
        UserEntity user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setSubscriptionStatus(subscriptionStatus);
        userRepo.save(user);
    }

    // Password Reset
    public void resetPassword(int userId, String oldPassword, String newPassword) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found!"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
