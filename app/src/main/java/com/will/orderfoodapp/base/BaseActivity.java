package com.will.orderfoodapp.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.bmob.v3.Bmob;


/**
 * Created by Will on 2016/4/21.
 */
public class BaseActivity extends AppCompatActivity {
    private static String APP_ID = "e8a8de0be0da48c05ba1ebf3360b34c6";
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bmob.initialize(this,APP_ID);
    }
    public  void showToast(String message){
        if(toast == null){
            toast = Toast.makeText(this,null,Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        toast.show();
    }
}
