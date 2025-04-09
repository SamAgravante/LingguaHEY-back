package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.UserEntity;
import edu.cit.alibata.Repository.UserRepository;
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
            if (!passwordEncoder.matches(newUser.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }
            user.setSubscriptionStatus(newUser.getSubscriptionStatus());
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
}
