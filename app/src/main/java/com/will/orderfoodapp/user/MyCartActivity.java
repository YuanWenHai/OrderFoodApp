package com.will.orderfoodapp.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseActivity;
import com.will.orderfoodapp.bean.Order;

/**
 * Created by Will on 2016/5/3.
 */
public class MyCartActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fragment_my_order_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_my_order_toolbar);
        //
        Order order = (Order) getIntent().getSerializableExtra("order");
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
}
