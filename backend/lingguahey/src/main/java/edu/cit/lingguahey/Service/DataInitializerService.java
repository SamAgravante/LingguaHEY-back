package edu.cit.lingguahey.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            // Put your image files in: src/main/resources/static/images/
            cosmeticsData.put("Basic Staff", new Object[]{Rarity.COMMON, "images/WeaponBasicStaff.png"});
            cosmeticsData.put("Druid Staff", new Object[]{Rarity.COMMON, "images/DruidStaff.png"});
            cosmeticsData.put("Holy Staff", new Object[]{Rarity.COMMON, "images/HolyStaff.png"});
            cosmeticsData.put("Ruby Staff", new Object[]{Rarity.COMMON, "images/RubyStaff.png"});
            cosmeticsData.put("Hellfire Staff", new Object[]{Rarity.RARE, "images/HellfireStaff.png"});
            cosmeticsData.put("Frost Staff", new Object[]{Rarity.RARE, "images/FrostStaff.png"});
            cosmeticsData.put("Staff of Life", new Object[]{Rarity.LEGENDARY, "images/StaffOfLife.png"});
            cosmeticsData.put("Staff of Primordial Fire", new Object[]{Rarity.LEGENDARY, "images/StaffOfPrimordialFire.png"});
            cosmeticsData.put("Dragon Void Staff", new Object[]{Rarity.MYTHIC, "images/DragonVoidStaff.png"});

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
        // Try classpath first (static/ is typical Spring Boot location)
        ClassPathResource resource = new ClassPathResource("static/" + path);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                return inputStream.readAllBytes();
            }
        }

        // Fallback: try without "static/" (if you placed directly under resources)
        resource = new ClassPathResource(path);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                return inputStream.readAllBytes();
            }
        }

        // Final fallback: try project file system location (useful during development)
        Path fsPath = Paths.get("src", "main", "resources", "static", path);
        if (Files.exists(fsPath)) {
            return Files.readAllBytes(fsPath);
        }

        System.err.println("Warning: Image file not found at " + path + ". Using placeholder image.");
        return createPlaceholderImage();
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
