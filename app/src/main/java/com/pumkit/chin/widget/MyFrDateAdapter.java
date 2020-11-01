package com.pumkit.chin.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.PokeData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyFrDateAdapter extends BaseAdapter {

    public ArrayList<PokeData> listViewItemList = new ArrayList<PokeData>() ;

    public MyFrDateAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    public class ViewHolder {
//        ImageView img_my;
//        TextView user_name, status;
        ImageView img_poker_pic, img_fr_pic;
        TextView txt_poker_name, txt_fr_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_mywantfr, parent, false);
        }

        holder = new ViewHolder();
        holder.img_poker_pic = (ImageView) convertView.findViewById(R.id.img_poker_pic);
        holder.txt_poker_name = (TextView) convertView.findViewById(R.id.txt_poker_name);
        holder.img_fr_pic = (ImageView) convertView.findViewById(R.id.img_fr_pic);
        holder.txt_fr_name = (TextView) convertView.findViewById(R.id.txt_fr_name);
//        holder.status = (TextView) convertView.findViewById(status);

        PokeData pokeData = listViewItemList.get(position);

        Log.e("abc", "xxxxxxx pokeData.getPoke_pic() = " + pokeData.getPoke_pic());
        Picasso.with(context).load(Statics.main_url + pokeData.getPoke_pic()).fit().centerCrop().placeholder(R.drawable.icon_noprofile_m).transform(new CircleTransform()).into(holder.img_poker_pic);
        holder.txt_poker_name.setText(pokeData.getPoke_name());
        Log.e("abc", "xxxxxxx pokeData.getFr_pic() = " + pokeData.getFr_pic());
        Picasso.with(context).load(Statics.main_url + pokeData.getFr_pic()).fit().centerCrop().placeholder(R.drawable.icon_noprofile_m).transform(new CircleTransform()).into(holder.img_fr_pic);
        holder.txt_fr_name.setText(pokeData.getFr_name());

//        holder.status.setText(pokeData.getStatus());

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

    public void addItem(String poker_id, String poker_name, String poker_pic, String fr_id, String fr_name, String fr_pic) {
//        MemberData item = new MemberData();
//
//        item.setSid(sid);
//        item.setUser_name(user_name);
//        item.setPic1(pic1);
//        item.setId_firebase(fire_base);
//
//        listViewItemList.add(item);

        PokeData item = new PokeData();

        item.setPoke_id(poker_id);
        item.setPoke_name(poker_name);
        item.setPoke_pic(poker_pic);
        item.setFr_id(fr_id);
        item.setFr_name(fr_name);
        item.setFr_pic(fr_pic);

        listViewItemList.add(item);
    }

}