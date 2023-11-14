package com.example.update.view.info;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;



public class InfoView extends ConstraintLayout {
    private static final String TAG = InfoView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;

    private InfoView infoView;



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

}

