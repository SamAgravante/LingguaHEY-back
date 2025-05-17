package edu.cit.lingguahey.model;

import edu.cit.lingguahey.Entity.LessonActivityEntity.GameType;

public interface UserActivityProjection {
    int getActivity_ActivityId();
    String getActivity_LessonName();
    boolean isCompleted();
    GameType getActivity_GameType();
}
