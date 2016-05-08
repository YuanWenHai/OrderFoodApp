package com.will.orderfoodapp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/4/21.
 */
public class Headline extends BmobObject {
    private String image;
    private String description;
    private String name;
    private String price;
    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return image;
    }
    public void setDescription(String description){
        this.description  = description;
    }
    public String getDescription(){
        return description;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return price;
    }
}
