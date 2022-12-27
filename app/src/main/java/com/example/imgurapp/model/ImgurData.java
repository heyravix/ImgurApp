package com.example.imgurapp.model;

public class ImgurData {

    public ImgurData(String title, String datetime, String imageUrl, String totalImages) {
        this.title = title;
        this.datetime = datetime;
        this.imageUrl = imageUrl;
        this.totalImages = totalImages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(String totalImages) {
        this.totalImages = totalImages;
    }

    private  String title;
    private  String datetime;
    private  String imageUrl;
    private  String totalImages;
}
