package com.will.orderfoodapp.user;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;
import com.will.orderfoodapp.bean.MyUser;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/4/24.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener{
    private EditText phoneNumber,authCode,password1,password2;
    private TextInputLayout inputLayout;
    private Button send;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //初始化
        View view = inflater.inflate(R.layout.fragment_register,null);
        phoneNumber = (EditText) view.findViewById(R.id.register_phone_number);
        authCode = (EditText) view.findViewById(R.id.register_authentication);
        password1 = (EditText) view.findViewById(R.id.register_password);
        password2 = (EditText) view.findViewById(R.id.register_password_again);
        inputLayout = (TextInputLayout) view.findViewById(R.id.register_input_layout);
        send = (Button) view.findViewById(R.id.register_send_button);
        Button commit = (Button) view.findViewById(R.id.register_commit);
        TextView skip2Login = (TextView) view.findViewById(R.id.register_skip_to_login);
        //监听点击
        skip2Login.setOnClickListener(this);
        commit.setOnClickListener(this);
        send.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v ){
        switch (v.getId()){
            case R.id.register_send_button:
                String number = phoneNumber.getText().toString();
                if(number.length() != 11){
                    showToast("请输入合法的手机号");
                }else{
                    BmobSMS.requestSMSCode(getActivity(), number, "默认", new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e == null){
                                showToast("已发送");
                                countdown();
                            }
                        }
                    });
                }
                break;
            case R.id.register_commit:
                String phoneStr = phoneNumber.getText().toString();
                String password1Str = password1.getText().toString();
                String password2Str = password2.getText().toString();
                String authCodeStr = authCode.getText().toString();
                if(!password1Str.equals(password2Str)){
                    showToast("两次密码输入不一致");
                }else if (password1Str.length() < 6 ||password2Str.length() < 6){
                    showToast("密码长度不足六位");
                } else if ( phoneStr.length() != 11){
                    showToast("请输入合法的手机号");
                } else{
                    MyUser user = new MyUser();
                    user.setMobilePhoneNumber(phoneStr);
                    user.setPassword(password1Str);
                    user.signOrLogin(getActivity(), authCodeStr, new SaveListener() {
                        @Override
                        public void onSuccess() {
                         //
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserFragment(),"user").commit();
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            if(i == 207){
                                inputLayout.setError(s);
                            }
                        }
                    });
                }
                break;
            case R.id.register_skip_to_login:
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().remove(manager.findFragmentByTag("user")).commit();
                manager.beginTransaction().add(R.id.fragment_container,new LoginFragment(),"user").commit();
        }
    }

    /**
     * 延迟60秒才能再次发送验证短信
     */
    private void countdown(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 60;i > 0;i--){
                   changeButtonNumber(i);
                    SystemClock.sleep(1000);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        send.setText("发送");
                        send.setClickable(true);
                    }
                });
            }
        }).start();
    }
    private void changeButtonNumber(final int second){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send.setText(String.valueOf(second)+"s");
                send.setClickable(false);
            }
        });
    }
}
