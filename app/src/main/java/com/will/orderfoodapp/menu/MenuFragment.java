package com.will.orderfoodapp.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.viewpagerindicator.CirclePageIndicator;
import com.will.orderfoodapp.R;
import com.will.orderfoodapp.bean.Food;
import com.will.orderfoodapp.bean.Headline;
import com.will.orderfoodapp.bean.MyUser;
import com.will.orderfoodapp.menu.detail.FoodDetailActivity;
import com.will.orderfoodapp.menu.edit.EditMenuActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/4/22.
 */
public class MenuFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener,
        MyRecyclerAdapter.OnFoodClickListener{
    private SwipeRefreshLayout refreshLayout;
    private MyRecyclerAdapter recyclerAdapter;
    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private CirclePageIndicator indicator;
    private Thread autoChangeThread;
    private boolean autoChange = true;
    private boolean isManager = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_menu,null);
        //初始化
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_menu_swipe_refresh);
        recyclerAdapter = new MyRecyclerAdapter(getActivity());
        recyclerAdapter.setOnClickListener(this);
        viewPager = (ViewPager) view.findViewById(R.id.fragment_menu_view_pager);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_menu_recycler_view);
        indicator = (CirclePageIndicator) view.findViewById(R.id.view_pager_indicator);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        RecyclerViewHeader header = (RecyclerViewHeader) view.findViewById(R.id.header);
        header.attachTo(recyclerView);
        //
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        MyUser user = BmobUser.getCurrentUser(getActivity(),MyUser.class);
        if(user == null || !user.getIsAdministrator()){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditMenuActivity.class));
            }
        });
        //
        //
        getData();
        //
        refreshLayout.setOnRefreshListener(this);
        return view;
    }
    @Override
    public void onRefresh(){
        recyclerAdapter.refreshData(new MyRecyclerAdapter.OnFoodRefreshListener() {
            @Override
            public void onSuccess() {
                refreshLayout.setRefreshing(false);
            }
            @Override
            public void onFailure() {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getData(){
        BmobQuery<Headline> query = new BmobQuery<>();
        //query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(getActivity(), new FindListener<Headline>() {
            @Override
            public void onSuccess(List<Headline> list) {
                pagerAdapter = new PagerAdapter(getChildFragmentManager(),list);
                viewPager.setAdapter(pagerAdapter);
                indicator.setViewPager(viewPager);
                if(autoChangeThread == null){
                    pagerCarousel();
                }
            }
            @Override
            public void onError(int i, String s) {
                Log.e("错误码："+i,s);
                //101为class不存在错误码，说明服务器端没有此表，先在服务器创建两个空headline实例
                if(i == 101){
                    Headline headline1 = new Headline();
                    iniHeadline(headline1);
                    headline1.save(getActivity(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //创建第二个
                            Headline headline2 = new Headline();
                            iniHeadline(headline2);
                            headline2.save(getActivity(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    getData();
                                }
                                @Override
                                public void onFailure(int i, String s) {}
                            });
                        }
                        @Override
                        public void onFailure(int i, String s) {}
                    });
                }
            }
        });
    }
    public void refresh(){
        refreshLayout.setRefreshing(true);
        onRefresh();
    }
    @Override
    public void onFoodClick(Food food){
        Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
        intent.putExtra("food",food);
        startActivity(intent);
    }
    private void iniHeadline(Headline headline){
        headline.setName("");
        headline.setImage("");
        headline.setPrice("");
        headline.setDescription("");
    }

    /**
     * 自动轮播pager
     */
    private void pagerCarousel(){
        autoChangeThread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    SystemClock.sleep(5000);
                    if(autoChange){
                        changeViewPagerItem();
                    }
                }
            }
        });
        autoChangeThread.start();
    }
    private void changeViewPagerItem(){
       getActivity().runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if (viewPager.getCurrentItem() == 0){
                   viewPager.setCurrentItem(1,true);
               }else{
                   viewPager.setCurrentItem(0,true);
               }
           }
       });
    }
    public void onDestroy(){
        super.onDestroy();
       autoChange = false;
    }
}
