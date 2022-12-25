package com.example.zwitter.Models;

public class Notification_model {
    private String notification_by;
    private long notification_at;
    private String type; /// notification whether for like or comment or follow
    private String postID;
    private String notificationID;
    private String posted_by;
    private boolean check_open;

    public String getNotification_by() {
        return notification_by;
    }

    public void setNotification_by(String notification_by) {
        this.notification_by = notification_by;
    }

    public long getNotification_at() {
        return notification_at;
    }

    public void setNotification_at(long notification_at) {
        this.notification_at = notification_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public boolean isCheck_open() {
        return check_open;
    }

    public void setCheck_open(boolean check_open) {
        this.check_open = check_open;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }
}
