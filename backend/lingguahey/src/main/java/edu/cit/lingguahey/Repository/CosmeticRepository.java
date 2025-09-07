package edu.cit.lingguahey.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import edu.cit.lingguahey.Entity.Rarity;

public interface CosmeticRepository extends JpaRepository<CosmeticEntity, Integer> {
    List<CosmeticEntity> findByRarity(Rarity rarity);
}
