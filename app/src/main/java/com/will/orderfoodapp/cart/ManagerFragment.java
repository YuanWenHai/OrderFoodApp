package com.will.orderfoodapp.cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;
import com.will.orderfoodapp.bean.Order;
import com.will.orderfoodapp.user.OrderAdapter;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/5/5.
 */
public class ManagerFragment extends BaseFragment implements OrderAdapter.OnLongClickListener,OrderAdapter.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,OrderAdapter.RefreshListener{
    private OrderAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view  = inflater.inflate(R.layout.fragment_manager,null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.manager_recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.manager_refresh_layout);
        //
        adapter = new OrderAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        adapter.setRefreshListener(this);
        refreshLayout.setEnabled(true);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);
        recyclerView.setEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.queryAllOrder();
        return view;
    }
    @Override
    public void onItemClick(Order order){
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("order",order);
        startActivity(intent);
    }
    @Override
    public void onItemLongClick(final Order order){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle);
        builder.setMessage("是否标记为已完成？");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order.setState("已完成");
                refreshLayout.setRefreshing(true);
                order.update(getActivity(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        adapter.queryAllOrder();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                    }
                });
            }
        }).show();
    }
    @Override
    public void cancelRefreshing(){
        refreshLayout.setRefreshing(false);
    }
    @Override
    public void onRefresh(){
        adapter.queryAllOrder();
    }
    @Override
    public void onHiddenChanged(boolean hidden){
        if(!hidden){
            refreshLayout.setRefreshing(true);
            adapter.queryAllOrder();
        }
    }
}
