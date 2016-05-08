package com.will.orderfoodapp.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Will on 2016/4/24.
 */
public class BaseFragment extends Fragment {

    public void showToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}
