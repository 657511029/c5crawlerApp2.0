package com.example.update.view.rank;


import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.update.R;
import com.example.update.entity.Jewelry;
import com.example.update.entity.Rank_jewelry;

import java.util.List;
import java.util.Map;

/**
 * Created by zhaikun68 on 2018/3/5.
 * <p>
 * ListView演示Demo中的数据适配器
 */
public class RankListViewAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<Rank_jewelry> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public RankListViewAdapter(Context context, List<Rank_jewelry> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.rank_table_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Rank_jewelry rankJewelry = dataList.get(position);
        viewHolder.rank_item_image.setImageBitmap(rankJewelry.getBitmap());
        viewHolder.rank_item_name.setText(rankJewelry.getJewelryName());
        viewHolder.rank_item_price.setText("￥" + String.valueOf(rankJewelry.getPrice()));
        viewHolder.rank_item_count.setText("出:"+ String.valueOf(rankJewelry.getSell_number()) + " 求:" + String.valueOf(rankJewelry.getBuy_number()));
        viewHolder.rank_item_addPrice.setText(String.valueOf(rankJewelry.getBalance()));
        viewHolder.rank_item_addProportion.setText(String.valueOf(rankJewelry.getScale()) + "%");
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {

         ImageView rank_item_image;

         TextView rank_item_name;

         TextView rank_item_price;

         TextView rank_item_count;

         TextView rank_item_addPrice;

         TextView rank_item_addProportion;

        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
           rank_item_image = (ImageView) view.findViewById(R.id.rank_item_image);
            rank_item_name = (TextView) view.findViewById(R.id.rank_item_name);
            rank_item_price = (TextView) view.findViewById(R.id.rank_item_price);
            rank_item_count = (TextView) view.findViewById(R.id.rank_item_count);
            rank_item_addPrice = (TextView) view.findViewById(R.id.rank_item_addPrice);
            rank_item_addProportion = (TextView) view.findViewById(R.id.rank_item_addProportion);
        }
    }
}