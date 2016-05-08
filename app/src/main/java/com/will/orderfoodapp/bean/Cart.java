package com.will.orderfoodapp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/4/23.
 */
public class Cart extends BmobObject {
    private String image;
    private String name;
    private String price;
    private Integer quantity = 1;
    private MyUser user;
    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return image;
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
    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
    public Integer getQuantity(){
        return quantity;
    }
    public void setUser(MyUser user){
        this.user = user;
    }
    public MyUser getUser(){
        return user;
    }
}
