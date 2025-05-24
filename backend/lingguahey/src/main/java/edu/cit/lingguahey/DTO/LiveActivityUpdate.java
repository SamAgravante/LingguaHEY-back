package edu.cit.lingguahey.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveActivityUpdate {
    private int activityId;
    private String status; // e.g., "STARTED", "QUESTION_UPDATED", etc.
    private Object payload; // could be a question, user stats, etc.
}
