package com.ibrickedlabs.BlogApp.Model;

public class Blog {
    public  String title;
    public  String desc;
    public String image;
    public  String timeStamp;
    public  String userid;

    public Blog() {
    }

    public Blog(String title, String desc, String image, String timeStamp, String userid) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.timeStamp = timeStamp;
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
