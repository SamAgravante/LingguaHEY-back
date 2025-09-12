package edu.cit.lingguahey.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Repository.MonsterRepository;
import edu.cit.lingguahey.model.MonsterCreateRequest;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MonsterService {
    
    @Autowired
    private MonsterRepository monsterRepo;

    // Create
    @Transactional
    public MonsterEntity createMonster(MonsterCreateRequest request) {
        if (request.getTagalogName() == null || request.getTagalogName().isEmpty() ||
            request.getEnglishName() == null || request.getEnglishName().isEmpty() ||
            request.getDescription() == null || request.getDescription().isEmpty() ||
            request.getImageData() == null || request.getImageData().length == 0) {
            throw new IllegalArgumentException("All fields, including image data, are required to create a monster.");
        }

        if (monsterRepo.findByTagalogName(request.getTagalogName()).isPresent()) {
            throw new IllegalArgumentException("A monster with this Tagalog name already exists.");
        }

        MonsterEntity monster = MonsterEntity.builder()
            .tagalogName(request.getTagalogName())
            .englishName(request.getEnglishName())
            .description(request.getDescription())
            .imageData(request.getImageData())
            .build();

        return monsterRepo.save(monster);
    }

    // Read all
    public List<MonsterEntity> getAllMonsters() {
        return monsterRepo.findAll();
    }

    // Read by id
    public MonsterEntity getMonsterById(int monsterId) {
        return monsterRepo.findById(monsterId)
            .orElseThrow(() -> new EntityNotFoundException("Monster not found with ID: " + monsterId));
    }

    // Update
    @Transactional
    public MonsterEntity editMonster(int monsterId, MonsterCreateRequest request) {
        MonsterEntity monsterToUpdate = monsterRepo.findById(monsterId)
            .orElseThrow(() -> new EntityNotFoundException("Monster not found with ID: " + monsterId));
        
        if (!monsterToUpdate.getTagalogName().equals(request.getTagalogName())) {
            Optional<MonsterEntity> existingMonster = monsterRepo.findByTagalogName(request.getTagalogName());
            if (existingMonster.isPresent() && existingMonster.get().getMonsterId() != monsterId) {
                throw new IllegalArgumentException("A monster with this Tagalog name already exists.");
            }
        }
        
        if (request.getTagalogName() == null || request.getTagalogName().isEmpty() ||
            request.getEnglishName() == null || request.getEnglishName().isEmpty() ||
            request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("All fields are required to edit a monster.");
        }

        monsterToUpdate.setTagalogName(request.getTagalogName());
        monsterToUpdate.setEnglishName(request.getEnglishName());
        monsterToUpdate.setDescription(request.getDescription());
        
        if (request.getImageData() != null && request.getImageData().length > 0) {
            monsterToUpdate.setImageData(request.getImageData());
        }

        return monsterRepo.save(monsterToUpdate);
    }

    // Delete a MonsterEntity by id
    public void deleteMonster(int monsterId) {
        if (!monsterRepo.existsById(monsterId)) {
            throw new EntityNotFoundException("Monster not found with ID: " + monsterId);
        }
        monsterRepo.deleteById(monsterId);
    }

}
