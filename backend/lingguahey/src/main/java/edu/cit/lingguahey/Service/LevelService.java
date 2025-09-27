package edu.cit.lingguahey.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.BossForms;
import edu.cit.lingguahey.Entity.LevelEntity;
import edu.cit.lingguahey.Entity.LevelMonster;
import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Entity.MonsterType;
import edu.cit.lingguahey.Entity.UserCompletedLevel;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.BossFormsRepository;
import edu.cit.lingguahey.Repository.LevelRepository;
import edu.cit.lingguahey.Repository.MonsterRepository;
import edu.cit.lingguahey.Repository.UserCompletedLevelRepository;
import edu.cit.lingguahey.Repository.UserRepository;
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

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private UserCompletedLevelRepository userCompletedLevelRepo;

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
        
        levelToUpdate.setLevelName(request.getLevelName());
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

    // Mark a level as completed for a user by id
    @Transactional
    public UserCompletedLevel completeLevel(int userId, int levelId) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        LevelEntity level = levelRepo.findById(levelId)
            .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + levelId));
        
        if (userCompletedLevelRepo.existsByUserUserIdAndLevelLevelId(userId, levelId)) {
            throw new IllegalArgumentException("Level " + levelId + " is already completed by user " + userId + ".");
        }
        
        UserCompletedLevel completedLevel = new UserCompletedLevel();
        completedLevel.setUser(user);
        completedLevel.setLevel(level);
        
        return userCompletedLevelRepo.save(completedLevel);
    }

    // Get all completed levels for a user by id
    @Transactional(readOnly = true)
    public List<LevelEntity> getCompletedLevelsForUser(int userId) {
        @SuppressWarnings("unused")
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            
        return userCompletedLevelRepo.findByUserUserId(userId).stream()
                .map(UserCompletedLevel::getLevel)
                .collect(Collectors.toList());
    }

    // Get a list of unique monsters for a level preview
    @Transactional(readOnly = true)
    public Set<MonsterEntity> getUniqueMonstersForLevelPreview(int levelId) {
        LevelEntity level = levelRepo.findById(levelId)
            .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + levelId));

        Set<MonsterEntity> uniqueMonsters = new HashSet<>();

        level.getLevelMonsters().stream()
            .filter(levelMonster -> levelMonster.getMonster() != null)
            .forEach(levelMonster -> uniqueMonsters.add(levelMonster.getMonster()));

        level.getLevelMonsters().stream()
            .filter(levelMonster -> levelMonster.getMonsterType() == MonsterType.BOSS)
            .flatMap(levelMonster -> levelMonster.getMinionForms().stream())
            .filter(bossForm -> bossForm.getMinionMonster() != null && bossForm.getMinionMonster().getMonster() != null)
            .forEach(bossForm -> uniqueMonsters.add(bossForm.getMinionMonster().getMonster()));
            
        return uniqueMonsters;
    }

    // Check for completed level by user id
    @Transactional(readOnly = true)
    public boolean isLevelCompletedByUser(int userId, int levelId) {
        return userCompletedLevelRepo.existsByUserUserIdAndLevelLevelId(userId, levelId);
    }

}
