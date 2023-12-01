package com.example.update.view.thelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.update.R;
import com.example.update.api.THelperApi;

import java.text.ParseException;
import java.util.Calendar;


public class TimingOrderView extends LinearLayout {
    private static final String TAG = TimingOrderView.class.getSimpleName();

    private TimingOrderView timingOrderView;

    private Context context;
    private  String element;

    private int showView;

    private TextView summer;

    private TextView winter;

    private String timing;

    private DateView dateView;




    public TimingOrderView(Context context,DateView dateView) {
        super(context);
        this.context = context;
        initView(context,dateView);

    }
    public TimingOrderView(Context context,DateView dateView, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context,dateView);
        initAttrs(context,attrs);
    }
    public TimingOrderView(Context context,DateView dateView, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context,dateView);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TimingOrderView(Context context,DateView dateView, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context,dateView);
        initAttrs(context,attrs);
    }
    private void initView(Context context,DateView dateView) {
        this.dateView = dateView;
        timingOrderView = (TimingOrderView) LayoutInflater.from(context).inflate(R.layout.t_helper_choose_menu_timing_order, this,true);
        summer = (TextView) timingOrderView.findViewById(R.id.t_helper_choose_menu_timing_order_summer);
        winter = (TextView) timingOrderView.findViewById(R.id.t_helper_choose_menu_timing_order_winter);
        initSummer();
        initWinter();
    }

    private void initSummer(){
        summer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("夏令时","夏令时");
                if(!timing.equals("夏令时")){
                    clickItem("夏令时");
                    dateView.init();
                }
            }
        });
    }

    private void initWinter(){
        winter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("冬令时","冬令时");
                if(!timing.equals("冬令时")){
                    clickItem("冬令时");
                    dateView.init();
                }
            }
        });
    }

    private void clickItem(String timing){
        Log.e("令时",timing);
        if(timing.equals("夏令时")){
            summer.setTextColor(context.getResources().getColor(R.color.white));
            summer.setBackground(context.getResources().getDrawable(R.drawable.timing_order_choose_mask));

            winter.setTextColor(Color.parseColor("#C8C8C8"));
            winter.setBackground(context.getResources().getDrawable(R.drawable.timing_order_choose_shape));


        }
        else {
            winter.setTextColor(context.getResources().getColor(R.color.white));
            winter.setBackground(context.getResources().getDrawable(R.drawable.timing_order_choose_mask));

            summer.setTextColor(Color.parseColor("#C8C8C8"));
            summer.setBackground(context.getResources().getDrawable(R.drawable.timing_order_choose_shape));

        }
        this.timing = timing;
        SharedPreferences sharedPreferences = context.getSharedPreferences("params", context.MODE_PRIVATE);
        //获取Editor对象的引用
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //将获取过来的值放入文件
        editor.putString("timingOrder", timing);
        //将获取过来的值放入文件
        editor.commit();

    }
    public void choose(String timingOrder){
        clickItem(timingOrder);
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
    private void toastMessage(String message){
        timingOrderView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });

    }

}


