package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Entity.Rarity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.CosmeticRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.model.GachaPullResponse;
import jakarta.persistence.EntityNotFoundException;

@Service
public class GachaSystemService {

    @Autowired
    private CosmeticRepository cosmeticRepo;
    
    @Autowired
    private UserRepository userRepo;

    // Gacha Pull
    @Transactional
    public GachaPullResponse performGachaPull(int userId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        final int pullCost = 100;
        final double rebatePercentage = 0.10;
        if (user.getGems() < pullCost) {
            throw new IllegalArgumentException("Not enough gems.");
        }

        user.setGems(user.getGems() - pullCost);

        Rarity pulledRarity = determineRarity();
        CosmeticEntity pulledCosmetic = getRandomCosmeticByRarity(pulledRarity);

        if (pulledCosmetic != null) {
            if (user.getInventory().contains(pulledCosmetic)) {
                int rebateAmount = (int) (pullCost * rebatePercentage);
                user.setGems(user.getGems() + rebateAmount);
                userRepo.save(user);
                System.out.println("User " + user.getUserId() + " pulled a duplicate. Received " + rebateAmount + " gems back.");
                return new GachaPullResponse("Duplicate", pulledCosmetic);
            } else {
                user.getInventory().add(pulledCosmetic);
                userRepo.save(user);
                System.out.println("User " + user.getUserId() + " pulled a new " + pulledCosmetic.getRarity() + " item: " + pulledCosmetic.getName());
                return new GachaPullResponse("Success", pulledCosmetic);
            }
        } else {
            return new GachaPullResponse("Failed to find a cosmetic.", null);
        }
    }

    // Gacha Rarity
    private Rarity determineRarity() {
        double chance = new Random().nextDouble() * 100;

        if (chance < Rarity.MYTHIC.getPullChance()) {
            return Rarity.MYTHIC;
        } else if (chance < Rarity.MYTHIC.getPullChance() + Rarity.LEGENDARY.getPullChance()) {
            return Rarity.LEGENDARY;
        } else if (chance < Rarity.MYTHIC.getPullChance() + Rarity.LEGENDARY.getPullChance() + Rarity.RARE.getPullChance()) {
            return Rarity.RARE;
        } else {
            return Rarity.COMMON;
        }
    }

    // Gacha RNG
    private CosmeticEntity getRandomCosmeticByRarity(Rarity rarity) {
        List<CosmeticEntity> pool = cosmeticRepo.findByRarity(rarity);
        if (pool.isEmpty()) {
            return null;
        }
        return pool.get(new Random().nextInt(pool.size()));
    }
}
