package edu.cit.lingguahey.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipCosmeticRequest {
    private int userId;
    private int cosmeticId;
}
