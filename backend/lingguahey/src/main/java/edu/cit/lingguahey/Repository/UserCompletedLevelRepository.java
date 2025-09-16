package edu.cit.lingguahey.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.UserCompletedLevel;

@Repository
public interface UserCompletedLevelRepository extends JpaRepository<UserCompletedLevel, Integer> {
    boolean existsByUserUserIdAndLevelLevelId(int userId, int levelId);
    List<UserCompletedLevel> findByUserUserId(int userId);
}
