package edu.cit.lingguahey.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GachaPullRequest {
    private int userId;
    private String pullType;
}
