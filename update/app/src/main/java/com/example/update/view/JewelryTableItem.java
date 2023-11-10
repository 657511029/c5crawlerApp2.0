package com.example.update.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;


public class JewelryTableItem extends ConstraintLayout {
    private static final String TAG = JewelryTableItem.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;

    private JewelryHomeView jewelryHomeViewLeft;

    private JewelryHomeView jewelryHomeViewRight;

    public JewelryTableItem(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public JewelryTableItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public JewelryTableItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JewelryTableItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        JewelryTableItem jewelryTableItem = (JewelryTableItem) LayoutInflater.from(context).inflate(R.layout.jewelry_table_item, this,true);
        jewelryHomeViewLeft = (JewelryHomeView) jewelryTableItem.findViewById(R.id.jewelry_table_item_left);
        jewelryHomeViewRight = (JewelryHomeView) jewelryTableItem.findViewById(R.id.jewelry_table_item_right);
    }

    public void setName(String leftName,String rightName){
        jewelryHomeViewLeft.setName(leftName);
        jewelryHomeViewRight.setName(rightName);
    }

    public String getName(){
        return jewelryHomeViewLeft.getName() + ": " + jewelryHomeViewRight.getName();
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

