package com.pumkit.chin.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.OneImage;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.vo.ChatData;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static com.pumkit.chin.chinchinso.R.id.img_chatting;

public class ChatAdapter extends BaseAdapter {
//    private final Context context;

    private ArrayList<ChatData> listViewItemList = new ArrayList<ChatData>();
    public ArrayList<ChatData> newList = new ArrayList<ChatData>();

    public ChatAdapter() {
//        this.context = context;
    }

    @Override
    public int getCount() {
        return newList.size();
    }

    public class ViewHolder {
        public RelativeLayout layout_chatBox;
        public ImageView img_user;
        public TextView txt_chatUserName_item;
        public TextView txt_chatMessage_item;
        public TextView chatTime_right;
        public TextView chatTime_left;
        public ImageView img_chatting;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_chat, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.layout_chatBox = (RelativeLayout) convertView.findViewById(R.id.layout_chatBox);
            viewHolder.img_user = (ImageView) convertView.findViewById(R.id.img_user);
            viewHolder.txt_chatUserName_item = (TextView) convertView.findViewById(R.id.txt_chatUserName_item);
            viewHolder.txt_chatMessage_item = (TextView) convertView.findViewById(R.id.txt_chatMessage_item);
            viewHolder.chatTime_right = (TextView) convertView.findViewById(R.id.chatTime_right);
            viewHolder.chatTime_left = (TextView) convertView.findViewById(R.id.chatTime_left);
            viewHolder.img_chatting = (ImageView) convertView.findViewById(R.id.img_chatting);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ChatData chatData = newList.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(chatData.getTimestampLong());

        Picasso.with(context).load(chatData.getPic1()).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).tag(context).transform(new CircleTransform()).into(viewHolder.img_user);
        viewHolder.txt_chatUserName_item.setText(chatData.getToUserName());

        viewHolder.chatTime_right.setText(formatter.format(calendar.getTime()));
        viewHolder.chatTime_left.setText(formatter.format(calendar.getTime()));

        if (chatData.getText()==null) {
            viewHolder.txt_chatMessage_item.setVisibility(View.GONE);
            viewHolder.img_chatting.setVisibility(View.VISIBLE);

            Log.e("chat", "chatData.getImage 가로 = " + chatData.getImageWidth() + ", 세로 = " + chatData.getImageHeight() + ", Url() = " + chatData.getImageUrl());
            Picasso.with(context)
                    .load(chatData.getImageUrl())
                    .placeholder(R.drawable.icon_noprofile_s)
//                    .error(R.drawable.back_keepcalm)
//                    .resize(chatData.getImageWidth()*3/2, chatData.getImageHeight()*3/2)
                    .resize(898, 898/chatData.getImageWidth()*chatData.getImageHeight())
                    .onlyScaleDown()
                    .centerInside()
                    .tag(context)
                    .into(viewHolder.img_chatting);
            viewHolder.img_chatting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("chat_abc", "position = "+ pos + ", img_url = " + chatData.getImageUrl());
                    Intent intent = new Intent(context, OneImage.class);
                    intent.putExtra("img_url", chatData.getImageUrl());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });

//            RelativeLayout.LayoutParams plControl = (RelativeLayout.LayoutParams) viewHolder.chatTime_right.getLayoutParams();
//            RelativeLayout.LayoutParams plControl2 = (RelativeLayout.LayoutParams) viewHolder.chatTime_left.getLayoutParams();
//
//            plControl.leftMargin = chatData.getImageWidth() + 10;
//            plControl.rightMargin = chatData.getImageWidth() + 10;
//
//            viewHolder.layout_chatBox.setLayoutParams(plControl);
//            viewHolder.layout_chatBox.setLayoutParams(plControl2);
        } else {
            viewHolder.txt_chatMessage_item.setVisibility(View.VISIBLE);
            viewHolder.img_chatting.setVisibility(View.GONE);

            viewHolder.txt_chatMessage_item.setText(chatData.getText());
        }

        if (chatData.getFromId().equals(MainActivity.myFireId)) {
            viewHolder.layout_chatBox.setGravity(Gravity.RIGHT);
            viewHolder.img_user.setVisibility(View.GONE);
            viewHolder.txt_chatUserName_item.setVisibility(View.GONE);

            viewHolder.chatTime_right.setVisibility(View.GONE);
            viewHolder.chatTime_left.setVisibility(View.VISIBLE);

//            RelativeLayout.LayoutParams timeParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            viewHolder.chatTime_right.setLayoutParams(timeParams);
//            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.chatTime_right);

            Log.e("chat", "RIGHT , " + pos);
        } else {
            viewHolder.layout_chatBox.setGravity(Gravity.LEFT);
            viewHolder.img_user.setVisibility(View.VISIBLE);
            viewHolder.txt_chatUserName_item.setVisibility(View.VISIBLE);

            viewHolder.chatTime_right.setVisibility(View.VISIBLE);
            viewHolder.chatTime_left.setVisibility(View.GONE);
//                convertView = inflater.inflate(R.layout.item_chat_left, parent, false);
            Log.e("chat", "LEFT , " + pos);
        }

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

    public void addItem(String fromId, String toId, String ToUserName, String text, Long milliSeconds, String imageUrl, int imageWidth, int imageHeight, String pic1) {
        ChatData item = new ChatData();

        item.setFromId(fromId);
        item.setToId(toId);
        item.setToUserName(ToUserName);
        item.setText(text);
        item.setTimestamp(milliSeconds);
        item.setImageUrl(imageUrl);
        item.setImageWidth(imageWidth);
        item.setImageHeight(imageHeight);
        item.setPic1(pic1);

        listViewItemList.add(item);

        RemoveDuplicate();
    }

    int partition(int arr[], int left, int right) {
        int i = left, j = right;
        int tmp;
        int pivot = arr[(left + right) / 2];

        while (i <= j) {
            while (arr[i] < pivot)
                i++;
            while (arr[j] > pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        };

        return i;
    }

    public void RemoveDuplicate() {

        Set set = new TreeSet(new Comparator<ChatData>() {
            @Override
            public int compare(ChatData obj1, ChatData obj2) {
                // DESC 내림차순
//                return (obj1.timestamp > obj2.timestamp) ? -1: (obj1.timestamp > obj2.timestamp) ? 1:0 ;
                // ASC 오름차순
                return (obj1.timestamp < obj2.timestamp) ? -1 : (obj1.timestamp > obj2.timestamp) ? 1 : 0;
            }

        });
        set.addAll(listViewItemList);

        newList = new ArrayList<ChatData>(set);


    }

}