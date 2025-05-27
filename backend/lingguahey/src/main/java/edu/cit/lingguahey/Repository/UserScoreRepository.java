package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.UserScore;
import edu.cit.lingguahey.model.LeaderboardEntry;
import edu.cit.lingguahey.model.UserScoreProjection;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Integer> {
    Optional<UserScore> findByUser_UserIdAndQuestion_QuestionId(int userId, int questionId);
    List<UserScore> findByUser_UserId(int userId);
    List<UserScore> findByScoreEntity_ScoreId(int scoreId);
    List<UserScore> findByQuestion_QuestionId(int questionId);
    List<UserScore> findByQuestion_LiveActivity_ActivityId(int liveActivityId);

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

    @Query("""
        SELECT 
            us.user.userId as userId,
            us.user.firstName as firstName,
            us.user.lastName as lastName,
            us.user.profilePic as profilePic,
            SUM(us.score) as totalScore
        FROM UserScore us
        WHERE us.question.liveActivity.activityId = :activityId
        GROUP BY us.user.userId, us.user.firstName, us.user.lastName
        ORDER BY totalScore DESC
    """)
    List<LeaderboardEntry> findLeaderboardByLiveActivity(@Param("activityId") int activityId);

    @Query("SELECT us.score FROM UserScore us WHERE us.question.liveActivity.id = :activityId")
    List<Integer> findScoresByActivityId(@Param("activityId") int activityId);
}
