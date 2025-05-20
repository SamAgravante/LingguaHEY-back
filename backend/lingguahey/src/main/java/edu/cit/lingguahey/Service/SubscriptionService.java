package edu.cit.lingguahey.Service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Entity.UserEntity.SubscriptionType;
import edu.cit.lingguahey.Repository.UserRepository;

@Service
public class SubscriptionService {
    @Autowired
    private UserRepository userRepository;

    public void updateSubscription(
        Integer userId, 
        SubscriptionType type, 
        boolean status
    ) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        
        user.setSubscriptionType(type);
        user.setSubscriptionStatus(status);
        user.setSubscriptionStartDate(new Date());
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (type == SubscriptionType.PREMIUM_PLUS) {
            calendar.add(Calendar.MONTH, 6);
        } else if (type == SubscriptionType.PREMIUM) {
            calendar.add(Calendar.MONTH, 1);
        }
        user.setSubscriptionEndDate(calendar.getTime());
        
        userRepository.save(user);
    }
}
