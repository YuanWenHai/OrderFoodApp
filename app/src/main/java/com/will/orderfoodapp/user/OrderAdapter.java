package com.will.orderfoodapp.user;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Cart;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.bean.Order;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Will on 2016/4/25.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private OnClickListener listener;
    private RefreshListener refreshListener;
    private OnLongClickListener longClickListener;
    private List<Order> data = new ArrayList<>();
    private MyUser user;
    private Context context;
    public OrderAdapter(Context context){
        user = BmobUser.getCurrentUser(context,MyUser.class);
        this.context = context;
    }

    public void queryOrder(){
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("user",user);
        query.setLimit(50);
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if(list.size() > 0){
                    data.clear();
                    data.addAll(list);
                    notifyItemRangeChanged(0, data.size());
                    if(refreshListener != null){
                        refreshListener.cancelRefreshing();
                    }
                }else{
                    Toast.makeText(context,"未找到记录",Toast.LENGTH_SHORT).show();
                    if(refreshListener != null){
                        refreshListener.cancelRefreshing();
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                if(refreshListener != null){
                    refreshListener.cancelRefreshing();
                }
            }
        });
    }
    public void queryAllOrder(){
        BmobQuery<Order> query = new BmobQuery<>();
        query.setLimit(50);
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if(list.size() > 0){
                    data.clear();
                    data.addAll(list);
                    notifyItemRangeChanged(0, data.size());
                    if(refreshListener != null){
                        refreshListener.cancelRefreshing();
                    }
                }else{
                    Toast.makeText(context,"未找到记录",Toast.LENGTH_SHORT).show();
                    if(refreshListener != null){
                        refreshListener.cancelRefreshing();
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                if(refreshListener != null){
                    refreshListener.cancelRefreshing();
                }
            }
        });
    }
    public void setOnItemClickListener(OnClickListener listener){
        this.listener = listener;
    }
    public void setOnItemLongClickListener(OnLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }
    public void setRefreshListener(RefreshListener refreshListener){
        this.refreshListener = refreshListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int position){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,null);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        holder.price.setText("¥"+data.get(position).getPrice());
        holder.time.setText(data.get(position).getCreatedAt());
        String stateStr = data.get(position).getState();
        holder.state.setText(stateStr);
        if(stateStr.equals("送餐中")){
            //holder.itemView.setBackgroundColor(Color.GREEN);
            ((CardView)holder.itemView).setCardBackgroundColor(Color.GREEN);
        }else{
            ((CardView)holder.itemView).setCardBackgroundColor(Color.GRAY);
        }
        //取出各个菜名
        StringBuilder builder = new StringBuilder();
        List<Cart> list = data.get(position).getList();
        for(int i = 0;i < list.size();i++){
            builder.append(list.get(i).getName());
           if(i != list.size()-1){
               builder.append("、");
           }else{
               builder.append("。");
           }
        }
        String str = builder.toString();
        holder.names.setText(str);
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        public TextView price;
        public TextView time;
        public TextView state;
        public TextView names;
        public ViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            price = (TextView) v.findViewById(R.id.order_item_price);
            time = (TextView) v.findViewById(R.id.order_item_time);
            state = (TextView) v.findViewById(R.id.order_item_state);
            names = (TextView) v.findViewById(R.id.order_item_names);
        }
        @Override
        public void onClick(View v){
            if(listener != null){
                listener.onItemClick(data.get(getAdapterPosition()));
            }
        }
        @Override
        public boolean onLongClick(View v){
            if(longClickListener != null){
                longClickListener.onItemLongClick(data.get(getAdapterPosition()));
            }
            return  true;
        }
    }
    public interface OnClickListener{
        void onItemClick(Order order);
    }
    public interface OnLongClickListener{
        void onItemLongClick(Order order);
    }
    public interface RefreshListener{
        void cancelRefreshing();
    }
}
