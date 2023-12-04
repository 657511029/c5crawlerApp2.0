package com.example.update.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.update.R;
import com.example.update.entity.Hangknife_jewelry;
import com.example.update.entity.OrdersItem;

import java.util.List;

public class THelperAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<OrdersItem> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public THelperAdapter(Context context, List<OrdersItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.t_helper_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrdersItem ordersItem = dataList.get(position);
        viewHolder.setName(ordersItem.getJewelryName());
        viewHolder.setImage(ordersItem.getImageUrl());
        viewHolder.setDesc(ordersItem.getExteriorColor(),ordersItem.getExteriorName(),ordersItem.getQualityColor(),ordersItem.getQualityName());
        viewHolder.t_helper_jewelry_list_item_average.setText("均价: " + String.format("%.2f", ordersItem.getAverage()));
        viewHolder.t_helper_jewelry_list_item_count.setText("售出: " + ordersItem.getList().size() + " 件");
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {


        TextView t_helper_jewelry_abrasion;

        TextView t_helper_jewelry_statTrak;

        ImageView t_helper_image;

        TextView t_helper_jewelry_list_item_name;

        TextView t_helper_jewelry_list_item_average;

        TextView t_helper_jewelry_list_item_count;

        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            t_helper_jewelry_abrasion = (TextView) view.findViewById(R.id.t_helper_jewelry_abrasion);
            t_helper_jewelry_statTrak = (TextView) view.findViewById(R.id.t_helper_jewelry_statTrak);

            t_helper_image = (ImageView) view.findViewById(R.id.t_helper_image);

            t_helper_jewelry_list_item_name = (TextView) view.findViewById(R.id.t_helper_jewelry_list_item_name);

            t_helper_jewelry_list_item_average = (TextView) view.findViewById(R.id.t_helper_jewelry_list_item_average);

            t_helper_jewelry_list_item_count = (TextView) view.findViewById(R.id.t_helper_jewelry_list_item_count);

        }
        public void setName(String name){
            t_helper_jewelry_list_item_name.setText(name);
        }

        public void setImage(String url){
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .into(t_helper_image);
        }

        public void setDesc(String exteriorColor, String exteriorName, String qualityColor,String qualityName){

            t_helper_jewelry_abrasion.setText(exteriorName);
            if(!TextUtils.isEmpty(exteriorColor)){
                t_helper_jewelry_abrasion.setTextColor(Color.parseColor(exteriorColor));
            }
            t_helper_jewelry_statTrak.setText(qualityName);
            if(!TextUtils.isEmpty(qualityColor)){
                t_helper_jewelry_statTrak.setTextColor(Color.parseColor(qualityColor));
            }
        }
    }
}

