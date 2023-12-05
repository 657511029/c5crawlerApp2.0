package com.example.update.view.loading;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.update.R;

public class LoadingLayout extends FrameLayout {


    private int mLoadingView;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0);
        try {
            mLoadingView = a.getResourceId(R.styleable.LoadingLayout_loadingView, R.layout.loading_layout);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(mLoadingView, this, true);
        } finally {
            a.recycle();
        }
    }

    /**
     * 布局加载完成后隐藏所有View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount() - 1; i++) {
            getChildAt(i).setVisibility(GONE);
        }
    }


    /**
     * 设置自定义布局的点击事件
     * @param resoureId
     * @param listener
     */
    public void setViewOncClickListener(int resoureId,OnClickListener listener) {
        findViewById(resoureId).setOnClickListener(listener);
    }

    /**
     * 设置自定义布局的view文本
     * @param resoureId
     * @param text
     */
    public void setViewText(int resoureId,String text){
        ((TextView)findViewById(resoureId)).setText(text);
    }

    /**
     * 设置自定义布局的image
     * @param resoureId
     * @param img
     */
    public void setViewImage(int resoureId,int img ){
        ((ImageView)findViewById(resoureId)).setImageResource(img);
    }

    /**
     * State View
     */
    /**
     * Loading view
     */
    public void showLoading() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }


    /**
     *
     * @param text
     */
    public void showLoading(String text) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                child.setVisibility(VISIBLE);
                ((TextView) child.findViewById(R.id.loading_text)).setText(text + "");
            } else {
                child.setVisibility(GONE);
            }
        }
    }
    /**
     * 展示内容
     */
    public void showContent() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i > 2 ) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }
}
