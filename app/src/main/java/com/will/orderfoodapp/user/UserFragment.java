package com.will.orderfoodapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.bean.Order;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/4/25.
 */
public class UserFragment extends BaseFragment implements View.OnClickListener, OrderAdapter.OnClickListener,
        OrderAdapter.RefreshListener,SwipeRefreshLayout.OnRefreshListener {
    private EditText address;
    private MyUser user;
    private SwipeRefreshLayout refreshLayout;
    private OrderAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        user = BmobUser.getCurrentUser(getActivity(),MyUser.class);
       if(!user.getIsAdministrator()){
           View view = inflater.inflate(R.layout.fragment_user,null);
           address = (EditText) view.findViewById(R.id.user_address);
           TextView phoneNumber = (TextView) view.findViewById(R.id.user_phone_number);
           Button change = (Button) view.findViewById(R.id.user_change_address);
           RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_order_recycler_view);
           refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.user_refresh_layout);
           //
           phoneNumber.setText(user.getMobilePhoneNumber());
           if(user.getAddress() != null){
               address.setText(user.getAddress());
           }
           change.setOnClickListener(this);
           recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
           adapter = new OrderAdapter(getActivity());
           adapter.setRefreshListener(this);
           adapter.setOnItemClickListener(this);
           adapter.queryOrder();
           refreshLayout.setEnabled(true);
           refreshLayout.setRefreshing(true);
           refreshLayout.setOnRefreshListener(this);
           recyclerView.setAdapter(adapter);
           return view;
       }else{
           View view = inflater.inflate(R.layout.fragment_you_are_manager,null);
           return view;
       }
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.user_change_address:
                String addressStr = address.getText().toString();
                user.setAddress(addressStr);
                user.update(getActivity(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("修改成功");
                    }
                    @Override
                    public void onFailure(int i, String s) {}
                });
                break;

        }
    }
    @Override
    public void onItemClick(Order order){
        Intent intent = new Intent(getActivity(),MyCartActivity.class);
        intent.putExtra("order",order);
        startActivity(intent);
    }
    @Override
    public void onRefresh(){
        adapter.queryOrder();
    }
    @Override
    public void cancelRefreshing(){
        refreshLayout.setRefreshing(false);
    }
    @Override
    public void onHiddenChanged(boolean hidden){
        if(!hidden){
            if(!user.getIsAdministrator()){
                refreshLayout.setRefreshing(true);
                adapter.queryOrder();
            }
        }
    }
}
