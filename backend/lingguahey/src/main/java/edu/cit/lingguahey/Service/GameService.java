package edu.cit.lingguahey.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.lingguahey.Entity.BossForms;
import edu.cit.lingguahey.Entity.LevelEntity;
import edu.cit.lingguahey.Entity.LevelMonster;
import edu.cit.lingguahey.Entity.MonsterEntity;
import edu.cit.lingguahey.Entity.MonsterType;
import edu.cit.lingguahey.Entity.PotionType;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.LevelRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import edu.cit.lingguahey.model.GameSession;
import edu.cit.lingguahey.model.GameSessionResponse;
import edu.cit.lingguahey.model.GuessResponse;
import edu.cit.lingguahey.model.MonsterResponse;
import edu.cit.lingguahey.model.PotionUseResponse;
import jakarta.persistence.EntityNotFoundException;

@Service
public class GameService {

    @Autowired
    private LevelRepository levelRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userServ;

    @Autowired
    private PotionShopService potionShopServ;

    @Autowired
    private LevelService levelServ;

    private GameSession currentGameSession;
    private boolean canUsePotion = true;

    // Start new Game
    @Transactional
    public void initializeGame(int levelId, int userId) {
        LevelEntity level = levelRepo.findById(levelId)
                .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + levelId));
        
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        // Reset stats for new game
        user.setLives(4);
        user.setShield(0);
        user.setSkipsLeft(1);
        userRepo.save(user);

        if (user.getLives() <= 0) {
            throw new IllegalStateException("Game over. User has no lives left.");
        }

        List<LevelMonster> monsters = new java.util.ArrayList<>(level.getLevelMonsters());
        //Collections.shuffle(shuffledMonsters);
        loadMonsterData(monsters);

        this.currentGameSession = new GameSession();
        this.currentGameSession.setLevelId(levelId);
        this.currentGameSession.setUserId(userId);
        this.currentGameSession.setMonsters(monsters);
        this.currentGameSession.setLives(user.getLives());
        this.currentGameSession.setSkipsLeft(user.getSkipsLeft());
        this.currentGameSession.setShield(user.getShield());
        this.currentGameSession.setCurrentMonsterIndex(0);
        this.currentGameSession.setClearedMonstersCount(0);
        this.currentGameSession.setGameOver(false);
        this.currentGameSession.setBossEncounter(false);
        this.currentGameSession.setBossFormsMinionIds(null);
        this.canUsePotion = true;
    }

    public GameSessionResponse getCurrentGameSessionResponse() {
        if (currentGameSession == null) {
            throw new IllegalStateException("No active game session. Please start a new game.");
        }
        
        GameSessionResponse response = new GameSessionResponse();
        response.setLevelId(currentGameSession.getLevelId());
        response.setUserId(currentGameSession.getUserId());
        
        response.setMonsters(
            currentGameSession.getMonsters().stream()
                .map(levelMonster -> {
                    if (levelMonster.getMonsterType() == MonsterType.BOSS) {
                        return new MonsterResponse(
                            -1, // Placeholder ID for the boss
                            "BOSS",
                            "Defeat the minions to win!",
                            "This is a boss monster. Prepare for a wave of minions.",
                            null,
                            null
                        );
                    } else {
                        return new MonsterResponse(
                            levelMonster.getMonster().getMonsterId(),
                            levelMonster.getMonster().getTagalogName(),
                            levelMonster.getMonster().getEnglishName(),
                            levelMonster.getMonster().getDescription(),
                            levelMonster.getMonster().getImageData(),
                            null
                        );
                    }
                })
                .collect(Collectors.toList())
        );

        response.setLives(currentGameSession.getLives());
        response.setSkipsLeft(currentGameSession.getSkipsLeft());
        response.setShield(currentGameSession.getShield());
        response.setCurrentMonsterIndex(currentGameSession.getCurrentMonsterIndex());
        response.setClearedMonstersCount(currentGameSession.getClearedMonstersCount());
        response.setGameOver(currentGameSession.isGameOver());

        return response;
    }

    // Read Current Monster
    @Transactional
    public MonsterResponse getCurrentMonster() {
        if (currentGameSession == null || currentGameSession.isGameOver()) {
            throw new IllegalStateException("Game over or not started.");
        }

        LevelMonster currentLevelMonster = currentGameSession.getMonsters()
                .get(currentGameSession.getCurrentMonsterIndex());

        if (currentLevelMonster.getMonsterType() == MonsterType.BOSS && !currentGameSession.isBossEncounter()) {
            loadMonsterData(Collections.singletonList(currentLevelMonster));
            setupBossEncounter(currentLevelMonster);
        }

        MonsterEntity monsterToDisplay;

        if (currentGameSession.isBossEncounter()) {
            int currentMinionId = currentGameSession.getBossFormsMinionIds()
                    .get(currentGameSession.getCurrentBossFormIndex());

            LevelMonster minionLevelMonster = currentGameSession.getMonsters().stream()
                    .filter(lm -> lm.getMonster() != null && lm.getMonster().getMonsterId() == currentMinionId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Boss minion not found."));

            monsterToDisplay = minionLevelMonster.getMonster();

        } else {
            monsterToDisplay = currentLevelMonster.getMonster();
        }

        String tagalogName = monsterToDisplay.getTagalogName();
        List<Character> jumbledLetters = generateJumbledLetters(tagalogName);

        return new MonsterResponse(
                monsterToDisplay.getMonsterId(),
                null,
                monsterToDisplay.getEnglishName(),
                monsterToDisplay.getDescription(),
                monsterToDisplay.getImageData(),
                jumbledLetters
        );
    }

    // Process Guess
    @Transactional
    public GuessResponse processGuess(String guessedName) {
        if (currentGameSession == null || currentGameSession.isGameOver()) {
            throw new IllegalStateException("Game over or not started.");
        }

        UserEntity user = userRepo.findById(currentGameSession.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found for active session."));
        currentGameSession.setLives(user.getLives());
        currentGameSession.setShield(user.getShield());
        currentGameSession.setSkipsLeft(user.getSkipsLeft());

        LevelMonster currentLevelMonster;
        if (currentGameSession.isBossEncounter()) {
            int currentMinionId = currentGameSession.getBossFormsMinionIds()
                .get(currentGameSession.getCurrentBossFormIndex());
            currentLevelMonster = currentGameSession.getMonsters().stream()
                    .filter(lm -> lm.getMonsterType() == MonsterType.MINION && lm.getMonster().getMonsterId() == currentMinionId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Boss minion not found."));
        } else {
            currentLevelMonster = currentGameSession.getMonsters()
                .get(currentGameSession.getCurrentMonsterIndex());
        }
        
        String correctAnswer = currentLevelMonster.getMonster().getTagalogName();
        boolean isCorrect = guessedName.trim().equalsIgnoreCase(correctAnswer.trim());
        String feedback = "";

        if (isCorrect) {
            if (currentGameSession.isBossEncounter()) {
                currentGameSession.setCurrentBossFormIndex(currentGameSession.getCurrentBossFormIndex() + 1);
                if (currentGameSession.getCurrentBossFormIndex() >= currentGameSession.getBossFormsMinionIds().size()) {
                    currentGameSession.setBossEncounter(false);
                    currentGameSession.setClearedMonstersCount(currentGameSession.getClearedMonstersCount() + 1);
                    currentGameSession.setCurrentMonsterIndex(currentGameSession.getCurrentMonsterIndex() + 1);
                    feedback = "Boss defeated! Level up!";
                } else {
                    feedback = "Correct! Boss form defeated. One down, two to go.";
                }
            } else {
                currentGameSession.setClearedMonstersCount(currentGameSession.getClearedMonstersCount() + 1);
                currentGameSession.setCurrentMonsterIndex(currentGameSession.getCurrentMonsterIndex() + 1);
                feedback = "Correct! The monster is defeated.";
            }
        } else {
            if (currentGameSession.getShield() > 0) {
                userServ.consumeShield(currentGameSession.getUserId());
                currentGameSession.setShield(currentGameSession.getShield() - 1);
                feedback = "Shield active! Your shield absorbed the damage.";
            } else {
                userServ.deductLife(currentGameSession.getUserId());
                currentGameSession.setLives(currentGameSession.getLives() - 1);
                feedback = "Incorrect. Try again!";
            }
        }
        
        if (currentGameSession.getLives() <= 0) {
            endGame(false);
            feedback = "Game over. You have no hearts left. Returning to level selection.";
        } else if (currentGameSession.getCurrentMonsterIndex() >= currentGameSession.getMonsters().size() && !currentGameSession.isBossEncounter()) {
            endGame(true);
            feedback = "Level cleared! You have defeated all monsters.";
        }
        
        this.canUsePotion = true;
        
        return new GuessResponse(
            isCorrect,
            feedback,
            currentGameSession.getLives(),
            currentGameSession.isGameOver(),
            isCorrect ? correctAnswer : null
        );
    }

    // Use Potion with turn-based logic
    public PotionUseResponse usePotion(int userId, PotionType potionType) {
        if (!canUsePotion) {
            throw new IllegalStateException("A potion can only be used once per guess.");
        }

        String feedback = "Potion used successfully!"; // default message

        if (potionType == PotionType.SKIP) {
            if (currentGameSession.getSkipsLeft() <= 0) {
                throw new IllegalStateException("You do not have a skip available for this level.");
            }
        
            potionShopServ.usePotion(userId, potionType);
            performSkip();
            userServ.consumeSkip(userId);
            currentGameSession.setSkipsLeft(0);
            feedback = "Monster skipped successfully!";
            
        } else {
            potionShopServ.usePotion(userId, potionType);
        }
        
        // update current game session
        UserEntity updatedUser = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        currentGameSession.setLives(updatedUser.getLives());
        currentGameSession.setShield(updatedUser.getShield());

        this.canUsePotion = false;

        boolean isLevelCleared = false;
        if (currentGameSession.getLives() <= 0) {
            endGame(false);
            feedback = "Game over. You have no hearts left. Returning to level selection.";
        } else if (currentGameSession.getCurrentMonsterIndex() >= currentGameSession.getMonsters().size() && !currentGameSession.isBossEncounter()) {
            endGame(true);
            isLevelCleared = true;
            feedback = "Level cleared with a skip!";
        }

        return new PotionUseResponse(
            feedback,
            updatedUser.getLives(),
            updatedUser.getShield(),
            updatedUser.getSkipsLeft(),
            isLevelCleared
        );
    }

    // End Game
    private void endGame(boolean isSuccess) {
        currentGameSession.setGameOver(true);
        if (isSuccess) {
            if (levelServ.isLevelCompletedByUser(currentGameSession.getUserId(), currentGameSession.getLevelId())) {
                System.out.println("User has already completed this level. No rewards given.");
                return;
            }

            LevelEntity level = levelRepo.findById(currentGameSession.getLevelId())
                    .orElseThrow(() -> new EntityNotFoundException("Level not found with ID: " + currentGameSession.getLevelId()));
            
            int coins = level.getCoinsReward();
            int gems = level.getGemsReward();
            
            userServ.rewardUser(currentGameSession.getUserId(), coins, gems);
            levelServ.completeLevel(currentGameSession.getUserId(), currentGameSession.getLevelId());
        }
    }

    // Generate a 2x7 grid of jumbled letters.
    private List<Character> generateJumbledLetters(String correctName) {
        List<Character> puzzleLetters = correctName.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());

        Random random = new Random();
        while (puzzleLetters.size() < 14) {
            char randomChar = (char) ('A' + random.nextInt(26));
            puzzleLetters.add(randomChar);
        }

        Collections.shuffle(puzzleLetters);

        return puzzleLetters;
    }

    // Helper function for bosses
    private void setupBossEncounter(LevelMonster bossContainer) {
        currentGameSession.setBossEncounter(true);
        currentGameSession.setBossFormsMinionIds(
                bossContainer.getMinionForms().stream()
                        .map(BossForms::getMinionMonster)
                        .map(LevelMonster::getMonster)
                        .map(MonsterEntity::getMonsterId)
                        .collect(Collectors.toList())
        );
        currentGameSession.setCurrentBossFormIndex(0);
    }

    // Helper method to perform the skip action
    private void performSkip() {
        String feedback;
        if (currentGameSession.isBossEncounter()) {
            currentGameSession.setCurrentBossFormIndex(currentGameSession.getCurrentBossFormIndex() + 1);
            if (currentGameSession.getCurrentBossFormIndex() >= currentGameSession.getBossFormsMinionIds().size()) {
                currentGameSession.setBossEncounter(false);
                currentGameSession.setClearedMonstersCount(currentGameSession.getClearedMonstersCount() + 1);
                currentGameSession.setCurrentMonsterIndex(currentGameSession.getCurrentMonsterIndex() + 1);
                feedback = "Boss defeated with a skip! Level up!";
            } else {
                feedback = "Minion skipped! Moving on to the next boss form.";
            }
        } else {
            currentGameSession.setClearedMonstersCount(currentGameSession.getClearedMonstersCount() + 1);
            currentGameSession.setCurrentMonsterIndex(currentGameSession.getCurrentMonsterIndex() + 1);
            feedback = "Monster skipped successfully!";
        }
        System.out.println(feedback); // Debug logs
    }

    // Load in lazy-loaded monster data
    private void loadMonsterData(List<LevelMonster> monsters) {
        for (LevelMonster levelMonster : monsters) {
            MonsterEntity monster = levelMonster.getMonster();
            if (monster != null) {
                monster.getTagalogName();
            }
            
            if (levelMonster.getMonsterType() == MonsterType.BOSS) {
                levelMonster.getMinionForms().size();
            }
        }
    }
}
