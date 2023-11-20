package com.example.update;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.update.adapter.InfoArrayAdapter;
import com.example.update.api.InfoApi;
import com.example.update.entity.UserInfo;
import com.example.update.service.FloatingWindowService;
import com.example.update.service.TrackingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyInfoActivity extends AppCompatActivity {

    private ListView listView;

    private List<Map<String,String>> info_list;

    private String[] info_name_list = {"昵称","uu账户","追踪比例","注销账户"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        initActionBar("信息维护");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initListView(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String user = sharedPreferences.getString("user","");
                UserInfo userInfo = InfoApi.getUserInfo(user);
                if(userInfo == null){
                    toastMessage("用户不存在");
                    finish();
                    return;
                }
                info_list = new ArrayList<>();
                Map map = new HashMap();
                map.put(info_name_list[0],userInfo.getUserName());
                info_list.add(map);
                Map map1 = new HashMap();
                map1.put(info_name_list[1],"保密");
                info_list.add(map1);
                Map map2 = new HashMap();
                map2.put(info_name_list[2],"0-50: " + userInfo.getScale1() + "\n" + "50-100: " + userInfo.getScale2() + "\n" + "100-500: " + userInfo.getScale3() + "\n"+ "500-: " + userInfo.getScale4() + "\n");
//                map2.put("0-50",userInfo.getScale1());
//                map2.put("50-100",userInfo.getScale2());
//                map2.put("100-",userInfo.getScale3());
                info_list.add(map2);
                Map map3 = new HashMap();
                map3.put(info_name_list[3],"");
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
                                String function_name = ((TextView) view.findViewById(R.id.info_list_item_name)).getText().toString();
                                if(function_name.equals(info_name_list[0])){
                                    toastMessage("修改昵称功能尚未开放");
                                    return;
                                }
                                if(function_name.equals(info_name_list[1])){
                                    toastMessage("uu账户解绑功能尚未开放");
                                    return;
                                }
                                if(function_name.equals(info_name_list[2])){
                                    if(userExist()){
                                        Intent intent = new Intent(ModifyInfoActivity.this, ModifyInfoItemActivity.class);
                                        intent.putExtra("title","修改追踪比例");
                                        intent.putExtra("tips","请输入大于0的数;\n0.01代表追踪1%利润以上的饰品");
                                        intent.putExtra("infoName", new String[]{"0-50","50-100","100-500","500-"});
                                        intent.putExtra("infoMessage", new String[]{userInfo.getScale1(),userInfo.getScale2(),userInfo.getScale3(),userInfo.getScale4()});
                                        startActivity(intent);
                                        return;
                                    }else {
                                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                                .setMessage("账号尚未登录")
                                                .setPositiveButton("确定", null)
                                                .show();
                                        return;
                                    }

                                }
                                if(function_name.equals(info_name_list[3])){
                                    if(userExist()){
                                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                                .setMessage("是否注销账号")
                                                .setPositiveButton("注销账号", new deleteUserClick())
                                                .setNegativeButton("取消", new cancelClick())
                                                .show();
                                        return;
                                    }else {
                                        new AlertDialog.Builder(ModifyInfoActivity.this)
                                                .setMessage("账号尚未登录")
                                                .setPositiveButton("确定", null)
                                                .show();
                                        return;
                                    }

                                }
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
                        Intent intent = new Intent(ModifyInfoActivity.this, FloatingWindowService.class);
                        Intent intent2 = new Intent(ModifyInfoActivity.this, TrackingService.class);
                        stopService(intent);
                        stopService(intent2);
                        
                        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                        //获取Editor对象的引用
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //将获取过来的值放入文件
                        editor.putString("user", "");
                        editor.commit();
                        Intent intent3 = new Intent(ModifyInfoActivity.this,MainActivity.class);
                        intent3.putExtra("point",3);
                        startActivity(intent3);
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
    @Override
    protected void onResume() {
        super.onResume();
        initComponent();
        //重新获取数据的逻辑，此处根据自己的要求回去
        //显示信息的界面

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