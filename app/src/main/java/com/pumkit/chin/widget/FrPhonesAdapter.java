package com.pumkit.chin.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.vo.RejectData;

import java.util.ArrayList;

public class FrPhonesAdapter extends BaseAdapter {

    public ArrayList<RejectData> listViewItemList = new ArrayList<RejectData>();
    private ArrayList<String> phones_arr = new ArrayList<>();
    private ArrayList<String> names_arr = new ArrayList<>();

    public FrPhonesAdapter(ArrayList<String> phones_arr, ArrayList<String> names_arr) {
        listViewItemList.clear();

        this.phones_arr = phones_arr;
        this.names_arr = names_arr;
        for (int i=0; i<phones_arr.size(); i++) {
            RejectData r2 = new RejectData(phones_arr.get(i), names_arr.get(i));
            listViewItemList.add(r2);
        }
    }

    @Override
    public int getCount() {
//        return listViewItemList.size();
        return phones_arr.size();
    }

    public class ViewHolder {
        TextView fr_name, fr_phone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_dial, parent, false);
        }

        holder = new ViewHolder();
        holder.fr_name = (TextView) convertView.findViewById(R.id.fr_name);
        holder.fr_phone = (TextView) convertView.findViewById(R.id.fr_phone);

//        MemberData memberData = phones_arr.get(position);
//        memberData.setPhone(phones_arr.get(pos));

//        Picasso.with(context).load(Statics.main_url + memberData.getPic1()).fit().centerCrop().placeholder(R.drawable.icon_noprofile).transform(new CircleTransform()).into(holder.img_poker);
        holder.fr_name.setText(names_arr.get(pos));
        holder.fr_phone.setText(phones_arr.get(pos));

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

    public void addItem(String user_name, String phone) {
        RejectData item = new RejectData(phone, user_name);

//        item.setName(user_name);
//        item.setDial(phone);

        listViewItemList.add(item);
    }

}