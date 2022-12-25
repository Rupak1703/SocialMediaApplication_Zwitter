package com.example.zwitter.Models;

public class UserStories {
    private String story_image; /// uri
    private long story_posted_At;

    public UserStories(String story_image, long story_posted_At) {
        this.story_image = story_image;
        this.story_posted_At = story_posted_At;
    }

    public UserStories() {
    }

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }

    public long getStory_posted_At() {
        return story_posted_At;
    }

    public void setStory_posted_At(long story_posted_At) {
        this.story_posted_At = story_posted_At;
    }
}
