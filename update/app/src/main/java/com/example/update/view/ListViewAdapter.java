package com.example.update.view;


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

import java.util.List;
import java.util.Map;

/**
 * Created by zhaikun68 on 2018/3/5.
 * <p>
 * ListView演示Demo中的数据适配器
 */
public class ListViewAdapter extends BaseAdapter {

    private Context context;//上下文对象
    private List<Map<String, Jewelry>> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public ListViewAdapter(Context context, List<Map<String,Jewelry>> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.jewelry_table_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Jewelry jewelry_left = dataList.get(position).get("jewelry_left");
        Jewelry jewelry_right = dataList.get(position).get("jewelry_right");
        if(TextUtils.isEmpty(jewelry_left.getJewelryName())){
            viewHolder.jewelryHomeViewLeft.setVisibility(View.GONE);

        }{
            viewHolder.jewelryHomeViewLeft.setName(jewelry_left.getShortName());
            viewHolder.jewelryHomeViewLeft.setImage(jewelry_left.getBitmap() );
            viewHolder.jewelryHomeViewLeft.setDesc(jewelry_left.getExteriorColor(),jewelry_left.getExteriorName(),jewelry_left.getQualityColor(),jewelry_left.getQualityName());
        }
        if(TextUtils.isEmpty(dataList.get(position).get("jewelry_right").getJewelryName())){
            viewHolder.jewelryHomeViewRight.setVisibility(View.GONE);
        }{
            viewHolder.jewelryHomeViewRight.setName(dataList.get(position).get("jewelry_right").getShortName());
            viewHolder.jewelryHomeViewRight.setImage(dataList.get(position).get("jewelry_right").getBitmap());
            viewHolder.jewelryHomeViewRight.setDesc(jewelry_right.getExteriorColor(),jewelry_right.getExteriorName(),jewelry_right.getQualityColor(),jewelry_right.getQualityName());
        }

        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {

        JewelryHomeView jewelryHomeViewLeft;//图片
        JewelryHomeView jewelryHomeViewRight;//内容

        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            jewelryHomeViewLeft = (JewelryHomeView) view.findViewById(R.id.jewelry_table_item_left);
            jewelryHomeViewRight = (JewelryHomeView) view.findViewById(R.id.jewelry_table_item_right);
        }
    }
}