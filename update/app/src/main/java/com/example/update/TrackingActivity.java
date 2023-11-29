package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.update.service.FloatViewService;
import com.example.update.service.FloatingWindowService;
import com.example.update.service.TrackingService;
import com.example.update.view.tracking.TrackingAddJewelryListView;
import com.example.update.view.tracking.TrackingBlockJewelryListView;
import com.example.update.view.tracking.TrackingJewelryListView;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends AppCompatActivity {

    private Context context;

    private SwitchCompat tracking_switch;

    private List<TextView> tracking_topBar_items;

    private ConstraintLayout tracking_list_container;

    private ConstraintLayout tracking_main_item;

    private int tracking_main_itemId;

    private TrackingJewelryListView trackingJewelryListView;

    private TrackingBlockJewelryListView trackingBlockJewelryListView;

    private TrackingAddJewelryListView trackingAddJewelryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        initData();
        initActionBar("追踪饰品");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initComponent();

    }

    private void initData(){
        context = TrackingActivity.this;
    }
    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tracking_list_container = (ConstraintLayout) findViewById(R.id.tracking_list_container);

                trackingJewelryListView = new TrackingJewelryListView(context);
                trackingJewelryListView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                trackingBlockJewelryListView = new TrackingBlockJewelryListView(context);
                trackingBlockJewelryListView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                trackingAddJewelryListView = new TrackingAddJewelryListView(context);
                trackingAddJewelryListView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                tracking_list_container.addView(trackingJewelryListView);
                tracking_switch = (SwitchCompat) findViewById(R.id.tracking_switch);
                initSwitch();
                tracking_topBar_items = new ArrayList<>();
                tracking_topBar_items.add((TextView) findViewById(R.id.tracking_topBar_item1));
                tracking_topBar_items.add((TextView) findViewById(R.id.tracking_topBar_item2));
                tracking_topBar_items.add((TextView) findViewById(R.id.tracking_topBar_item3));

                tracking_main_item = trackingJewelryListView;
                tracking_main_itemId = R.id.tracking_topBar_item1;
                clickItem(R.id.tracking_topBar_item1);

                setTitleClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickItem(view.getId());
                    }
                });
            }
        });
    }
    private void setTitleClickListener(View.OnClickListener onClickListener) {
        for(TextView tracking_topBar_item: tracking_topBar_items){
            tracking_topBar_item.setOnClickListener(onClickListener);
        }
    }

    private void clickItem(int id){
        for(TextView tracking_topBar_item: tracking_topBar_items){
            if(tracking_topBar_item.getId() == id){
//                home_topBar_item.setTypeface(Typeface.DEFAULT_BOLD);
                tracking_topBar_item.setTextColor(this.getResources().getColor(R.color.home_topBar_item_active));
//                home_topBar_item.setBackgroundColor(this.getResources().getColor(R.color.home_topBar_item_background_active));
                tracking_topBar_item.setTextSize(18);
            }
            else {
//                home_topBar_item.setTypeface(Typeface.DEFAULT);
                tracking_topBar_item.setTextColor(this.getResources().getColor(R.color.home_topBar_item));
//                home_topBar_item.setBackgroundColor(this.getResources().getColor(R.color.home_topBar_item_background));
                tracking_topBar_item.setTextSize(15);
            }
        }
        chooseItem(id);
    }
    private void chooseItem(int id){
        if(id == tracking_main_itemId){
            return;
        }
        if(id == R.id.tracking_topBar_item1){
            chooseItem1(R.id.tracking_topBar_item1);
        }
        else if(id == R.id.tracking_topBar_item2){
            chooseItem2(R.id.tracking_topBar_item2);
        }
        else if(id == R.id.tracking_topBar_item3){
            chooseItem3(R.id.tracking_topBar_item3);
        }
    }

    private void chooseItem1(int id){
        tracking_list_container.removeView(tracking_main_item);
        tracking_list_container.addView(trackingJewelryListView);
        tracking_main_item = trackingJewelryListView;
        tracking_main_itemId = id;
    }

    private void chooseItem2(int id){
        tracking_list_container.removeView(tracking_main_item);
        tracking_list_container.addView(trackingBlockJewelryListView);
        tracking_main_item = trackingBlockJewelryListView;
        tracking_main_itemId = id;
    }
    private void chooseItem3(int id){
        tracking_list_container.removeView(tracking_main_item);
        tracking_list_container.addView(trackingAddJewelryListView);
        tracking_main_item = trackingAddJewelryListView;
        tracking_main_itemId = id;
    }



    private void initSwitch(){

        tracking_switch.setChecked(isOpenTracking());
        tracking_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NotificationManagerCompat manager = NotificationManagerCompat.from(TrackingActivity.this);
                    // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
                    boolean isOpened = manager.areNotificationsEnabled();
                    if(!isOpened){
                        Intent intent = new Intent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //android 8.0引导，引导到CHANNEL_ID对应的渠道设置下
                            intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID,"重要通知渠道");

                        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //android 5.0-7.0,引导到所有渠道设置下（单个渠道没有具体的设置）
                            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra("app_package", getPackageName());
//                intent.putExtra("app_uid", getApplicationInfo().uid);
                        } else {
                            //其他
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                        }
                        startActivity(intent);
                        tracking_switch.setChecked(false);
                        return;
                    }
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Android 8.0及以上
                        NotificationChannel channel = mNotificationManager.getNotificationChannel("重要通知渠道");//CHANNEL_ID是自己定义的渠道ID
                        if (channel.getImportance() == NotificationManager.IMPORTANCE_DEFAULT) {//未开启
                            // 跳转到设置页面
                            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                            startActivity(intent);
                            tracking_switch.setChecked(false);
                            return;
                        }
                    }

                    if (!Settings.canDrawOverlays(TrackingActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:" + getPackageName()));
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                startActivityForResult(intent, 1);
                        tracking_switch.setChecked(false);
                        return;
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    //获取Editor对象的引用
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //将获取过来的值放入文件
                    editor.putString("tracking", "true");
                    editor.commit();
                    controlTracking(true);
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    //获取Editor对象的引用
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //将获取过来的值放入文件
                    editor.putString("tracking", "false");
                    editor.commit();
                    controlTracking(false);
                }
            }
        });
    }
    private void initActionBar(String menuTitle){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.title_layout);//设置标题样式
            TextView textView = (TextView) actionBar.getCustomView().findViewById(R.id.display_title);//获取标题布局的textview
            textView.setText(menuTitle);//设置标题名称，menuTitle为String字符串
            actionBar.setHomeButtonEnabled(true);//设置左上角的图标是否可以点击
            actionBar.setDisplayHomeAsUpEnabled(true);//给左上角图标的左边加上一个返回的图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_icon_foreground);
            actionBar.setDisplayShowCustomEnabled(true);// 使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void controlTracking(boolean control){
        if(control){
//            Intent intent = new Intent(this, FloatingWindowService.class);
//
//            startService(intent);
            Intent intent = new Intent(this, FloatViewService.class);

            startService(intent);
            Intent intent2 = new Intent(this, TrackingService.class);
            startService(intent2);


        }
        else {
//            Intent intent = new Intent(this, FloatingWindowService.class);
//
//            stopService(intent);
            Intent intent = new Intent(this, FloatViewService.class);

            stopService(intent);
            Intent intent2 = new Intent(this, TrackingService.class);
            stopService(intent2);
        }
    }
    private boolean userExist(){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        if(user.equals("")){
            return false;
        }
        return true;
    }
    private boolean isOpenTracking(){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String tracking = sharedPreferences.getString("tracking","");
        if(tracking.equals("") || tracking.equals("false")){
            return false;
        }
        return true;
    }
}