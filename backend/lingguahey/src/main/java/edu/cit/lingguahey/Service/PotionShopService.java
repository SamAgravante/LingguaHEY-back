package edu.cit.lingguahey.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.PotionType;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PotionShopService {
    
    @Autowired
    private UserRepository userRepo;

    final int maxLives = 4;

    // Buy Potion
    @Transactional
    public void buyPotion(int userId, PotionType potionType, int cost) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (user.getCoins() < cost) {
            throw new IllegalArgumentException("Not enough coins to buy this potion.");
        }

        user.setCoins(user.getCoins() - cost);
        user.getPotions().put(potionType.name(), user.getPotions().getOrDefault(potionType.name(), 0) + 1);
        
        userRepo.save(user);
    }

    // Get All Potions for a user
    public Map<String, Integer> getPotions(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return user.getPotions();
    }

    // Use Potion
    @Transactional
    public void usePotion(int userId, PotionType potionType) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Map<String, Integer> potions = user.getPotions();
        int currentQuantity = potions.getOrDefault(potionType.name(), 0);

        if (currentQuantity <= 0) {
            throw new IllegalArgumentException("User does not have any " + potionType.name() + " potions.");
        }

        switch (potionType) {
            case HEALTH:
                if (user.getLives() >= maxLives) {
                    throw new IllegalStateException("Cannot use a health potion when health is full.");
                }
                user.setLives(user.getLives() + 1);
                break;
            case SHIELD:
                if (user.getShield() > 0) {
                    throw new IllegalStateException("Cannot use a shield potion when a shield is already active.");
                }
                user.setShield(user.getShield() + 1);
                break;
            case SKIP:
                user.setSkipsLeft(0);
                break;
            default:
                throw new IllegalArgumentException("Invalid potion type: " + potionType.name());
        }

        potions.put(potionType.name(), currentQuantity - 1);
        
        userRepo.save(user);
    }
}
