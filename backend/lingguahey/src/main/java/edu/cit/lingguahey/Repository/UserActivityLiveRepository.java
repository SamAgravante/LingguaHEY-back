package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.lingguahey.Entity.UserActivityLive;
import edu.cit.lingguahey.model.UserActivityLiveProjection;

public interface UserActivityLiveRepository extends JpaRepository<UserActivityLive, Integer>{
    Optional<UserActivityLive> findByUser_UserIdAndActivity_ActivityId(int userId, int activityId);
    List<UserActivityLiveProjection> findByUser_UserId(int userId);
}
