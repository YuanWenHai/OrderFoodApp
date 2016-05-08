package com.will.orderfoodapp.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/4/25.
 */
public class Order extends BmobObject {
    private List<Cart> list;
    private String state;
    private String price;
    private MyUser user;
    public void setList(List<Cart> list){
        this.list = list;
    }
    public List<Cart> getList(){
        return list;
    }
    public void setState(String state){
        this.state = state;
    }
    public String getState(){
        return state;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return price;
    }
    public void setUser(MyUser user){
        this.user = user;
    }
    public MyUser getUser(){
        return user;
    }
}
