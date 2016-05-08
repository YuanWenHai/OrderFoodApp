package com.will.orderfoodapp.cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;
import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Cart;
import com.will.orderfoodapp.bean.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/4/25.
 *必须主动调用getData方法获取数据.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Cart> data;
    private Context context;
    private OnDataChangedListener listener;
    private MyUser user;
    private RefreshListener refreshListener;
    public CartAdapter(Context context,List<Cart> data) {
        this.context = context;
        this.data = data;
        user = BmobUser.getCurrentUser(context,MyUser.class);
    }

    /**
     * 必须主动调用此方法获取数据
     */
    public void getData(){
        BmobQuery<Cart> query = new BmobQuery<>();
        query.addWhereEqualTo("user",user);
        query.findObjects(context, new FindListener<Cart>() {
            @Override
            public void onSuccess(List<Cart> list) {
                if(list.size() > 0 ){
                    data.clear();
                    data.addAll(list);
                    notifyItemRangeChanged(0,data.size());
                    listener.onDataChanged();
                    refreshListener.cancelRefreshing();
                }else{
                    refreshListener.cancelRefreshing();
                }
            }
            @Override
            public void onError(int i, String s) {
                refreshListener.cancelRefreshing();
            }
        });
    }


    public void onBindViewHolder(CartViewHolder holder,int position){
        Picasso.with(context).load(data.get(position).getImage()).resize(200,200).centerCrop().placeholder(R.drawable.loading_image).into(holder.image);
        holder.name.setText(data.get(position).getName());
        holder.price.setText("¥" +data.get(position).getPrice());
        holder.picker.setValue(data.get(position).getQuantity());
    }
    public int getItemCount(){
        return data.size();
    }
    public CartViewHolder onCreateViewHolder(ViewGroup parent,int position){
        View view  = LayoutInflater.from(context).inflate(R.layout.cart_item,null);
        return new CartViewHolder(view);
    }
    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,HorizontalNumberPickerListener{
        public HorizontalNumberPicker picker;
        public TextView name;
        public TextView price;
        public ImageView image;
        public ImageButton delete;
        public CartViewHolder(View v){
            super(v);
            name =(TextView) v.findViewById(R.id.cart_item_name);
            price =(TextView) v.findViewById(R.id.cart_item_price);
            image = (ImageView) v.findViewById(R.id.cart_item_image);
            picker = (HorizontalNumberPicker) v.findViewById(R.id.cart_item_picker);
            delete = (ImageButton) v.findViewById(R.id.cart_item_delete);
            delete.setOnClickListener(this);
            picker.setListener(this);
        }
        @Override
        public void onClick(View v){
            Cart cart = data.get(getAdapterPosition());
            cart.delete(context, new DeleteListener() {
                @Override
                public void onSuccess() {
                    data.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    if(listener != null){
                        listener.onDataChanged();
                    }
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast("删除失败");
                }
            });
        }
        @Override
        public void onHorizontalNumberPickerChanged(HorizontalNumberPicker picker, int value){
            Cart cart  = data.get(getAdapterPosition());
            cart.setQuantity(value);
            cart.update(context, new UpdateListener() {
                @Override
                public void onSuccess() {
                    if(listener != null){
                        listener.onDataChanged();
                    }
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast("更新失败");
                }
            });
        }
    }
    public void setOnDataChangedListener(OnDataChangedListener listener){
        this.listener = listener;
    }
    public void setRefreshListener(RefreshListener refreshListener){
        this.refreshListener = refreshListener;
    }
    private void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    interface OnDataChangedListener{
        /**
         * 当条目的增删导致总计金额改变时，调用此方法通知外部更新
         */
        void onDataChanged();
    }
    interface RefreshListener{
        void cancelRefreshing();
    }
}
