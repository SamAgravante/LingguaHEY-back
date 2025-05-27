package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ScoreEntity;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    @Query("SELECT SUM(s.score) FROM ScoreEntity s WHERE s.user.id = :userId")
    Integer getMaxPossibleScore(@Param("userId") int userId);
    
}
