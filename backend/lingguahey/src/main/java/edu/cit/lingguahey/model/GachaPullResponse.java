package edu.cit.lingguahey.model;

import edu.cit.lingguahey.Entity.CosmeticEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GachaPullResponse {
    private String message;
    private CosmeticEntity cosmetic;
}
