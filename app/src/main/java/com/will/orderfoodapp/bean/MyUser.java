package com.will.orderfoodapp.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Will on 2016/4/25.
 */
public class MyUser extends BmobUser {
    private String address;
    private Boolean isAdministrator = false;
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
    public void setIsAdministrator(Boolean which){
        isAdministrator  = which;
    }
    public Boolean getIsAdministrator(){
        return isAdministrator;
    }
}
