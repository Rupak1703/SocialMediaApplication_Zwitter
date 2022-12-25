package com.example.zwitter.Models;

public class Post_model {
    private String post_id;
    private String PostImage;
    private String post_description;
    private String posted_by; // id of that user
    private long posted_at;   // shows the time at which they are saved
    private int postLiked;
    private int commentsCount;

    public Post_model() {
    }

    public Post_model(String post_id, String postImage, String post_description, String posted_by, long posted_at) {
        this.post_id = post_id;
        PostImage = postImage;
        this.post_description = post_description;
        this.posted_by = posted_by;
        this.posted_at = posted_at;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getPostLiked() {
        return postLiked;
    }

    public void setPostLiked(int postLiked) {
        this.postLiked = postLiked;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getPost_description() {
        return post_description;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public long getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(long posted_at) {
        this.posted_at = posted_at;
    }
}
