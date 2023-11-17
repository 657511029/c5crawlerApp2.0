package com.example.update.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.update.R;
import com.example.update.view.hangknife.HangknifeView;
import com.example.update.view.rank.RankView;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends ConstraintLayout {
    private static final String TAG = HomeView.class.getSimpleName();

    private Context context;

    private List<TextView> home_topBar_items;

    private ConstraintLayout home_main_item;

    private int home_main_itemId;

    private int showView;

    private HomeView homeView;

    private ConstraintLayout home_main_container;

    private JewelryListView jewelryListView;

    private RankView rankView;

    private HangknifeView hangknifeView;

    public HomeView(Context context) {
        super(context);
        this.context = context;
        initView(context);

    }
    public HomeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    public HomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initAttrs(context,attrs);
    }
    private void initView(Context context){
        homeView = (HomeView) LayoutInflater.from(context).inflate(R.layout.home, this,true);
        home_main_container = (ConstraintLayout)homeView.findViewById(R.id.home_main_container);
        jewelryListView = new JewelryListView(context);
        jewelryListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));



        rankView = new RankView(context);
        rankView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        hangknifeView  = new HangknifeView(context);
        hangknifeView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));


        home_main_container.addView(jewelryListView);
        home_topBar_items = new ArrayList<>();
        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item1));
        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item2));
        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item3));
//        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item3));
        home_main_item = jewelryListView;
        home_main_itemId = R.id.home_topBar_item1;
        clickItem(R.id.home_topBar_item1);
//        home_topBar_items.add((TextView) findViewById(R.id.home_topBar_item4));
        setTitleTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                TextView home_topBar_item = (TextView) findViewById(view.getId());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(getResources().getDrawable(R.drawable.border_active));
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setBackground(getResources().getDrawable(R.drawable.border));
                        clickItem(view.getId());
//                        chooseItem(view.getId());
                        break;
                }
                return true;
            }
        });
    }
    private void setTitleTouchListener(OnTouchListener onTouchListener) {
        for(TextView home_topBar_item: home_topBar_items){
            home_topBar_item.setOnTouchListener(onTouchListener);
        }
    }

    private void clickItem(int id){
        for(TextView home_topBar_item: home_topBar_items){
            if(home_topBar_item.getId() == id){
//                home_topBar_item.setTypeface(Typeface.DEFAULT_BOLD);
                home_topBar_item.setTextColor(this.getResources().getColor(R.color.home_topBar_item_active));
//                home_topBar_item.setBackgroundColor(this.getResources().getColor(R.color.home_topBar_item_background_active));
                home_topBar_item.setTextSize(18);
            }
            else {
//                home_topBar_item.setTypeface(Typeface.DEFAULT);
                home_topBar_item.setTextColor(this.getResources().getColor(R.color.home_topBar_item));
//                home_topBar_item.setBackgroundColor(this.getResources().getColor(R.color.home_topBar_item_background));
                home_topBar_item.setTextSize(15);
            }
        }
        chooseItem(id);
    }

    private void chooseItem(int id){
        if(id == home_main_itemId){
            return;
        }
        if(id == R.id.home_topBar_item1){
            chooseItem1(R.id.home_topBar_item1);
        }
        else if(id == R.id.home_topBar_item2){
            chooseItem2(R.id.home_topBar_item2);
        }
        else if(id == R.id.home_topBar_item3){
            chooseItem3(R.id.home_topBar_item3);
        }
    }

    private void chooseItem1(int id){
        home_main_container.removeView(home_main_item);
        home_main_container.addView(jewelryListView);
        home_main_item = jewelryListView;
        home_main_itemId = id;
    }

    private void chooseItem2(int id){
        home_main_container.removeView(home_main_item);
        home_main_container.addView(rankView);
//        JewelryListView jewelryListView = new JewelryListView(context);
//        jewelryListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        homeView.addView(jewelryListView);
        home_main_item =  rankView;
        home_main_itemId = id;
    }
    private void chooseItem3(int id){
        home_main_container.removeView(home_main_item);
        home_main_container.addView(hangknifeView);
//        JewelryListView jewelryListView = new JewelryListView(context);
//        jewelryListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        homeView.addView(jewelryListView);
        home_main_item =  hangknifeView;
        home_main_itemId = id;
    }
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar);
        //获取title_text属性
        //获取show_views属性，如果没有设置时默认为0x26
        showView = mTypedArray.getInt(R.styleable.HeaderBar_show_views, 0x26);
//        text_center.setTextColor(mTypedArray.getColor(R.styleable.HeaderBar_title_text_color, Color.WHITE));
        mTypedArray.recycle();
        showView(showView);
    }

    private void showView(int showView) {
        //将showView转换为二进制数，根据不同位置上的值设置对应View的显示或者隐藏。
        Long data = Long.valueOf(Integer.toBinaryString(showView));
    }



}
