package edu.cit.lingguahey.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByVerificationToken(String verificationToken);

}
