package com.pumkit.chin.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.MemberData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OneManAdapter extends BaseAdapter {

    public ArrayList<MemberData> listViewItemList = new ArrayList<MemberData>() ;

    public OneManAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    public class ViewHolder {
        ImageView user_image;
        TextView user_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_oneman, parent, false);
        }

        holder = new ViewHolder();
        holder.user_image = (ImageView) convertView.findViewById(R.id.user_image);
        holder.user_name = (TextView) convertView.findViewById(R.id.user_name);

        MemberData memberData = listViewItemList.get(position);

        Picasso.with(context).load(Statics.main_url + memberData.getPic1()).fit().centerCrop().placeholder(R.drawable.icon_noprofile).transform(new CircleTransform()).into(holder.user_image);
        holder.user_name.setText(memberData.getUser_name());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String sid, String user_name, String img_url) {
        MemberData item = new MemberData();

        item.setSid(sid);
        item.setUser_name(user_name);
        item.setPic1(img_url);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }

}