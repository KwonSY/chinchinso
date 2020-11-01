package com.pumkit.chin.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.fragments.DateFragment;
import com.pumkit.chin.vo.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DateListSwipeAdapter extends PagerAdapter {

    public ArrayList<UserData> listViewItemList = new ArrayList<UserData>() ;
//    private int[] image_resources = {R.drawable.icon_noprofile_f, R.drawable.icon_noprofile, R.drawable.icon_noprofile_m};
    private Context context;
    private LayoutInflater layoutInflater;

    int currentPage;

    public DateListSwipeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
//        Log.e("abc", "listViewItemList.size() = " + listViewItemList.size());

//        return image_resources.length;
        return listViewItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout)object;
    }

    public class ViewHolder {
        ImageView img_swipe;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ViewHolder holder;
        View view = null;

        if (view == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_swipe, container, false);
        }

        holder = new ViewHolder();
        holder.img_swipe = (ImageView) view.findViewById(R.id.img_swipe);
        holder.img_swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DateFragment.cnt_dated == 0) {//|| i_cnt_blind > 0
                    if (DateFragment.i_cnt_blind > 0) {
                        DateFragment.mStartSelectMatchMaker(context);
                    } else {Toast.makeText(context,  "친친팅을 할 주선자 친구를 먼저 초대해주세요.", Toast.LENGTH_SHORT).show();

                    }
//                    Intent intent = new Intent(context, SelectMatchMaker.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("my_pic", DateFragment.my_pic);
//
//                    Pair<View, String> pair1 = Pair.create(view.findViewById(R.id.img_my_pic), "myImage");
//                    Pair<View, String> pair2 = Pair.create(view.findViewById(R.id.img_match), "ImageMatch");
//                    Pair<View, String> pair3 = Pair.create(view.findViewById(R.id.viewpager_datelist), "ImageBlind");
//
//                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, pair1, pair2, pair3);
//                    context.startActivity(intent, optionsCompat.toBundle());
                } else {
                    Toast.makeText(context,  "친친팅을 할 주선자 친구를 먼저 초대해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.img_swipe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ImageView image = (ImageView) view;

                int action = motionEvent.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.greyafafaf), PorterDuff.Mode.MULTIPLY);
                } else if (action == MotionEvent.ACTION_UP) {
                    image.clearColorFilter();
                } else if (action == MotionEvent.ACTION_CANCEL) {
//                    b.setBackgroundResource(0);
                }

                return false;
            }
        });

        Log.e("abc", "position = " + position);
        if (position != 0) {
            UserData userData = listViewItemList.get(position);
            Log.e("abc", "userData getImg_url = " + userData.getImg_url());

//            holder.img_swipe.setImageResource(image_resources[position]);
//        if (position == 1) {
//            Picasso.with(context).load(R.drawable.icon_noprofile_f).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(context).transform(new CircleTransform()).into(holder.img_swipe);
//        } else {
            Picasso.with(context).load(Statics.main_url + userData.getImg_url()).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(context).transform(new CircleTransform()).into(holder.img_swipe);
//        }
        } else {
//            Picasso.with(context).load(R.drawable.icon_noprofile_f).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(context).transform(new CircleTransform()).into(holder.img_swipe);
            Picasso.with(context).load(R.drawable.btn_circle_new_date).placeholder(R.drawable.btn_circle_new_date).error(R.drawable.btn_circle_new_date).transform(new CircleTransform()).into(holder.img_swipe);
        }

        container.addView(view);

        currentPage = position;

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void addItem(String img_url) {
        UserData item = new UserData();

        item.setImg_url(img_url);

        listViewItemList.add(item);
    }

    public int getCurrentItem() {
        return currentPage;
    }
}