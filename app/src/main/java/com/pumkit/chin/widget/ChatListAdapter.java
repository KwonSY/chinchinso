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
import com.pumkit.chin.vo.ChatListData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class ChatListAdapter extends BaseAdapter {

    public ArrayList<ChatListData> listViewItemList = new ArrayList<ChatListData>() ;
    public ArrayList<ChatListData> newList = new ArrayList<ChatListData>();

//    private OkHttpClient httpClient;

    public ChatListAdapter() {
//        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public int getCount() {
        return newList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_listchat, parent, false);
        }

        ImageView img_chatlist_item = (ImageView) convertView.findViewById(R.id.img_chatlist_item);
        TextView txt_chatlistNm_item = (TextView) convertView.findViewById(R.id.txt_chatlistNm_item);
        TextView txt_chatlistInfo_item = (TextView) convertView.findViewById(R.id.txt_chatlistInfo_item);
        TextView time_lastMessage = (TextView) convertView.findViewById(R.id.time_lastMessage);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ChatListData chatListData = newList.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(chatListData.getTimestampLong());

        Log.e("abc", "xx chatListData.getYourId() = " + chatListData.getYourId() + ", getUserName = " + chatListData.getYourName() +", chatListData.getPic1() = " + chatListData.getPic1());
//        Log.e("abc", "xx pic1 = " + pic1);
        if (chatListData.getPic1()==null) {
//            Log.e("abc", "이미지가 널이다 = " + chatListData.getPic1());
        }else {
//            Log.e("abc", "이미지가 있다 = " + chatListData.getPic1());
            if (Statics.my_gender.equals("f")) {
                Picasso.with(context).load(chatListData.getPic1()).placeholder(R.drawable.icon_noprofile_m).tag(context).transform(new CircleTransform()).into(img_chatlist_item);
//            Picasso.with(context).load(pic1).placeholder(R.drawable.icon_noprofile_m).tag(context).transform(new CircleTransform()).into(img_chatlist_item);
            } else if (Statics.my_gender.equals("m")) {
                Picasso.with(context).load(chatListData.getPic1()).placeholder(R.drawable.icon_noprofile_f).tag(context).transform(new CircleTransform()).into(img_chatlist_item);
//            Picasso.with(context).load(pic1).placeholder(R.drawable.icon_noprofile_f).tag(context).transform(new CircleTransform()).into(img_chatlist_item);
            }
        }
        txt_chatlistNm_item.setText(chatListData.getYourName());
        txt_chatlistInfo_item.setText(chatListData.getText());
        time_lastMessage.setText(formatter.format(calendar.getTime()));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return newList.get(position);
    }


    public void addItem(String my_id, String matchId, String yourId, String partnerId, String text, Long timestamp) {

        ChatListData item = new ChatListData();

        item.setMy_id(my_id);
        item.setMatch_id(matchId);
        item.setYourId(yourId);
        item.setYourName(partnerId);
        if(text==null) {
            text = "사진";
        }
        item.setText(text);
        item.setTimestamp(timestamp);

        listViewItemList.add(item);

//        newList.add(item);
//        newList = QuickSort.getData(newList);

        RemoveDuplicate();
    }

    public void RemoveDuplicate() {
        // 시간 내림차순
        Collections.sort(listViewItemList, new Comparator<ChatListData>(){
            public int compare(ChatListData obj1, ChatListData obj2)
            {
                // DESC 내림차순
                return (obj1.timestamp > obj2.timestamp) ? -1: (obj1.timestamp > obj2.timestamp) ? 1:0 ;
                // ASC 오름차순
//                return (obj1.timestamp < obj2.timestamp) ? -1: (obj1.timestamp > obj2.timestamp) ? 1:0 ;
            }
        });

        // 중복제거
        Set set = new TreeSet(new Comparator<ChatListData>() {
            @Override
            public int compare(ChatListData o1, ChatListData o2) {
                if(o1.getYourId().equalsIgnoreCase(o2.getYourId())){
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(listViewItemList);

        newList = new ArrayList<ChatListData>(set);
    }

    public void changeUserName(String partnerId, String user_name, String pic1) {

        for (int i=0; i < newList.size(); i++) {

            if (newList.get(i).getYourId().contains(partnerId)) {
//                listViewItemList.get(i).getYourName().replace(yourName, user_name);
                newList.get(i).setYourName(user_name);
                newList.get(i).setPic1(Statics.main_url + pic1);
            } else {

            }
//            Log.e("abc", "In changeUserName, user_name = " + user_name + "+ pic1 = " + pic1);
        }

    }

}