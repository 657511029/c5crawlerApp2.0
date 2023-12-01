package com.example.update.view.thelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import com.example.update.R;
import com.example.update.api.THelperApi;

import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinNumberView extends LinearLayout {
    private static final String TAG = MinNumberView.class.getSimpleName();

    private MinNumberView minNumberView;

    private Context context;
    private  String element;

    private int showView;

    private TextView min_number;

    private int number;


    private TextView clean;



    public MinNumberView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public MinNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public MinNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MinNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        minNumberView = (MinNumberView) LayoutInflater.from(context).inflate(R.layout.t_helper_choose_menu_min_number, this,true);
        min_number = (EditText) minNumberView.findViewById(R.id.t_helper_choose_menu_min_number_count);
        initMinNumber();
        number = 0;
        clean = (TextView) minNumberView.findViewById(R.id.t_helper_choose_menu_min_number_clean);
        initClean();
    }

    private void initMinNumber(){
        min_number.addTextChangedListener(new TextWatcher() {
            //改变之前会执行的方法
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            //实时监听
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tel = min_number.getText().toString();
                if(TextUtils.isEmpty(tel)){
                    return;
                }
                Pattern p = Pattern.compile("[0-9]{1,7}");//正则
                Matcher m = p.matcher(tel);
                if (!m.matches()) {
                    //提示
                    toastMessage("输入数量语法错误或超出最大数量限制");
                    setNumber(0);
                }
                else {
                  number = Integer.parseInt(tel);
                }
            }
            //改变之后会执行的方法、注意,在此写验证方法将导致死循环,请不要在这里进行验证操作
            @Override
            public void afterTextChanged(Editable s) { }
        });
        min_number.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        min_number.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_4875C6)));
                    } else {
                        // For older versions of Android, you can try using the support library
                        ViewCompat.setBackgroundTintList(min_number, ColorStateList.valueOf(getResources().getColor(R.color.blue_4875C6)));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        min_number.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_C8C8C8)));
                    } else {
                        // For older versions of Android, you can try using the support library
                        ViewCompat.setBackgroundTintList(min_number, ColorStateList.valueOf(getResources().getColor(R.color.gray_C8C8C8)));
                    }
                }
            }
        });
    }


    private void initClean(){
        clean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                min_number.setText("");
                number = 0;
            }
        });
    }

    public int getNumber(){
        return number;
    }

    public void setNumber(int number){
        this.number = number;
        min_number.setText(String.valueOf(number));
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
        minNumberView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });

    }

}


