package edu.cit.lingguahey.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.BossForms;
import edu.cit.lingguahey.Entity.LevelEntity;
import edu.cit.lingguahey.Entity.LevelMonster;
import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Entity.MonsterType;
import edu.cit.lingguahey.Repository.BossFormsRepository;
import edu.cit.lingguahey.Repository.LevelRepository;
import edu.cit.lingguahey.Repository.MonsterRepository;
import edu.cit.lingguahey.model.LevelCreateRequest;
import edu.cit.lingguahey.model.LevelEditRequest;
import edu.cit.lingguahey.model.MonsterRequest;
import jakarta.persistence.EntityNotFoundException;

@Service
public class LevelService {

    @Autowired
    private LevelRepository levelRepo;

    @Autowired
    private MonsterRepository monsterRepo;

    @Autowired
    private BossFormsRepository bossFormsRepo;

    // Create
    @Transactional
    public LevelEntity createLevel(LevelCreateRequest request) {
        if (request.getLevelName() == null || request.getLevelName().isEmpty()) {
            throw new IllegalArgumentException("Level name cannot be empty.");
        }
        if (request.getMonsters() == null || request.getMonsters().isEmpty()) {
            throw new IllegalArgumentException("A level must have at least one enemy.");
        }

        LevelEntity level = new LevelEntity();
        level.setLevelName(request.getLevelName());
        level.setCoinsReward(request.getCoinsReward());
        level.setGemsReward(request.getGemsReward());

        List<LevelMonster> levelMonsters = new ArrayList<>();
        LevelMonster bossMonsterInstance = null;
        
        for (MonsterRequest monsterRequest : request.getMonsters()) {
            LevelMonster newLevelMonster;

            if (monsterRequest.getMonsterType() == MonsterType.BOSS) {
                if (bossMonsterInstance != null) {
                    throw new IllegalArgumentException("A level can only have one boss.");
                }
                
                if (monsterRequest.getBossFormsMinionIds() == null || monsterRequest.getBossFormsMinionIds().isEmpty()) {
                    throw new IllegalArgumentException("A boss must have at least one minion form.");
                }
                newLevelMonster = LevelMonster.builder()
                    .level(level)
                    .monster(null)
                    .monsterType(monsterRequest.getMonsterType())
                    .build();
                bossMonsterInstance = newLevelMonster;
            } else {
                MonsterEntity monster = monsterRepo.findById(monsterRequest.getMonsterId())
                    .orElseThrow(() -> new EntityNotFoundException("Monster not found with ID: " + monsterRequest.getMonsterId()));
                
                newLevelMonster = LevelMonster.builder()
                    .level(level)
                    .monster(monster)
                    .monsterType(monsterRequest.getMonsterType())
                    .build();
            }
            levelMonsters.add(newLevelMonster);
        }
        
        level.setLevelMonsters(levelMonsters);
        
        if (bossMonsterInstance != null) {
            List<Integer> bossFormsMinionIds = request.getMonsters().stream()
                .filter(m -> m.getMonsterType() == MonsterType.BOSS)
                .flatMap(m -> m.getBossFormsMinionIds().stream())
                .collect(Collectors.toList());

            List<BossForms> bossForms = new ArrayList<>();
            List<LevelMonster> availableMinions = new ArrayList<>(level.getLevelMonsters());

            LevelMonster persistedBoss = level.getLevelMonsters().stream()
                    .filter(lm -> lm.getMonsterType() == MonsterType.BOSS)
                    .findFirst().orElseThrow();
                    
            for (int i = 0; i < 3; i++) {
                Integer minionId = bossFormsMinionIds.get(i % bossFormsMinionIds.size());
                
                LevelMonster minion = availableMinions.stream()
                    .filter(lm -> lm.getMonster() != null && lm.getMonster().getMonsterId() == minionId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Minion not found in the monster list with ID: " + minionId));

                if (minion.getMonsterType() != MonsterType.MINION) {
                    throw new IllegalArgumentException("Boss forms must be of type MINION.");
                }
                
                bossForms.add(BossForms.builder()
                    .bossMonster(persistedBoss)
                    .minionMonster(minion)
                    .build());
            }
            
            persistedBoss.setMinionForms(bossForms);
        }
        
        LevelEntity persistedLevel = levelRepo.saveAndFlush(level);
        // Force a refresh from the database
        return levelRepo.findById(persistedLevel.getLevelId())
            .orElseThrow(() -> new EntityNotFoundException("Level not found after creation"));
    }

    // Read all
    public List<LevelEntity> getAllLevels() {
        return levelRepo.findAll();
    }

    // Read by id
    public LevelEntity getLevelById(int levelId) {
        return levelRepo.findById(levelId)
            .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + levelId));
    }

    // Update
    @Transactional
    public LevelEntity editLevel(int levelId, LevelEditRequest request) {
        LevelEntity levelToUpdate = levelRepo.findById(levelId)
            .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + levelId));

        if (request.getMonsters() == null || request.getMonsters().isEmpty()) {
            throw new IllegalArgumentException("A level must have at least one enemy.");
        }
        
        levelToUpdate.setCoinsReward(request.getCoinsReward());
        levelToUpdate.setGemsReward(request.getGemsReward());
        
        for (LevelMonster old : levelToUpdate.getLevelMonsters()) {
            if (old.getMonsterType() == MonsterType.BOSS) {
                bossFormsRepo.deleteAllByBossMonster(old);
            }
        }
        
        levelToUpdate.getLevelMonsters().clear();
        levelRepo.saveAndFlush(levelToUpdate);

        List<LevelMonster> newLevelMonsters = new ArrayList<>();
        LevelMonster bossMonsterInstance = null;

        for (MonsterRequest monsterRequest : request.getMonsters()) {
            if (monsterRequest.getMonsterType() == MonsterType.BOSS) {
                if (bossMonsterInstance != null) {
                    throw new IllegalArgumentException("A level can only have one boss.");
                }
                bossMonsterInstance = LevelMonster.builder()
                    .level(levelToUpdate)
                    .monster(null)
                    .monsterType(monsterRequest.getMonsterType())
                    .build();
                newLevelMonsters.add(bossMonsterInstance);
            } else {
                MonsterEntity monster = monsterRepo.findById(monsterRequest.getMonsterId())
                    .orElseThrow(() -> new EntityNotFoundException("Monster not found with ID: " + monsterRequest.getMonsterId()));
                newLevelMonsters.add(LevelMonster.builder()
                    .level(levelToUpdate)
                    .monster(monster)
                    .monsterType(monsterRequest.getMonsterType())
                    .build());
            }
        }

        levelToUpdate.getLevelMonsters().addAll(newLevelMonsters);
        
        if (bossMonsterInstance != null) {
            LevelMonster persistedBoss = newLevelMonsters.stream()
                .filter(lm -> lm.getMonsterType() == MonsterType.BOSS)
                .findFirst().orElseThrow();

            List<Integer> bossFormsMinionIds = request.getMonsters().stream()
                .filter(m -> m.getMonsterType() == MonsterType.BOSS)
                .flatMap(m -> m.getBossFormsMinionIds().stream())
                .collect(Collectors.toList());

            List<BossForms> bossForms = new ArrayList<>();
            for (Integer minionId : bossFormsMinionIds) {
                 LevelMonster persistedMinion = newLevelMonsters.stream()
                    .filter(lm -> lm.getMonster() != null && lm.getMonster().getMonsterId() == minionId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Minion not found: " + minionId));
                bossForms.add(BossForms.builder()
                    .bossMonster(persistedBoss)
                    .minionMonster(persistedMinion)
                    .build());
            }

            persistedBoss.setMinionForms(bossForms);

        }
        
        levelToUpdate = levelRepo.saveAndFlush(levelToUpdate);
        
        return levelRepo.findById(levelId).orElseThrow();
    }


    // Delete a LevelEntity by id
    @Transactional
    public void deleteLevel(int levelId) {
        LevelEntity level = levelRepo.findById(levelId)
            .orElseThrow(() -> new EntityNotFoundException("Level not found"));

        for (LevelMonster lm : level.getLevelMonsters()) {
            bossFormsRepo.deleteAllByBossMonster(lm);
            bossFormsRepo.deleteAllByMinionMonster(lm);
        }

        level.getLevelMonsters().clear();
        levelRepo.saveAndFlush(level);

        levelRepo.delete(level);
    }

}
