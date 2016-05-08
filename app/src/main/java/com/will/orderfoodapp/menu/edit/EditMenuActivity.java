package com.will.orderfoodapp.menu.edit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.MainActivity;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.base.BaseActivity;
import com.will.orderfoodapp.bean.Food;
import com.will.orderfoodapp.bean.Headline;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Will on 2016/4/23.
 */
public class EditMenuActivity extends BaseActivity implements View.OnClickListener{
    private EditText editName, editPrice, editDescription;
    private ImageView image;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Food original;
    private Food newOne = new Food();
    private boolean isEdit;
    private boolean TYPE_HEADLINE;
    private Headline originalHeadline;
    private Headline newHeadline = new Headline();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        initializeViews();
    }
    private void initializeViews(){
        editName = (EditText) findViewById(R.id.edit_menu_name);
        editPrice = (EditText) findViewById(R.id.edit_menu_price);
        editDescription = (EditText) findViewById(R.id.edit_menu_description);
        progressBar = (ProgressBar) findViewById(R.id.edit_menu_progress_bar);
        image = (ImageView) findViewById(R.id.edit_menu_image);
        toolbar = (Toolbar) findViewById(R.id.edit_menu_toolbar);
        //
        image.setOnClickListener(this);
        //如果有food数据传入，则说明本界面应该是编辑已有条目，先导入其内容
        if(getIntent().hasExtra("food")){
            isEdit = true;
            original = (Food) getIntent().getSerializableExtra("food");
            //为newOne的图片赋值，反之，若edit界面未为image赋值，当返回detail界面时加载图片就会遇到null；
            newOne.setImage(original.getImage());
            //
            if(original.getName() != null){
                editName.setText(original.getName());
            }
            if(original.getPrice() != null){
                editPrice.setText(original.getPrice());
            }
            if(original.getImage() != null){
                Picasso.with(this).load(original.getImage()).into(image);
            }
            if(original.getDescription() != null){
                editDescription.setText(original.getDescription());
            }
        }else if (getIntent().hasExtra("headline")){
            TYPE_HEADLINE = true;
            originalHeadline = (Headline) getIntent().getSerializableExtra("headline");
            newHeadline.setImage(originalHeadline.getImage());
            //
            editName.setText(originalHeadline.getName());
            editPrice.setText(originalHeadline.getPrice());
            editDescription.setText(originalHeadline.getDescription());
            if(!originalHeadline.getImage().isEmpty()){
                Picasso.with(this).load(originalHeadline.getImage()).into(image);
            }
        }
        //初始化toolbar
        toolbar.setTitle("");
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_done,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(!TYPE_HEADLINE){
            String nameStr = editName.getText().toString();
            String priceStr = editPrice.getText().toString();
            String descriptionStr = editDescription.getText().toString();
            newOne.setName(nameStr);
            newOne.setPrice(priceStr);
            newOne.setDescription(descriptionStr);
            if(isEdit){
                newOne.update(this, original.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        //更新成功
                        Intent intent = new Intent();
                        intent.putExtra("food",newOne);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    @Override
                    public void onFailure(int i, String s) {}
                });
            }else{
                newOne.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(EditMenuActivity.this, MainActivity.class);
                        intent.putExtra("refresh",true);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onFailure(int i, String s) {}
                });
            }
        }else{
            String nameStr = editName.getText().toString();
            String priceStr = editPrice.getText().toString();
            String descriptionStr = editDescription.getText().toString();
            newHeadline.setDescription(descriptionStr);
            newHeadline.setName(nameStr);
            newHeadline.setPrice(priceStr);
            newHeadline.update(this, originalHeadline.getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(EditMenuActivity.this,MainActivity.class);
                    intent.putExtra("refresh_pager",true);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
        return true;
    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            progressBar.setVisibility(View.VISIBLE);
            toolbar.getMenu().getItem(0).setVisible(false);
            Uri uri = data.getData();
            image.setImageURI(uri);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,filePathColumn,null,null,null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            final BmobFile file = new BmobFile(new File(path));
            file.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    toolbar.getMenu().getItem(0).setVisible(true);
                    if(!TYPE_HEADLINE){
                        newOne.setImage(file.getFileUrl(EditMenuActivity.this));
                    }else{
                        newHeadline.setImage(file.getFileUrl(EditMenuActivity.this));
                    }
                }
                @Override
                public void onFailure(int i, String s) {}
            });
        }
    }
}
