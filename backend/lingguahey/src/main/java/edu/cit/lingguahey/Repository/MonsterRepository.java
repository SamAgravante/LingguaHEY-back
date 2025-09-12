package edu.cit.lingguahey.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.MonsterEntity;

@Repository
public interface MonsterRepository extends JpaRepository<MonsterEntity, Integer> {
    Optional<MonsterEntity> findByTagalogName(String tagalogName);
}
