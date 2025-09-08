package edu.cit.lingguahey.Service;

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
}
