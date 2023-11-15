package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.adapter.InfoArrayAdapter;
import com.example.update.api.InfoApi;
import com.example.update.entity.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyInfoActivity extends AppCompatActivity {

    private ListView listView;

    private List<Map<String,String>> info_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        initActionBar("信息维护");
        initComponent();
    }

    private void initListView(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String user = sharedPreferences.getString("user","");
                UserInfo userInfo = InfoApi.getUserInfo(user);
                info_list = new ArrayList<>();
                Map map = new HashMap();
                map.put("昵称",userInfo.getUserName());
                info_list.add(map);
                Map map1 = new HashMap();
                map1.put("uu账户","保密");
                info_list.add(map1);
                Map map2 = new HashMap();
                map2.put("0-50",userInfo.getScale1());
                map2.put("50-100",userInfo.getScale2());
                map2.put("100-",userInfo.getScale3());
                info_list.add(map2);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InfoArrayAdapter adapter=new InfoArrayAdapter(ModifyInfoActivity.this,info_list);
                        //5、将适配器加载到控件中
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
//                                String function_name = ((TextView) view).getText().toString();
//                                if(function_name.equals(info_list[0])){
//                                    toastMessage("工程师正在努力开发中");
//                                    return;
//                                }
//                                if(function_name.equals(info_list[1])){
//                                    toastMessage("工程师正在努力开发中");
//                                    return;
//                                }
//                                if(function_name.equals(info_list[2])){
//                                    toastMessage("工程师正在努力开发中");
//                                    return;
//                                }
//                                if(function_name.equals(info_list[3])){
//                                    if(userExist()){
//                                        new AlertDialog.Builder(ModifyInfoActivity.this)
//                                                .setMessage("是否注销账号")
//                                                .setPositiveButton("注销账号", new deleteUserClick())
//                                                .setNegativeButton("取消", new cancelClick())
//                                                .show();
//                                        return;
//                                    }else {
//                                        new AlertDialog.Builder(ModifyInfoActivity.this)
//                                                .setMessage("账号尚未登录")
//                                                .setPositiveButton("确定", null)
//                                                .show();
//                                        return;
//                                    }
//
//                                }
                            }
                        });
                    }
                });
            }
        });
        thread.start();
    }

    private void deleteUser(String user){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!InfoApi.deleteUser(user)){
                    toastMessage("注销失败,用户不存在");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ModifyInfoActivity.this,MainActivity.class);
                        intent.putExtra("point",3);
                        startActivity(intent);
                    }
                });
            }
        });
        thread.start();

    }
    private void initComponent(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView = (ListView) findViewById(R.id.modify_info_list);
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
    class deleteUserClick implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog,int which)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            String user = sharedPreferences.getString("user","");
            if(user.equals("")){
                toastMessage("注销失败,用户未登录");
                return;
            }
            deleteUser(user);
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
                Toast.makeText(ModifyInfoActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
}