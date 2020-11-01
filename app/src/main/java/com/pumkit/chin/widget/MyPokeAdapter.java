package com.pumkit.chin.widget;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.ProfileActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.MemberData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPokeAdapter extends BaseAdapter {

    public ArrayList<MemberData> listViewItemList = new ArrayList<MemberData>() ;

    public MyPokeAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    public class ViewHolder {
        ImageView img_poker;
        TextView poker_name, time_poke;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_mypoke, parent, false);
        }

        holder = new ViewHolder();
        holder.img_poker = (ImageView) convertView.findViewById(R.id.poker_img);
        holder.poker_name = (TextView) convertView.findViewById(R.id.poker_name);
        holder.time_poke = (TextView) convertView.findViewById(R.id.time_poke);

        final MemberData memberData = listViewItemList.get(position);

        Picasso.with(context).load(Statics.main_url + memberData.getPic1()).fit().centerCrop().placeholder(R.drawable.icon_noprofile).transform(new CircleTransform()).into(holder.img_poker);
        holder.poker_name.setText(memberData.getUser_name());
        holder.time_poke.setText(memberData.getJoin_time());

        holder.img_poker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user_id", memberData.getSid());
                intent.putExtra("match_id", memberData.getMatch_id());
                context.startActivity(intent);
            }
        });

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

    public void addItem(String match_id, String sid, String user_name, String img_url, String time) {
        MemberData item = new MemberData();

        item.setMatch_id(match_id);
        item.setSid(sid);
        item.setUser_name(user_name);
        item.setPic1(img_url);
        item.setJoin_time(time);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }

}