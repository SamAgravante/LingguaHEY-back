package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.BossForms;
import edu.cit.lingguahey.Entity.LevelMonster;

@Repository
public interface BossFormsRepository extends JpaRepository<BossForms, Integer> {
    void deleteAllByBossMonster(LevelMonster bossMonster);
    void deleteAllByMinionMonster(LevelMonster minionMonster);
}
