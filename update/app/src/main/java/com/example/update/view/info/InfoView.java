package com.example.update.view.info;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;
import com.example.update.SettingActivity;
import com.example.update.api.InfoApi;


public class InfoView extends ConstraintLayout {
    private static final String TAG = InfoView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;

    private InfoView infoView;

    private ImageView info_top_tool_setting;

    private TextView info_top_function_tracking;

    private TextView info_top_person_user;

    private TextView info_top_function_2;

    private TextView info_top_function_3;

    private LinearLayout login;



    public InfoView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public InfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public InfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public InfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        infoView = (InfoView) LayoutInflater.from(context).inflate(R.layout.info, this,true);
        info_top_function_tracking = (TextView) infoView.findViewById(R.id.info_top_function_tracking);
        initTracking();
        info_top_function_2 = (TextView)infoView.findViewById(R.id.info_top_function_2);
        initFunction2();
        info_top_function_3 = (TextView)infoView.findViewById(R.id.info_top_function_3);
        initFunction3();
        info_top_tool_setting = (ImageView)infoView.findViewById(R.id.info_top_tool_setting);
        initSetting();
        info_top_person_user = (TextView) infoView.findViewById(R.id.info_top_person_user);
        initUser();
    }

    private void initTracking(){
        info_top_function_tracking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userExist()){
                    toastMessage("尚未登录");
                    return;
                }
                toastMessage("工程师正在努力开发");
            }
        });
    }
    private void initFunction2(){
        info_top_function_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userExist()){
                    toastMessage("尚未登录");
                    return;
                }
                toastMessage("工程师正在努力开发");
            }
        });
    }
    private void initFunction3(){
        info_top_function_3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userExist()){
                    toastMessage("尚未登录");
                    return;
                }
                toastMessage("工程师正在努力开发");
            }
        });
    }
    private void initSetting(){
        info_top_tool_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
    private void initUser(){
        if(userExist()){
            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            String user = sharedPreferences.getString("user","");
            info_top_person_user.setText(user);
            info_top_person_user.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialog();
                }
            });
        }
        else {
            info_top_person_user.setText("请先登录");
            info_top_person_user.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialog();
                }
            });
        }
    }

    private void initDialog(){
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//                startActivityForResult(intent, 1);
            return;
        }
        login = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.login,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(userExist()){
            builder.setTitle("切换账号");
        }else {
            builder.setTitle("登录");
        }

        builder.setView(login);
        builder.setIcon(R.drawable.capoo);

        builder.setPositiveButton("登录",new loginClick());
        builder.setNegativeButton("取消",new cancelClick());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
        //获取title_text属性
        //获取show_views属性，如果没有设置时默认为0x26
//        showView = mTypedArray.getInt(R.styleable.HeaderBar_show_views, 0x26);
//        text_center.setTextColor(mTypedArray.getColor(R.styleable.HeaderBar_title_text_color, Color.WHITE));
//        mTypedArray.recycle();
//        showView(showView);
    }

    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }
    private boolean userExist(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        if(user.equals("")){
            return false;
        }
        return true;
    }
    private void toastMessage(String message){
        infoView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
    class cancelClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog,int which)
        {
            dialog.cancel();
        }

    }
    class loginClick implements DialogInterface.OnClickListener{
        EditText user_edit;
        @Override
        public void onClick(DialogInterface dialog,int which)
        {
            user_edit = (EditText)login.findViewById(R.id.user_edit);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(InfoApi.login(user_edit.getText().toString())){
                        changeUser(user_edit.getText().toString());
                        toastMessage("登录成功");
                        dialog.cancel();
                        closeInput();
                    }

                    else{
                        toastMessage("登录失败");
                        closeInput();
                    }
                }
            });
            thread.start();
        }
    }
    private void changeUser(String user){
        infoView.post(new Runnable() {
            @Override
            public void run() {
                info_top_person_user.setText(user);
                SharedPreferences sharedPreferences = context.getSharedPreferences("user", context.MODE_PRIVATE);
                //获取Editor对象的引用
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //将获取过来的值放入文件
                editor.putString("user", user);
                editor.commit();
            }
        });

    }
    private void closeInput(){
        infoView.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(infoView.getApplicationWindowToken(),0);
            }
        });

    }
}

