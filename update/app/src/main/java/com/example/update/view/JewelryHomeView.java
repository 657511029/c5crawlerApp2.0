package com.example.update.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;

public class JewelryHomeView extends ConstraintLayout {
    private static final String TAG = JewelryHomeView.class.getSimpleName();

    private Context context;
    private  String element;

    private int showView;

    private ImageView imageView;

    private TextView textView;

    private TextView jewelry_abrasion;

    private TextView jewelry_statTrak;

    private TextView jewelry_price;

    private TextView jewelry_quantity;

    public JewelryHomeView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public JewelryHomeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public JewelryHomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JewelryHomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context) {
        JewelryHomeView jewelryHomeView = (JewelryHomeView) LayoutInflater.from(context).inflate(R.layout.jewelry_home, this,true);
        imageView = (ImageView) jewelryHomeView.findViewById(R.id.image);
        textView = (TextView) jewelryHomeView.findViewById(R.id.home_jewelry_name);
        jewelry_abrasion = (TextView) jewelryHomeView.findViewById(R.id.jewelry_abrasion);
        jewelry_statTrak = (TextView) jewelryHomeView.findViewById(R.id.jewelry_statTrak);
        jewelry_price = (TextView) jewelryHomeView.findViewById(R.id.jewelry_price);
        jewelry_quantity = (TextView) jewelryHomeView.findViewById(R.id.jewelry_quantity);
    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }

    public void setDesc(String exteriorColor, String exteriorName, String qualityColor,String qualityName){

        jewelry_abrasion.setText(exteriorName);
        if(!TextUtils.isEmpty(exteriorColor)){
            jewelry_abrasion.setTextColor(Color.parseColor(exteriorColor));
        }
        jewelry_statTrak.setText(qualityName);
        if(!TextUtils.isEmpty(qualityColor)){
            jewelry_statTrak.setTextColor(Color.parseColor(qualityColor));
        }
    }

    public void setPrice(double price){
        jewelry_price.setText( "￥" + String.valueOf(price));
    }

    public void setQuantity(int quantity){
        jewelry_quantity.setText("在售 " + String.valueOf(quantity) + " 件");
    }

    public String getName(){
        return textView.getText().toString();
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
