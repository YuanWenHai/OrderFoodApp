package com.will.orderfoodapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;

/**
 * Created by Will on 2016/5/5.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener{
    private ImageView image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home,null);
        image = (ImageView) view.findViewById(R.id.home_image);
        image.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v){
        //BmobUser.logOut(getActivity());
        //getActivity().finish();
    }
}
