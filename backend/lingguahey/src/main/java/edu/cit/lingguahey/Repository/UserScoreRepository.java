package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.UserScore;
import edu.cit.lingguahey.model.UserScoreProjection;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Integer> {
    Optional<UserScore> findByUser_UserIdAndQuestion_QuestionId(int userId, int questionId);
    List<UserScoreProjection> findByUser_UserId(int userId);
    List<UserScore> findByScoreEntity_ScoreId(int scoreId);

    @Query(value = """
            SELECT u.user_id AS user_UserId, u.first_name AS user_FirstName, u.last_name AS user_LastName, 
                SUM(us.score) AS score, 
                RANK() OVER (ORDER BY SUM(us.score) DESC) AS `rank`
            FROM user_scores us
            JOIN user_entity u ON us.user_id = u.user_id
            GROUP BY u.user_id, u.first_name, u.last_name
            ORDER BY `rank`
            """, nativeQuery = true)
    List<UserScoreProjection> getLeaderboardWithRank();
}
