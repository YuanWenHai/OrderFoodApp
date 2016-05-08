package com.will.orderfoodapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.will.orderfoodapp.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by Will on 2016/5/6.
 */
public class PushService extends Service {
    @Override
    public void onCreate(){
        super.onCreate();
        setupPush();
    }
   @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent ){
        return null;
    }
    private void setupPush(){
        Bmob.initialize(this,"e8a8de0be0da48c05ba1ebf3360b34c6");
            final BmobRealTimeData rtd = new BmobRealTimeData();
            rtd.start(this, new ValueEventListener() {
                @Override
                public void onConnectCompleted() {
                    Log.e("connected","success");
                    if(rtd.isConnected()){
                        rtd.subTableUpdate("Order");
                        Log.e("isConnected",rtd.isConnected()+"");
                    }
                }
                @Override
                public void onDataChange(JSONObject jsonObject) {
                    Log.e("on data change","execute");
                    try{
                        JSONObject object = jsonObject.getJSONObject("data");
                        if(object.optString("state").equals("送餐中")){
                            showNotification();
                            Log.e("new data","received");
                        }
                    }catch (JSONException j){
                        j.printStackTrace();
                    }
                }
            });
    }
    private void showNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("有新的订单");
        builder.setContentText("点击查看");
        builder.setVibrate(new long[]{0,500});
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("push",true);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
