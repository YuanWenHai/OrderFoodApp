package com.will.orderfoodapp.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/4/24.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener{
    private EditText account,password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login,null);
        account = (EditText) view.findViewById(R.id.login_account);
        password = (EditText) view.findViewById(R.id.login_password);
        Button login = (Button) view.findViewById(R.id.login_login);
        login.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v){
        String accountStr = account.getText().toString();
        String passwordStr = password.getText().toString();
        BmobUser user = new BmobUser();
        user.setUsername(accountStr);
        user.setPassword(passwordStr);
        user.login(getActivity(), new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("登陆成功");
                //getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("user")).commit();
                //getFragmentManager().beginTransaction().add(R.id.fragment_container,new UserFragment(),"user").commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserFragment(),"user").commit();
            }
            @Override
            public void onFailure(int i, String s) {
                if(i == 101){
                    showToast("密码错误");
                }
            }
        });
    }
}
