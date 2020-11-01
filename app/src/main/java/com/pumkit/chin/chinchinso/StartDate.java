package com.pumkit.chin.chinchinso;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class StartDate extends AppCompatActivity {

    private OkHttpClient httpClient;

    private String match_id, match_name, match_fire, match_pic;
    private String blind_id, blind_name, blind_fire;

    int start_point = 440;
    int end_point = 990;
    int checked = 0;

    private RelativeLayout layout_above;

    private ImageView img_close_x;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    ArrayList<String> hash_name_arr = new ArrayList<>();
    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_date);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        final Intent intent = getIntent();
        match_id = intent.getStringExtra("match_id");
        match_name = intent.getStringExtra("match_name");
        match_fire = intent.getStringExtra("match_fire");
        match_pic = intent.getStringExtra("match_pic");
        blind_id = intent.getStringExtra("blind_id");
        blind_name = intent.getStringExtra("blind_name");
        blind_fire = intent.getStringExtra("blind_fire");
        hash_name_arr = intent.getStringArrayListExtra("blind_hash_name_arr");
        img_arr = intent.getStringArrayListExtra("img_arr");
        layouts2 = intent.getIntegerArrayListExtra("layouts2");

//////////////// layout above
        layout_above = (RelativeLayout) findViewById(R.id.layout_above);
//        RelativeLayout layout_below = (RelativeLayout) findViewById(R.id.layout_below);

        TextView txt_match = (TextView) findViewById(R.id.txt_match);
        txt_match.setText(match_name);

        RelativeLayout layout_scratch = (RelativeLayout) findViewById(R.id.layout_scratch);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
//        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        start_point = deviceHeight/5;
        Log.e("abc", "total_height111 = " + deviceHeight);
        Log.e("abc", "start_point = " + start_point);
        layout_scratch.setY(start_point);

//        ImageView back_scratch2 = (ImageView) findViewById(R.id.back_scratch2);
//        back_scratch2.setY(start_point - 185);

        ImageView btn_start = (ImageView) findViewById(R.id.btn_start);
        Picasso.with(StartDate.this).load(Statics.main_url + match_pic).fit().placeholder(R.drawable.icon_noprofile_s).transform(new CircleTransform()).into(btn_start);
//        btn_start.setY(start_point);
        Log.e("abc", "btn_start.getY() = " + btn_start.getY());
        btn_start.setOnTouchListener(new DragExperimentTouchListener(btn_start.getX(), btn_start.getY()));





//////////////layout_below
        RelativeLayout layout_match = (RelativeLayout) findViewById(R.id.layout_match);
        layout_match.setVisibility(View.GONE);

        img_close_x = (ImageView) findViewById(R.id.img_close_x);
        img_close_x.setOnClickListener(onClickListener);

        Button btn_pic_management = (Button) findViewById(R.id.btn_pic_management);
        RelativeLayout topbar = (RelativeLayout) findViewById(R.id.topbar);
        btn_pic_management.setVisibility(View.GONE);
        topbar.setVisibility(View.GONE);

        TextView txt_match_name = (TextView) findViewById(R.id.txt_match_name);
        TextView txt_blind_name = (TextView) findViewById(R.id.profile_name);
        TextView txt_hash1 = (TextView) findViewById(R.id.txt_hash1);
        TextView txt_hash2 = (TextView) findViewById(R.id.txt_hash2);
        TextView txt_hash3 = (TextView) findViewById(R.id.txt_hash3);
        txt_match_name.setText(match_name + "님의 친구");
        txt_blind_name.setText(blind_name);
        if (hash_name_arr.size() == 0) {

        } else if (hash_name_arr.size() == 1) {
            txt_hash3.setText(hash_name_arr.get(0));
        } else if (hash_name_arr.size() == 2) {
            txt_hash3.setText(hash_name_arr.get(0));
            txt_hash2.setText(hash_name_arr.get(1));
        } else if (hash_name_arr.size() >= 3) {
            txt_hash3.setText(hash_name_arr.get(0));
            txt_hash2.setText(hash_name_arr.get(1));
            txt_hash1.setText(hash_name_arr.get(2));
        }

        viewPager = (ViewPager) findViewById(R.id.profile_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        layouts = new int[]{
                R.layout.slide_profile1};
        myViewPagerAdapter = new MyViewPagerAdapter();


        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        addBottomDots(0);

        Button btn_go_chat = (Button) findViewById(R.id.btn_go_chat);
        btn_go_chat.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_close_x:
                    if (checked > 0) {
                        Intent intent = new Intent(StartDate.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    break;
                case R.id.btn_go_chat:
                    if (checked > 0) {
                        new EnterChatTask().execute();

                        Intent intent2 = new Intent(StartDate.this, ChatActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.putExtra("myFireId", MainActivity.myFireId);
                        intent2.putExtra("matchFireId", match_fire);
                        intent2.putExtra("yourFireId", blind_fire);
                        intent2.putExtra("yourUserName", blind_name);
                        intent2.putExtra("pic1", img_arr.get(0));
                        startActivity(intent2);
                        finish();
                    }

                    break;
            }
        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts2.size()];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
//            dots[i].setTextColor(colorsInactive[currentPage]);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
//            dots[currentPage].setTextColor(colorsActive[currentPage]);
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.dot_active));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts2.size() - 1) {

            } else {

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
//            img_fr_pic = (ImageView) findViewById(R.id.img_fr_pic);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts2.get(position), container, false);

            ImageView img_profile = (ImageView) view.findViewById(R.id.img_profile);

            if (position < img_arr.size()) {
                Picasso.with(StartDate.this).load(img_arr.get(position)).placeholder(R.drawable.icon_noprofile).into(img_profile);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts2.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        new PickBlindTask().execute();
    }

    public class DragExperimentTouchListener implements View.OnTouchListener {

        public DragExperimentTouchListener(float initalX, float initialY) {
            lastX = initalX;
            lastY = initialY;
            Log.e("abc", "lastX = " + lastX);
        }

        boolean isDragging = false;
        float lastX;
        float lastY;
        float deltaX;
        float deltaY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
//            Log.e("abc", "action = " + action);
//            Log.e("abc", "layout_above getMeasuredHeight = " + layout_above.getMeasuredHeight());
//            int total_height = layout_above.getMeasuredHeight();
//            start_point = total_height/3*2;
            start_point = 0;

            if (action == MotionEvent.ACTION_DOWN && !isDragging) {
                isDragging = true;
//                deltaX = event.getX();
                deltaY = event.getY();
                return true;
            } else if (isDragging) {
                if (action == MotionEvent.ACTION_MOVE) {//2
                    view.setX(view.getX());

                    if (checked > 0) {
                        float a = 0;
                        layout_above.setAlpha(a);
                    } else {
                        Log.e("abc", "start_point = " + start_point);
                        Log.e("abc", "view.getY() = " + view.getY());
                        Log.e("abc", "event.getY() = " + event.getY());
                        Log.e("abc", "deltaY = " + deltaY);

                        if ((view.getY() + event.getY() - deltaY) < start_point) {
                            view.setY(start_point);
                        } else if ((view.getY() + event.getY() - deltaY) >= (end_point+start_point)*2/3) {
                            view.setY(end_point);

                            checked++;
                        } else {
                            view.setY(view.getY() + event.getY() - deltaY);

                            float a = (1 - ((view.getY() + event.getY() - deltaY) - start_point) / (end_point - start_point));
                            layout_above.setAlpha(a);
                        }
                    }

                    return true;

                } else if (action == MotionEvent.ACTION_UP) {//1
                    isDragging = false;
                    lastX = event.getX();
//                    lastY = event.getY();
                    if (view.getY() < end_point) {
                        lastY = start_point;
                        view.setY(lastY);

                        float b = 1;
                        layout_above.setAlpha(b);
                    } else if (view.getY() >= end_point) {
//                        lastY = end_point;
                        view.setY(lastY);
                    }

                    return true;
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    view.setX(lastX);
                    view.setY(lastY);

                    isDragging = false;
                    return true;
                }
            }

            return false;
        }
    }

    private class EnterChatTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "enter_chat")
                    .add("my_id", MainActivity.my_id)
                    .add("blind_id", blind_id)
                    .build();
            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "enter_chat root = " + obj);
                }
            } catch (Exception e) {
                Log.e("abc", "Record 실패 = " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}