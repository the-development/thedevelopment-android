package com.jmartin.thedevelopment.android.model;

import java.io.Serializable;

/**
 * Created by jeff on 2014-03-25.
 */
public class Interview implements Serializable {

    private String name;
    private String publishedDate;
    private String position;
    private String content;
    private String url;
    private String image;
    private boolean read = false;
    private String dpImage;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Interview(String name, String publishedDate, String position, String url, String image, String dpImage, String content) {
        this.name = name;
        this.publishedDate = publishedDate;
        this.position = position;
        this.url = url;
        this.image = image;
        this.dpImage = dpImage;
        this.content = content;
        this.read = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDpImage() {
        return dpImage;
    }

    public void setDpImage(String dpImage) {
        this.dpImage = dpImage;
    }
}
