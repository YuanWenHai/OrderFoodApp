package com.will.orderfoodapp.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Food;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Will on 2016/4/22.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    private static String TAG = "MyRecyclerAdapter";
    private List<Food> data = new ArrayList<>();
    private OnFoodClickListener clickListener;
    private Context context;
    public MyRecyclerAdapter(Context context){
        this.context = context;
        getData();
    }
    @Override
    public void onBindViewHolder(MyHolder holder,int position){
        Food item = data.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        Picasso.with(context).load(item.getImage()).resize(200,200).centerCrop().placeholder(R.drawable.loading_image).
                error(R.drawable.no_image_available).into(holder.image);
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent,int position){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,null);
        return new MyHolder(v);
    }
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name;
        public TextView price;
        public ImageView image;
        public MyHolder(View v){
            super(v);
            name = (TextView) v.findViewById(R.id.menu_item_name);
            price = (TextView) v.findViewById(R.id.menu_item_price);
            image = (ImageView) v.findViewById(R.id.menu_item_image);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v){
            if(clickListener != null){
                clickListener.onFoodClick(data.get(getAdapterPosition()));
            }
        }
    }

    private void getData(){
        BmobQuery<Food> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findObjects(context, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if(list.size() != 0){
                    data.addAll(list);
                    notifyItemRangeChanged(0,list.size());
                }else{
                    Log.e(TAG,"list's size is zero!");
                }
            }
            @Override
            public void onError(int i, String s) {
                Log.e(TAG,s);
            }
        });
    }
    public void refreshData(final OnFoodRefreshListener listener){
        BmobQuery<Food> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        query.findObjects(context, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if(list.size() != 0){
                    data.clear();
                    data.addAll(list);
                    listener.onSuccess();
                    notifyItemRangeChanged(0,data.size());
                }else{
                    Log.e(TAG,"list's size is zero!");
                }
            }
            @Override
            public void onError(int i, String s) {
                listener.onFailure();
            }
        });
    }
    public void setOnClickListener(OnFoodClickListener listener){
        this.clickListener = listener;
    }
    public interface OnFoodClickListener{
        /**
         * item的点击回调，传出相应的food实例
         */
        void onFoodClick(Food food);
    }
    public interface OnFoodRefreshListener {
        /**
         * 刷新成功后的回调，用于取消swipe的刷新动画
         */
        void onSuccess();
        /**
         * 刷新失败回调
         */
        void onFailure();
    }
}
