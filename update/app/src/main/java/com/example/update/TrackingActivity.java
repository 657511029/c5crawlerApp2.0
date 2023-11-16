package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.update.service.FloatingWindowService;
import com.example.update.service.TrackingService;

public class TrackingActivity extends AppCompatActivity {

    private SwitchCompat tracking_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        initActionBar("追踪饰品");
        initComponent();
    }
    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tracking_switch = (SwitchCompat) findViewById(R.id.tracking_switch);
                initSwitch();
            }
        });
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
                    editor.putString("track", "true");
                    editor.commit();
                    controlTracking(true);
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    //获取Editor对象的引用
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //将获取过来的值放入文件
                    editor.putString("track", "false");
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
            Intent intent = new Intent(this, FloatingWindowService.class);
            Intent intent2 = new Intent(this, TrackingService.class);
            startService(intent);
            startService(intent2);
        }
        else {
            Intent intent = new Intent(this, FloatingWindowService.class);
            Intent intent2 = new Intent(this, TrackingService.class);
            stopService(intent);
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
        String user = sharedPreferences.getString("tracking","");
        if(user.equals("") || user.equals("false")){
            return false;
        }
        return true;
    }
}