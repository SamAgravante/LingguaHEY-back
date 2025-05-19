package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.model.UserActivityLiveProjection;

@Repository
public interface UserActivityLiveRepository extends JpaRepository<UserActivityLive, Integer>{
    Optional<UserActivityLive> findByUser_UserIdAndActivity_ActivityId(int userId, int activityId);
    List<UserActivityLiveProjection> findByUser_UserId(int userId);
    List<UserActivityLive> findByActivity_ActivityId(int activityId);
    List<UserActivityLive> findByActivity_ActivityIdAndInLobby(int activityId, boolean inLobby);
    Optional<UserActivityLive> findByUser_UserIdAndActivity_ActivityIdAndInLobby(int userId, int activityId, boolean inLobby);
}
