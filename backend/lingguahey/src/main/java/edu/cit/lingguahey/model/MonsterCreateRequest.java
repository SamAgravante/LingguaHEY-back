package edu.cit.lingguahey.model;

import lombok.Data;

@Data
public class MonsterCreateRequest {
    private String tagalogName;
    private String englishName;
    private String description;
    private byte[] imageData;
}
