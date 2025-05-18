package edu.cit.lingguahey.DTO;

public class LiveActivityUpdate {
    private int activityId;
    private String status; // e.g., "STARTED", "QUESTION_UPDATED", etc.
    private Object payload; // could be a question, user stats, etc.
    

    public LiveActivityUpdate(int activityId, String status, Object payload) {
        this.activityId = activityId;
        this.status = status;
        this.payload = payload;
    }

    public LiveActivityUpdate() {
    }

    public int getActivityId() {
        return activityId;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

}
