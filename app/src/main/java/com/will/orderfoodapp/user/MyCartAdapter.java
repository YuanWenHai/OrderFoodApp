package com.will.orderfoodapp.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Cart;

import java.util.List;

/**
 * Created by Will on 2016/5/3.
 */
public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.CartHolder> {
    private List<Cart> data;
    private Context context;
    public MyCartAdapter(List<Cart> data){
        this.data = data;
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    @Override
    public CartHolder onCreateViewHolder(ViewGroup parent,int position){
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.activity_my_order_item,null);
        return new CartHolder(v);
    }
    @Override
    public void onBindViewHolder(CartHolder holder,int position){
        Cart cart = data.get(position);
        holder.title.setText(cart.getName());
        holder.price.setText("¥"+cart.getPrice());
        holder.quantity.setText("数量 "+cart.getQuantity()+"");
        Picasso.with(context).load(cart.getImage()).placeholder(R.drawable.loading_image).resize(200,200).centerCrop().into(holder.image);
    }



    class CartHolder extends RecyclerView.ViewHolder{
        public TextView title,quantity,price;
        public ImageView image;
        public CartHolder(View v){
            super(v);
            title = (TextView) v.findViewById(R.id.user_cart_item_title);
            quantity = (TextView) v.findViewById(R.id.user_cart_item_quantity);
            price = (TextView) v.findViewById(R.id.user_cart_item_price);
            image =(ImageView) v.findViewById(R.id.user_cart_item_image);
        }
    }
}
