package com.james.server.Model;

import java.io.Serializable;

public class Post implements Serializable{
    
    private String comment;
    private byte[] image;

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }



    
}
