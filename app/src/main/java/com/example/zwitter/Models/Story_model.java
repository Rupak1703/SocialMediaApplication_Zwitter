package com.example.zwitter.Models;

import java.util.ArrayList;

public class Story_model {

    private String storyBy;
    private long StoryAt;
    ArrayList<UserStories> stories;

    public Story_model() {
    }

    public Story_model(String storyBy, long storyAt, ArrayList<UserStories> stories) {
        this.storyBy = storyBy;
        StoryAt = storyAt;
        this.stories = stories;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return StoryAt;
    }

    public void setStoryAt(long storyAt) {
        StoryAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }
}
