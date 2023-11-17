package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.api.InfoApi;
import com.example.update.service.FloatingWindowService;
import com.example.update.service.TrackingService;
import com.example.update.view.HomeView;
import com.example.update.view.info.InfoView;

public class SettingActivity extends AppCompatActivity {

    private ListView listView;

    private String[] function_list = {"账号与安全","关于capoo导购","消息提醒","退出登录"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initActionBar("设置");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initComponent();
    }

    private void initListView(){


        ArrayAdapter<String> adapter=new ArrayAdapter<>(SettingActivity.this,R.layout.setting_list_item,function_list);
        //5、将适配器加载到控件中
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
               String function_name = ((TextView) view).getText().toString();
               if(function_name.equals(function_list[0])){
                   if(userExist()){
                       Intent intent = new Intent(SettingActivity.this,ModifyInfoActivity.class);
                       startActivity(intent);
                       return;
                   }else {
                       new AlertDialog.Builder(SettingActivity.this)
                               .setMessage("账号尚未登录")
                               .setPositiveButton("确定", null)
                               .show();
                       return;
                   }

               }
               if(function_name.equals(function_list[1])){
                   new AlertDialog.Builder(SettingActivity.this)
                           .setMessage("产品未开发完全")
                           .setPositiveButton("确定", null)
                           .show();
                   return;
               }
                if(function_name.equals(function_list[2])){
                    toastMessage("工程师正在努力开发中");
                    return;
                }
                if(function_name.equals(function_list[3])){
                    if(userExist()){
                        new AlertDialog.Builder(SettingActivity.this)
                                .setMessage("是否退出登录")
                                .setPositiveButton("退出登录", new loginOutClick())
                                .setNegativeButton("取消", new cancelClick())
                                .show();
                        return;
                    }else {
                        new AlertDialog.Builder(SettingActivity.this)
                                .setMessage("账号尚未登录")
                                .setPositiveButton("确定", null)
                                .show();
                        return;
                    }

                }
            }
        });
    }

    private void loginOut(){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        //获取Editor对象的引用
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //将获取过来的值放入文件
        editor.putString("user", "");
        editor.commit();
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        intent.putExtra("point",3);
        startActivity(intent);
    }
    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView = (ListView) findViewById(R.id.setting_list);
                initListView();
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
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
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
    class cancelClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog,int which)
        {
            dialog.cancel();
        }

    }
    class loginOutClick implements DialogInterface.OnClickListener{
        EditText user_edit;
        @Override
        public void onClick(DialogInterface dialog,int which)
        {
            Intent intent = new Intent(SettingActivity.this, FloatingWindowService.class);
            Intent intent2 = new Intent(SettingActivity.this, TrackingService.class);
            stopService(intent);
            stopService(intent2);
            loginOut();
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
    private void toastMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
}