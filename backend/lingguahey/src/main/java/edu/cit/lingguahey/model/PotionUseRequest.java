package edu.cit.lingguahey.model;

import edu.cit.lingguahey.Entity.PotionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PotionUseRequest {
    private int userId;
    private PotionType potionType;
}
