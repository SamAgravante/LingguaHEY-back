package edu.cit.lingguahey.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.token.TokenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRepository tokenRepo;
    
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

            if (newUser.getFirstName() != null && !newUser.getFirstName().trim().isEmpty()) {
                user.setFirstName(newUser.getFirstName());
            }
            user.setMiddleName(newUser.getMiddleName());
            if (newUser.getLastName() != null && !newUser.getLastName().trim().isEmpty()) {
                user.setLastName(newUser.getLastName());
            }
            //user.setEmail(newUser.getEmail());
            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                if (!passwordEncoder.matches(newUser.getPassword(), user.getPassword()) && !newUser.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                }
            }
            user.setIdNumber(newUser.getIdNumber());
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
            return "User " +userId+ " Deleted Successfully!";
        } else {
            return "User " +userId+ " not found!";
        }
    }

    // Subscritpion
    @Transactional
    public void updateSubscriptionStatus(Integer userId, boolean subscriptionStatus, String subscriptionType) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setSubscriptionStatus(subscriptionStatus);

        if (subscriptionStatus) {
            Date startDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            try {
                UserEntity.SubscriptionType type = UserEntity.SubscriptionType.valueOf(subscriptionType);
                user.setSubscriptionType(type);
                user.setSubscriptionStartDate(startDate);
                
                if (type == UserEntity.SubscriptionType.PREMIUM_PLUS) {
                    calendar.add(Calendar.MONTH, 6);
                } else if (type == UserEntity.SubscriptionType.PREMIUM) {
                    calendar.add(Calendar.MONTH, 1);
                }
                user.setSubscriptionEndDate(calendar.getTime());

                // Logging to verify the dates are being set correctly
                System.out.println("Setting subscription for user " + userId);
                System.out.println("Type: " + type);
                System.out.println("Start date: " + startDate);
                System.out.println("End date: " + calendar.getTime());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid subscription type: " + subscriptionType);
            }
        } else {
            user.setSubscriptionType(UserEntity.SubscriptionType.FREE);
            user.setSubscriptionStartDate(null);
            user.setSubscriptionEndDate(null);
        }
        UserEntity savedUser = userRepo.save(user);
        System.out.println("Saved user subscription status: " + savedUser.getSubscriptionStatus());
        System.out.println("Saved user subscription type: " + savedUser.getSubscriptionType());
        System.out.println("Saved user subscription start date: " + savedUser.getSubscriptionStartDate());
        System.out.println("Saved user subscription end date: " + savedUser.getSubscriptionEndDate());
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

    // Get the count of active tokens
    public long getActiveTokenCount() {
        return tokenRepo.countAllValidTokens();
    }

    // Life
    @Transactional
    public void deductLife(int userId) {
        UserEntity user = userRepo.findById(userId).get();
        if (user.getLives() > 0) {
            user.setLives(user.getLives() - 1);
        }
        userRepo.save(user);
    }

    // Shield
    @Transactional
    public void consumeShield(int userId) {
        UserEntity user = userRepo.findById(userId).get();
        if (user.getShield() > 0) {
            user.setShield(user.getShield() - 1);
        }
        userRepo.save(user);
    }

    // Level Rewards
    @Transactional
    public void rewardUser(int userId, int coins, int gems) {
        UserEntity user = userRepo.findById(userId).get();
        user.setCoins(user.getCoins() + coins);
        user.setGems(user.getGems() + gems);
        userRepo.save(user);
    }

    // Skip Counter
    @Transactional
    public void consumeSkip(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getSkipsLeft() > 0) {
            user.setSkipsLeft(user.getSkipsLeft() - 1);
            userRepo.save(user);
        }
    }

    // Get Equipped Cosmetic
    public CosmeticEntity getEquippedCosmetic(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getEquippedCosmetic() != null) {
            return user.getEquippedCosmetic();
        } else {
            return null;
        }
    }
}
