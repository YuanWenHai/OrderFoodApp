package com.will.orderfoodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.will.orderfoodapp.base.BaseActivity;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.cart.CartFragment;
import com.will.orderfoodapp.cart.ManagerFragment;
import com.will.orderfoodapp.home.HomeFragment;
import com.will.orderfoodapp.menu.MenuFragment;
import com.will.orderfoodapp.service.PushService;
import com.will.orderfoodapp.user.RegisterFragment;
import com.will.orderfoodapp.user.UserFragment;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ImageButton navigation_menu;
    private ImageButton navigation_shopping_cart;
    private ImageButton navigation_user;
    private ImageButton navigation_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeView();
        setupPushService();
    }
    private void initializeView(){
        navigation_home = (ImageButton) findViewById(R.id.navigation_home);
        navigation_menu = (ImageButton) findViewById(R.id.navigation_menu);
        navigation_shopping_cart = (ImageButton) findViewById(R.id.navigation_shopping_cart);
        navigation_user = (ImageButton) findViewById(R.id.navigation_user);
        navigation_home.setOnClickListener(this);
        navigation_menu.setOnClickListener(this);
        navigation_shopping_cart.setOnClickListener(this);
        navigation_user.setOnClickListener(this);
        //默认首页为菜单
        FragmentManager manager = getSupportFragmentManager();
        if(getIntent().getBooleanExtra("push",false)){
            navigation_shopping_cart.setSelected(true);
            manager.beginTransaction().add(R.id.fragment_container,new ManagerFragment(),"cart").commit();
        }else {
            navigation_menu.setSelected(true);
            manager.beginTransaction().add(R.id.fragment_container, new MenuFragment(), "menu").commit();
        }
    }
    @Override
    public void onClick(View v){
        FragmentManager manager = getSupportFragmentManager();
        boolean isAdministrator = false;//是否为管理员权限
        MyUser user;
        switch (v.getId()){
            case R.id.navigation_menu:
                if(manager.findFragmentByTag("menu") == null){
                    manager.beginTransaction().add(R.id.fragment_container,new MenuFragment(),"menu").commit();
                }else{
                    manager.beginTransaction().show(manager.findFragmentByTag("menu")).commit();
                }
                changeOthers(0);
                break;
            case R.id.navigation_shopping_cart:
                user = BmobUser.getCurrentUser(MainActivity.this,MyUser.class);
                if( user != null){
                    isAdministrator = user.getIsAdministrator();
                }
                if(manager.findFragmentByTag("cart") == null){
                    if(isAdministrator){
                        manager.beginTransaction().add(R.id.fragment_container,new ManagerFragment(),"cart").commit();
                    }else{
                        manager.beginTransaction().add(R.id.fragment_container,new CartFragment(),"cart").commit();
                    }
                }else{
                    manager.beginTransaction().show(manager.findFragmentByTag("cart")).commit();
                }
                changeOthers(1);
                break;
            case R.id.navigation_user:
                user = BmobUser.getCurrentUser(MainActivity.this,MyUser.class);
                if (user == null){
                    if(manager.findFragmentByTag("user") == null){
                        manager.beginTransaction().add(R.id.fragment_container,new RegisterFragment(),"user").commit();
                    }else{
                        manager.beginTransaction().show(manager.findFragmentByTag("user")).commit();
                    }
                }else{
                    if(manager.findFragmentByTag("user") == null){
                        manager.beginTransaction().add(R.id.fragment_container,new UserFragment(),"user").commit();
                    }else{
                        manager.beginTransaction().show(manager.findFragmentByTag("user")).commit();
                    }
                }
                changeOthers(2);
                break;
            case R.id.navigation_home:
                changeOthers(3);
               if(manager.findFragmentByTag("home") == null){
                   manager.beginTransaction().add(R.id.fragment_container,new HomeFragment(),"home").commit();
               }else{
                   manager.beginTransaction().show(manager.findFragmentByTag("home")).commit();
               }
                break;
        }
    }

    /**改变项目选定状态与fragment状态
     * @param id Navigation Button id
     */
    private void changeOthers(int id){
        FragmentManager manager = getSupportFragmentManager();
        if(id != 0){
            navigation_menu.setSelected(false);
            Fragment fragment = manager.findFragmentByTag("menu");
            if(fragment != null && fragment.isVisible()){
                manager.beginTransaction().hide(fragment).commit();
            }
        }else{
            navigation_menu.setSelected(true);
        }
        if(id != 1){
            navigation_shopping_cart.setSelected(false);
            Fragment fragment = manager.findFragmentByTag("cart");
            if(fragment != null && fragment.isVisible()){
                manager.beginTransaction().hide(fragment).commit();
            }
        }else{
            navigation_shopping_cart.setSelected(true);
        }
        if (id != 2){
            navigation_user.setSelected(false);
            Fragment fragment = manager.findFragmentByTag("user");
            if(fragment != null && fragment.isVisible()){
                manager.beginTransaction().hide(fragment).commit();
            }
        }else{
            navigation_user.setSelected(true);
        }
        if(id != 3){
            navigation_home.setSelected(false);
            Fragment fragment = manager.findFragmentByTag("home");
            if(fragment != null && fragment.isVisible()){
                manager.beginTransaction().hide(fragment).commit();
            }
        }else{
            navigation_home.setSelected(true);
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        MenuFragment fragment = (MenuFragment)getSupportFragmentManager().findFragmentByTag("menu");
        if(intent.getBooleanExtra("refresh",false)){
            fragment.refresh();
        }else if(intent.getBooleanExtra("refresh_pager",false)){
            fragment.getData();
            Log.e("refresh","execute");
        }
    }
    private void setupPushService(){
        MyUser user = BmobUser.getCurrentUser(this,MyUser.class);
        if(user != null && user.getIsAdministrator()){
            Intent intent = new Intent(this, PushService.class);
            startService(intent);
        }
    }
}
