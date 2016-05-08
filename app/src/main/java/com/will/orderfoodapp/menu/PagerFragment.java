package com.will.orderfoodapp.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Headline;
import com.will.orderfoodapp.menu.detail.FoodDetailActivity;

/**
 * Created by Will on 2016/4/22.
 */
public class PagerFragment extends Fragment implements View.OnClickListener{
    private Headline headline;
    /**
     * 因为fragment的传参机制，故如此传入headline数据
     * @param headline 对应的轮播数据
     * @return  返回存入参数后的fragment
     */
    public static PagerFragment getInstance( Headline headline){
        if(headline != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("headline", headline);
            PagerFragment fragment = new PagerFragment();
            fragment.setArguments(bundle);
            return fragment;
        }else{
            return new PagerFragment();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        headline = (Headline) (getArguments().getSerializable("headline"));
        String imagePath = headline.getImage();
        View view = inflater.inflate(R.layout.fragment_menu_pager, null);
        ImageView image = (ImageView) view.findViewById(R.id.fragment_menu_pager_image);
        image.setOnClickListener(this);
        if(!imagePath.isEmpty()) {
            Picasso.with(getParentFragment().getActivity()).load(headline.getImage()).placeholder(R.drawable.loading_image).
                    error(R.drawable.no_image_available).into(image);
        }else{
            image.setImageResource(R.drawable.loading_image);
        }
        return view;
    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(getParentFragment().getActivity(), FoodDetailActivity.class);
        intent.putExtra("headline",headline);
        startActivity(intent);
    }
}
