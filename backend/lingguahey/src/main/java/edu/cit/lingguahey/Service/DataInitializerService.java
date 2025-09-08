package edu.cit.lingguahey.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Entity.Rarity;
import edu.cit.lingguahey.Repository.CosmeticRepository;
import jakarta.annotation.PostConstruct;

@Service
public class DataInitializerService {
    
    @Autowired
    private CosmeticRepository cosmeticRepo;

    @PostConstruct
    public void init() throws IOException {
        if (cosmeticRepo.count() == 0) {
            //Cosmetics Map
            Map<String, Object[]> cosmeticsData = new HashMap<>();
            cosmeticsData.put("Fireball FX", new Object[]{Rarity.COMMON, "images/fireball_fx.png"});
            cosmeticsData.put("Waterbolt FX", new Object[]{Rarity.COMMON, "images/waterbolt_fx.png"});
            cosmeticsData.put("Air Slash FX", new Object[]{Rarity.COMMON, "images/air_slash_fx.png"});
            cosmeticsData.put("Earthquake FX", new Object[]{Rarity.COMMON, "images/earthquake_fx.png"});
            cosmeticsData.put("Lightning Armor", new Object[]{Rarity.RARE, "images/lightning_armor.png"});
            cosmeticsData.put("Shadow Cloak", new Object[]{Rarity.RARE, "images/shadow_cloak.png"});
            cosmeticsData.put("Mystic Bow", new Object[]{Rarity.RARE, "images/mystic_bow.png"});
            cosmeticsData.put("Legendary Sword", new Object[]{Rarity.LEGENDARY, "images/legendary_sword.png"});
            cosmeticsData.put("Mythic Dragon Mount", new Object[]{Rarity.LEGENDARY, "images/mythic_dragon_mount.png"});
            cosmeticsData.put("Cosmic Phoenix", new Object[]{Rarity.MYTHIC, "images/cosmic_phoenix.png"});

            for (Map.Entry<String, Object[]> entry : cosmeticsData.entrySet()) {
                String name = entry.getKey();
                Rarity rarity = (Rarity) entry.getValue()[0];
                String imagePath = (String) entry.getValue()[1];

                byte[] imageData = loadImageAsBytes(imagePath);
                
                CosmeticEntity cosmetic = new CosmeticEntity(name, rarity, imageData);
                cosmeticRepo.save(cosmetic);
            }
        }
    }

    //Image converter
    private byte[] loadImageAsBytes(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            System.err.println("Warning: Image file not found at " + path + ". Using placeholder image.");
            return createPlaceholderImage();
        }
    
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    //Placeholder cosmetic image
    private byte[] createPlaceholderImage() {
        return new byte[] {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x48, (byte) 0x44, (byte) 0x52,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x08, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1F, (byte) 0x15, (byte) 0xC4,
            (byte) 0x89, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x49, (byte) 0x44, (byte) 0x41,
            (byte) 0x54, (byte) 0x78, (byte) 0x9C, (byte) 0x63, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0x0D, (byte) 0x0A, (byte) 0x2D, (byte) 0xB4, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x45, (byte) 0x4E, (byte) 0x44, (byte) 0xAE
        };
    }
}
