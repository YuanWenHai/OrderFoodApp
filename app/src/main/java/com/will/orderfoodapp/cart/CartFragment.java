package com.will.orderfoodapp.cart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseFragment;
import com.will.orderfoodapp.bean.Cart;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.bean.Order;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/4/25.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener,CartAdapter.OnDataChangedListener,
        AlertDialog.OnClickListener,CartAdapter.RefreshListener,SwipeRefreshLayout.OnRefreshListener{
    private List<Cart> data = new ArrayList<>();
    private TextView total;
    private int sum;
    private SwipeRefreshLayout refreshLayout;
    private CartAdapter adapter;
    private MyUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        user = BmobUser.getCurrentUser(getActivity(),MyUser.class);
        if(user != null) {
            View view = inflater.inflate(R.layout.fragment_cart, null);
            total = (TextView) view.findViewById(R.id.cart_total);
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.cart_refresh_layout);
            refreshLayout.setEnabled(true);
            refreshLayout.setOnRefreshListener(this);
            TextView commit = (TextView) view.findViewById(R.id.cart_commit);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.cart_recycler_view);
            commit.setOnClickListener(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new CartAdapter(getActivity(), data);
            adapter.setRefreshListener(this);
            adapter.setOnDataChangedListener(this);
            recyclerView.setAdapter(adapter);
            return view;
        }else{
              return inflater.inflate(R.layout.not_login,null);
        }
    }
    @Override
    public void onClick(View v){
        if(data.size() != 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle);
            builder.setMessage("确定下单吗？下单后当我方送餐员到达指定地址时会电话联系您。");
            builder.setPositiveButton("确定下单",this);
            builder.setNegativeButton("取消",null);
            builder.show();
        }else{
            showToast("购物车为空");
        }
    }
    public void onDataChanged(){
        sum = 0;
        for (Cart cart : data){
            sum += (cart.getQuantity()*Integer.decode(cart.getPrice()));
        }
        total.setText("共计："+sum);
    }
    @Override
    public void onClick(DialogInterface dialog,int arg){
        Order order = new Order();
        order.setPrice(String.valueOf(sum));
        order.setList(data);
        order.setState("送餐中");
        order.setUser(user);
        order.save(getActivity(), new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("已提交");
                deleteCart();
                data.clear();
                total.setText("共计：");
                adapter.notifyDataSetChanged();
                /*new BmobObject().deleteBatch(getActivity(), getObjectList(), new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        data.clear();
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(int i, String s) {

                    }
                });*/
            }
            @Override
            public void onFailure(int i, String s) {
                showToast("提交失败");
            }
        });
    }
    @Override
    public void cancelRefreshing(){
        refreshLayout.setRefreshing(false);
    }
    @Override
    public void onRefresh(){
        adapter.getData();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(user != null){
            refreshLayout.setRefreshing(true);
            adapter.getData();
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden){
        if(!hidden){
            if(user != null){
                refreshLayout.setRefreshing(true);
                adapter.getData();
            }
        }
    }
    private List<BmobObject> getObjectList(){
        List<BmobObject> list = new ArrayList<>();
        BmobObject object;
        for(Cart cart :data){
            object = new BmobObject();
            object.setObjectId(cart.getObjectId());
            list.add(object);
        }
        return list;
    }

    /**
     * 这个...事实上无法确定所有项目已经全部删除
     */
    private void deleteCart(){
        for(Cart cart  :data){
            cart.delete(getContext());
        }
    }
}
