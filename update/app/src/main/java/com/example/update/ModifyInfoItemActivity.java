package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.adapter.InfoArrayAdapter;
import com.example.update.adapter.InfoItemArrayAdapter;
import com.example.update.api.InfoApi;
import com.example.update.entity.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyInfoItemActivity extends AppCompatActivity {

    private String title;

    private String tips;

    private String[] infoName;

    private String[] infoMessage;

    private ListView modify_info_item_list;

    private TextView modify_info_item_tips;

    private Button modify_info_item_submit;

    private List<Map<String,String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info_item);
        initData();
        initActionBar(title);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initComponent();
    }

    private void initData(){
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        tips = intent.getStringExtra("tips");
        infoName = intent.getStringArrayExtra("infoName");
        infoMessage = intent.getStringArrayExtra("infoMessage");
        for(int i = 0;i < infoName.length;i++){
            Map map = new HashMap();
            map.put(infoName[i],infoMessage[i]);
            dataList.add(map);
        }
    }

    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                modify_info_item_list = (ListView) findViewById(R.id.modify_info_item_list);
                InfoItemArrayAdapter adapter=new InfoItemArrayAdapter(ModifyInfoItemActivity.this,dataList);
                //5、将适配器加载到控件中
                modify_info_item_list.setAdapter(adapter);
                modify_info_item_tips = (TextView) findViewById(R.id.modify_info_item_tips);
                modify_info_item_tips.setText(tips);
                modify_info_item_submit = (Button) findViewById(R.id.modify_info_item_submit);
                modify_info_item_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo userInfo = new UserInfo();
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        String user = sharedPreferences.getString("user","");
                        for(int i = 0;i < adapter.getCount();i++){
                            View view = modify_info_item_list.getChildAt(i-modify_info_item_list.getFirstVisiblePosition());
                            String name = ((TextView)view.findViewById(R.id.modifyInfoItem_list_item_name)).getText().toString();
                            String message =  ((TextView)view.findViewById(R.id.modifyInfoItem_list_item_message)).getText().toString();
                            if(message == null || message.equals("")){
                                toastMessage("变更内容不能为空");
                                return;
                            }
                            if(name.equals("昵称")){
                                userInfo.setUserName(message);
                                continue;
                            }
                            if(name.equals("uu账户")){
                                userInfo.setUuAccount(message);
                                continue;
                            }
                            if(title.equals("修改c5追踪比例")){
                                if(name.equals("0-50")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale1_c5(message);
                                    continue;
                                }
                                if(name.equals("50-100")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale2_c5(message);
                                    continue;
                                }
                                if(name.equals("100-500")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale3_c5(message);
                                    continue;
                                }
                                if(name.equals("500-")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale4_c5(message);
                                    continue;
                                }
                            }
                            if(title.equals("修改ig追踪比例")){
                                if(name.equals("0-50")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale1_ig(message);
                                    continue;
                                }
                                if(name.equals("50-100")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale2_ig(message);
                                    continue;
                                }
                                if(name.equals("100-500")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale3_ig(message);
                                    continue;
                                }
                                if(name.equals("500-")){
                                    if(!message.matches("(^[1-9]\\d*\\.\\d+$|^0\\.\\d+$|^[1-9]\\d*$|^0$)")){
                                        toastMessage("百分比填写非法，更新失败");
                                        return;
                                    }
                                    userInfo.setScale4_ig(message);
                                    continue;
                                }
                            }
                        }
                        if(title.equals("修改昵称")){
                            submit(user,userInfo,"昵称");
                        }
                        else if(title.equals("修改uu账户绑定")){
                            submit(user,userInfo,"uu账户");
                        }
                        else if(title.equals("修改c5追踪比例")){
                            submit(user,userInfo,"c5");
                        }
                        else if(title.equals("修改ig追踪比例")){
                            submit(user,userInfo,"ig");
                        }
                    }
                });
            }
        });
    }

    private void submit(String user,UserInfo userInfo,String flag){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!InfoApi.submitInfo(user,userInfo,flag)){
                    toastMessage("修改失败,用户不存在");
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastMessage("修改成功");
                        finish();
                    }
                });
            }
        });
        thread.start();

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
    private void toastMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModifyInfoItemActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
}