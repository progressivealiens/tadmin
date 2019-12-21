package com.techsalt.tadmin.model;

public class PostDataModel {

    String text,image,postAddress,postDate, postLatitude, postLongitude;

    public PostDataModel(String text, String image, String postAddress, String postDate, String postLatitude, String postLongitude) {
        this.text = text;
        this.image = image;
        this.postAddress = postAddress;
        this.postDate = postDate;
        this.postLatitude = postLatitude;
        this.postLongitude = postLongitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostLatitude() {
        return postLatitude;
    }

    public void setPostLatitude(String postLatitude) {
        this.postLatitude = postLatitude;
    }

    public String getPostLongitude() {
        return postLongitude;
    }

    public void setPostLongitude(String postLongitude) {
        this.postLongitude = postLongitude;
    }


}
