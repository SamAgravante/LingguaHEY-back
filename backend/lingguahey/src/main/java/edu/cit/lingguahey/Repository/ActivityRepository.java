package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ActivityEntity;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Integer>{

}
