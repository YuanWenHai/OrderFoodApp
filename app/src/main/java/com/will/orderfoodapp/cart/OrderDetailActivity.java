package com.will.orderfoodapp.cart;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseActivity;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.bean.Order;
import com.will.orderfoodapp.user.MyCartAdapter;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Will on 2016/5/5.
 */
public class OrderDetailActivity extends BaseActivity {
    private TextView phone,address;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_order_detail);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fragment_my_order_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_my_order_toolbar);
        phone = (TextView) findViewById(R.id.order_detail_phone);
        address = (TextView) findViewById(R.id.order_detail_address);
        //
        Order order = (Order) getIntent().getSerializableExtra("order");
        queryUser(order.getUser().getObjectId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyCartAdapter(order.getList()));
        toolbar.setTitle("订单详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void queryUser(String objectId){
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId",objectId);
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
               if(list != null && list.size() != 0){
                   MyUser user = list.get(0);
                   phone.setText("客户联系方式："+user.getUsername()+"");
                   address.setText("送餐地址："+user.getAddress());
               }
            }
            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
