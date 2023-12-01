package com.example.update.view.thelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;
import com.example.update.api.THelperApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateView extends LinearLayout {
    private static final String TAG = DateView.class.getSimpleName();

    private DateView dateView;

    private Context context;
    private  String element;

    private int showView;

    private TextView startDate;

    private long startTime;

    private TextView endDate;

    private long endTime;

    private TextView clean;



    public DateView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public DateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public DateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        dateView = (DateView) LayoutInflater.from(context).inflate(R.layout.t_helper_choose_menu_date, this,true);
        startDate = (TextView) dateView.findViewById(R.id.t_helper_choose_menu_date_startTime);
        endDate = (TextView) dateView.findViewById(R.id.t_helper_choose_menu_date_endTime);
        clean = (TextView)dateView.findViewById(R.id.t_helper_choose_menu_date_clean);
        startTime = -1;
        endTime = -1;
        initStartDate();
        initEndDate();
        initClean();
    }

    private void initStartDate(){
        startDate.setOnClickListener(new OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        SharedPreferences sharedPreferences = context.getSharedPreferences("params", Context.MODE_PRIVATE);
                        String timingOrder = sharedPreferences.getString("timingOrder","冬令时");
                        if(startDate.getText().toString().equals("开始日期")){
                            try {
                                long newStartTime = THelperApi.getDataTime(year,month,dayOfMonth,timingOrder);
                                if(newStartTime > THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH) - 1,timingOrder)){
                                    toastMessage("开始时间不能大于前一天");
                                    return;
                                }
                                startTime = newStartTime;
                                endTime = THelperApi.getDataTime(year,month,dayOfMonth + 1,timingOrder);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            startDate.setText(year + "-" + month + "-" + dayOfMonth);
                            Date date = new Date(endTime * 1000);
                            endDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(date).toString());
                            startDate.setTextColor(Color.BLACK);
                            endDate.setTextColor(Color.BLACK);
                        }
                        else {
                            try {
                                long newStartTime = THelperApi.getDataTime(year,month,dayOfMonth,timingOrder);
                                if(newStartTime >= endTime){
                                    toastMessage("开始时间不能大于等于结束时间");
                                    return;
                                }
                                startTime = newStartTime;
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            startDate.setText(year + "-" + month + "-" + dayOfMonth);
                        }
                    }
                };
                new DatePickerDialog(context,3,onDateSetListener,now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initEndDate(){
        endDate.setOnClickListener(new OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        SharedPreferences sharedPreferences = context.getSharedPreferences("params", Context.MODE_PRIVATE);
                        String timingOrder = sharedPreferences.getString("timingOrder","冬令时");
                        if(endDate.getText().toString().equals("结束日期")){
                            try {
                                long newEndTime = THelperApi.getDataTime(year,month,dayOfMonth,timingOrder);
                                if(newEndTime > THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),timingOrder)){
                                    toastMessage("结束时间不能大于当前日期");
                                    return;
                                }
                                endTime = newEndTime;
                                startTime = THelperApi.getDataTime(year,month,dayOfMonth - 1,timingOrder);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Date date = new Date(startTime * 1000);
                            startDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(date).toString());
                            endDate.setText(year + "-" + month + "-" + dayOfMonth);
                            startDate.setTextColor(Color.BLACK);
                            endDate.setTextColor(Color.BLACK);
                        }
                        else {
                            try {
                                long newEndTime = THelperApi.getDataTime(year,month,dayOfMonth,"冬令时");
                                if(newEndTime > THelperApi.getDataTime(now.get(Calendar.YEAR),now.get(Calendar.MONTH) + 1,now.get(Calendar.DAY_OF_MONTH),"冬令时")){
                                    toastMessage("结束时间不能大于当前日期");
                                    return;
                                }
                                if(startTime >= newEndTime){
                                    toastMessage("结束日期不能小于等于开始日期");
                                    return;
                                }
                                endTime = newEndTime;
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            endDate.setText(year + "-" + month + "-" + dayOfMonth);
                        }
                    }
                };
                new DatePickerDialog(context,3,onDateSetListener,now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initClean(){
        clean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    public void init(){
        startDate.setText("开始日期");
        endDate.setText("结束日期");
        startDate.setTextColor(Color.parseColor("#C8C8C8"));
        endDate.setTextColor(Color.parseColor("#C8C8C8"));
        startTime = -1;
        endTime = -1;
    }

    public long getStartTime(){
        return startTime;
    }

    public long getEndTime(){
        return endTime;
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
        dateView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });

    }

}

