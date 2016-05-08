package com.will.orderfoodapp.menu.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseActivity;
import com.will.orderfoodapp.bean.Cart;
import com.will.orderfoodapp.bean.Food;
import com.will.orderfoodapp.bean.Headline;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.menu.edit.EditMenuActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/4/23.
 */
public class FoodDetailActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private Food food;
    private Headline headline;
    private TextView price,description;
    private ImageView image;
    private TextView addToCart;
    private boolean TYPE_FOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        initializeViews();
    }
    private void initializeViews(){
        //
        toolbar = (Toolbar) findViewById(R.id.food_detail_toolbar);
        addToCart = (TextView) findViewById(R.id.food_detail_add_to_cart);
        price = (TextView) findViewById(R.id.food_detail_price);
        image = (ImageView) findViewById(R.id.food_detail_image);
        description = (TextView) findViewById(R.id.food_detail_description);
        //
        if(getIntent().hasExtra("food")){
            food = (Food)getIntent().getSerializableExtra("food");
            TYPE_FOOD = true;
            setupFoodDataWithViews();
            toolbar.setTitle(food.getName());
        }else{
            headline = (Headline) getIntent().getSerializableExtra("headline");
            setupHeadlineDataWithViews();
            toolbar.setTitle(headline.getName());
        }
        //
        addToCart.setOnClickListener(this);
        //

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onClick(View v){
        MyUser user = BmobUser.getCurrentUser(this,MyUser.class);
        if(user == null){
            showToast("未注册");
        }else{
        if(TYPE_FOOD){
            Cart cart = new Cart();
            cart.setPrice(food.getPrice());
            cart.setName(food.getName());
            cart.setImage(food.getImage());
            cart.setUser(user);
            cart.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showToast("已添加");
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast(i+s);
                }
            });
        }else{
            Cart cart = new Cart();
            cart.setPrice(headline.getPrice());
            cart.setName(headline.getName());
            cart.setImage(headline.getImage());
            cart.setUser(user);
            cart.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showToast("已添加");
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast(i+s);
                }
            });
        }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        MyUser user = BmobUser.getCurrentUser(this,MyUser.class);
        if(user == null || !user.getIsAdministrator()){
            toolbar.getMenu().getItem(0).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, EditMenuActivity.class);
        if(TYPE_FOOD){
            intent.putExtra("food",food);
        }else{
            intent.putExtra("headline",headline);
        }
        startActivityForResult(intent,1);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null){
            if(TYPE_FOOD) {
                food = (Food) data.getSerializableExtra("food");
                setupFoodDataWithViews();
                getSupportActionBar().setTitle(food.getName());
            }else{
                headline = (Headline) data.getSerializableExtra("headline");
                setupHeadlineDataWithViews();
                getSupportActionBar().setTitle(headline.getName());
            }
        }
    }
    private void setupFoodDataWithViews(){
        description.setText(food.getDescription());
        Picasso.with(this).load(food.getImage()).error(R.drawable.no_image_available).into(image);
        String priceStr = "单价："+food.getPrice()+"元";
        price.setText(priceStr);
    }
    private void setupHeadlineDataWithViews(){
        description.setText(headline.getDescription());
        if(!headline.getImage().isEmpty()) {
            Picasso.with(this).load(headline.getImage()).error(R.drawable.no_image_available).into(image);
        }
        String priceStr = "单价："+headline.getPrice()+"元";
        price.setText(priceStr);
    }
}
