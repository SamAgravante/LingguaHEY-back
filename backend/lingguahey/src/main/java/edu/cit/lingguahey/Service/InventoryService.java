package edu.cit.lingguahey.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.CosmeticRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class InventoryService {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CosmeticRepository cosmeticRepo;

    // Grant user default cosmetic
    @Transactional
    public void grantDefaultCosmeticAndEquip(UserEntity user) {
        final String defaultCosmeticName = "Basic Staff";

        CosmeticEntity defaultCosmetic = cosmeticRepo.findByName(defaultCosmeticName)
            .orElseThrow(() -> new EntityNotFoundException("Default cosmetic '" + defaultCosmeticName + "' not found. Check DataInitializerService."));
        // Initialize inventory list
        List<CosmeticEntity> inventory = user.getInventory();
        if (inventory == null) {
            inventory = new ArrayList<>();
            user.setInventory(inventory);
        }

        if (!inventory.contains(defaultCosmetic)) {
            inventory.add(defaultCosmetic);
        }

        user.setEquippedCosmetic(defaultCosmetic);
        userRepo.save(user);
    }

    // Read Inventory by id
    public List<CosmeticEntity> getUserInventory(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return user.getInventory();
    }
    
    // Equip Cosmetic by id
    @Transactional
    public void equipCosmetic(int userId, int cosmeticId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        CosmeticEntity cosmetic = cosmeticRepo.findById(cosmeticId)
                .orElseThrow(() -> new EntityNotFoundException("Cosmetic not found with ID: " + cosmeticId));

        if (!user.getInventory().contains(cosmetic)) {
            throw new IllegalArgumentException("User does not own this cosmetic.");
        }

        user.setEquippedCosmetic(cosmetic);
        userRepo.save(user);
    }
}
