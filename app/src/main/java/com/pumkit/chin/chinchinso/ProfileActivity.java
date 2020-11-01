package com.pumkit.chin.chinchinso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pumkit.chin.vo.ProfileData;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileActivity extends AppCompatActivity {

    private OkHttpClient httpClient;


    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private Button btnSkip, btnNext;
    private PrefManager prefManager;


    private ArrayList<String> img_arr = new ArrayList<>();

    private ProfileData profileData;

    private String my_id = MainActivity.my_id;
    private String match_id, match_name, match_pic, match_fire, match_recomm;
    private String user_id, user_name, user_fire;
    private String type, poke_status;

    private ImageView img_close_x;
    private Button btn_pic_management;
    private TextView txt_match_name, profile_name, txt_hash1, txt_hash2, txt_hash3;
    private Button btn_go_chat;
    private RelativeLayout layout_match;
    private ImageView img_match;
    private TextView txt_top_match_name, txt_top_match_recomm;
//    private Button btn_change_hash;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        match_id = intent.getStringExtra("match_id");
        Log.e("abc", "u = " + user_id + ", m = " + match_id);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_profile_f);



//        layout_match = (RelativeLayout) findViewById(R.id.layout_match);
//
//        viewPager = (ViewPager) findViewById(R.id.profile_pager);
//        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
//
//        layouts = new int[]{
//                R.layout.slide_profile1};

        //xxxxxxx
//        layouts2.add(R.layout.slide_profile1);

        // adding bottom dots
//        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();

        img_close_x = (ImageView) findViewById(R.id.img_close_x);
        img_close_x.setOnClickListener(onClickListener);

        btn_pic_management = (Button) findViewById(R.id.btn_pic_management);
        btn_pic_management.setOnClickListener(onClickListener);

        btn_go_chat = (Button) findViewById(R.id.btn_go_chat);
        btn_go_chat.setOnClickListener(onClickListener);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("abc" , "xxxxxxxxxxxxxxxxxx 리줌");
        new ProfileTask().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("abc" , "xxxxxxxxxxxxxxxxxx 리스타트");
//        new ProfileTask().execute();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
                case R.id.btn_pic_management:
                    Intent intent_pics = new Intent(ProfileActivity.this, MyImageList.class);
                    intent_pics.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent_pics);

                    break;
                case R.id.btn_go_chat:
                    Log.e("abc", "type = " + type);
                    if (type.equals("my")) {
                        //해쉬관리
                        Intent intent_hash = new Intent(ProfileActivity.this, SettingHashActivity.class);
                        intent_hash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_hash);
                    } else if (type.equals("chin")) {
                        //
                        new EnterChatTask().execute();
                    } else if (type.equals("public")) {
                        if (poke_status.equals("poke")) {
                            new PokeTask().execute();
                        } else if (poke_status.equals("already")) {
                            Toast.makeText(ProfileActivity.this, "이미 콕찌르기를 하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else if (poke_status.equals("accept")) {
                            new EnterChatTask().execute();
                        } else if (poke_status.equals("chat")) {
                            Intent intent_chat = new Intent(ProfileActivity.this, ChatActivity.class);
                            intent_chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent_chat.putExtra("myFireId", MainActivity.myFireId);
                            intent_chat.putExtra("matchFireId", match_fire);
                            intent_chat.putExtra("yourFireId", user_fire);
                            intent_chat.putExtra("yourUserName", user_name);
                            intent_chat.putExtra("pic1", img_arr.get(0));
                            startActivity(intent_chat);
                            finish();
                        }
                    }

                    break;
                case R.id.img_close_x:
                    onBackPressed();

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton b = (ImageButton) v;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                b.setBackgroundResource(R.drawable.border_transparent);
            } else if (action == MotionEvent.ACTION_UP) {
                b.setBackgroundResource(0);

                onBackPressed();
            } else if (action == MotionEvent.ACTION_CANCEL) {
                b.setBackgroundResource(0);
            }
            return false;
        }
    };

    public void addBottomDots(int currentPage) {
//        dots = new TextView[layouts.length];
//        dots = new TextView[img_arr.size()];
        dots = new TextView[layouts2.size()];
        Log.e("abc", "dots.length = " + dots.length);

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

            // changing the next button text 'NEXT' / 'GOT IT'
//            if (position == layouts.length - 1) {
            if (position == layouts2.size() - 1) {
                // last page. make button text to GOT IT

            } else {
                // still pages are left

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//            View view = layoutInflater.inflate(layouts[position], container, false);
            View view = layoutInflater.inflate(layouts2.get(position), container, false);

            ImageView img_profile = (ImageView) view.findViewById(R.id.img_profile);

            Log.e("abc", "img_arr.size() = " + img_arr.size());
            if (position < img_arr.size()) {
                Picasso.with(ProfileActivity.this).load(img_arr.get(position)).placeholder(R.drawable.icon_noprofile).into(img_profile);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
//            return layouts.length;
//            return img_arr.size();
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





    List<String> hash_name_arr = new ArrayList<>();


//    private ArrayList<Integer> layouts2 = new ArrayList<>();
//    private ArrayList<String> img_arr = new ArrayList<>();

    // 프로필 로드
    private class ProfileTask extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {

            hash_name_arr.clear();

            layout_match = (RelativeLayout) findViewById(R.id.layout_match);

            viewPager = (ViewPager) findViewById(R.id.profile_pager);
            dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

            layouts = new int[]{
                    R.layout.slide_profile1};

//            img_close_x = (ImageView) findViewById(R.id.img_close_x);
//            img_close_x.setOnClickListener(onClickListener);

//            btn_pic_management = (Button) findViewById(R.id.btn_pic_management);
//            btn_pic_management.setOnClickListener(onClickListener);

//            btn_go_chat = (Button) findViewById(R.id.btn_go_chat);
//            btn_go_chat.setOnClickListener(onClickListener);



            img_arr.clear();
            layouts2.clear();

            img_match = (ImageView) findViewById(R.id.img_match);
            txt_top_match_name = (TextView) findViewById(R.id.txt_top_match_name);
            txt_top_match_recomm = (TextView) findViewById(R.id.txt_top_match_recomm);

            txt_match_name = (TextView) findViewById(R.id.txt_match_name);
            profile_name = (TextView) findViewById(R.id.profile_name);

            txt_hash1 = (TextView) findViewById(R.id.txt_hash1);
            txt_hash2 = (TextView) findViewById(R.id.txt_hash2);
            txt_hash3 = (TextView) findViewById(R.id.txt_hash3);
            txt_hash1.setText("");
            txt_hash2.setText("");
            txt_hash3.setText("");
        }

        @Override
        protected Void doInBackground(String... params) {

            if (match_id.equals(null)) {
                match_id = "";
            }

            FormBody body = new FormBody.Builder()
                    .add("opt", "profile")
                    .add("my_id", my_id)
                    .add("match_id", match_id)
                    .add("user_id", user_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "ProfileTask obj = " + obj);

                    type = obj.getString("type");
                    if (type.equals("public"))
                    poke_status = obj.getString("poke_status");

                    if (obj.has("match")) {
                        Log.e("abc", "zxfxxxxx = " + obj.getJSONObject("match").isNull("sid"));
                        JSONObject obj_match = obj.getJSONObject("match");
                        String match_id = obj_match.getString("sid");
                        match_name = obj_match.getString("user_name");
                        match_pic = obj_match.getString("pic1");
                        match_fire = obj_match.getString("id_firebase");
                        if (type.equals("public"))
                        match_recomm = obj_match.getString("recomm_note");
                    }

                    JSONObject obj_blind = obj.getJSONObject("profile");
                    user_id = obj_blind.getString("sid");
                    user_name = obj_blind.getString("user_name");
                    user_fire = obj_blind.getString("id_firebase");

                    JSONArray pic_arr = obj_blind.getJSONArray("pic1");
                    for (int i = 0; i < pic_arr.length(); i++) {
                        JSONObject obj2 = pic_arr.getJSONObject(i);

                        String pic_id = obj2.getString("sid");
                        String img_url = obj2.getString("url");

                        if (img_url != null && !img_url.isEmpty()) {
                            img_url = Statics.main_url + img_url;
                        }
                        img_arr.add(img_url);
                        layouts2.add(R.layout.slide_profile1);
                    }

                    JSONArray article = obj_blind.getJSONArray("hash");
                    for (int i = 0; i < article.length(); i++) {
                        JSONObject obj2 = article.getJSONObject(i);

                        String sid = obj2.getString("sid");
                        String hash_name = obj2.getString("hash_name");

                        hash_name_arr.add("#" + hash_name);
//                        if (img_url != null && !img_url.isEmpty()) {
//                            img_url = Statics.main_url + img_url;
//                        }
                    }

                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            img_close_x.setVisibility(View.GONE);

            if (type.equals("my")) {
                layout_match.setVisibility(View.GONE);

                btn_go_chat.setText("해쉬관리");
            } else if (type.equals("chin")) {
                layout_match.setVisibility(View.GONE);

                btn_go_chat.setVisibility(View.VISIBLE);
                btn_go_chat.setText("채팅하기");
                btn_pic_management.setVisibility(View.GONE);
            } else if (type.equals("public")) {
                layout_match.setVisibility(View.VISIBLE);
                Picasso.with(ProfileActivity.this).load(Statics.main_url + match_pic).placeholder(R.drawable.icon_noprofile_m).error(R.drawable.icon_noprofile_m).fit().tag(this).transform(new CircleTransform()).into(img_match);
                txt_top_match_name.setText(match_name + "주선자의 소개글");
                txt_top_match_recomm.setText(match_recomm);

                btn_pic_management.setVisibility(View.GONE);
                if (poke_status.equals("poke")) {
                    btn_go_chat.setText("콕찌르기");
                } else if (poke_status.equals("already")) {
                    btn_go_chat.setText("이미찌름");
                } else if (poke_status.equals("accept")) {
                    btn_go_chat.setText("소개수락");
                } else if (poke_status.equals("chat")) {
                    btn_go_chat.setText("대화하기");
                }

            }

            if (user_id.equals(my_id)) {
                // 내 프로필
                txt_match_name.setText("나의 프로필");
            } else {
                // 상대방 프로필
                txt_match_name.setText(match_name + "님의 친구");
            }

            profile_name.setText(user_name);

            Log.e("abc", "hash_name_arr = " + hash_name_arr.size());
            if (hash_name_arr.size() < 1) {

            } else if (hash_name_arr.size() < 2) {
                txt_hash1.setText(hash_name_arr.get(0));
                txt_hash2.setVisibility(View.GONE);
                txt_hash3.setVisibility(View.GONE);
            } else if (hash_name_arr.size() < 3) {
                txt_hash2.setVisibility(View.VISIBLE);

                txt_hash1.setText(hash_name_arr.get(0));
                txt_hash2.setText(hash_name_arr.get(1));
                txt_hash3.setVisibility(View.GONE);
            } else if (hash_name_arr.size() < 4) {
                txt_hash2.setVisibility(View.VISIBLE);
                txt_hash3.setVisibility(View.VISIBLE);

                txt_hash1.setText(hash_name_arr.get(0));
                txt_hash2.setText(hash_name_arr.get(1));
                txt_hash3.setText(hash_name_arr.get(2));
            } else {

            }

            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            addBottomDots(0);
        }
    }

    private class PokeTask extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "poke")
                    .add("my_id", my_id)
                    .add("match_id", match_id)
                    .add("poke_id", user_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "PokeTask obj = " + obj);

                    result = obj.getString("result");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0"))
                btn_go_chat.setText("찌름완료");
        }
    }

    private class EnterChatTask extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "enter_chat")
                    .add("my_id", my_id)
                    .add("blind_id", user_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "EnterChatTask obj = " + obj);

                    result = obj.getString("result");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {
                Intent intent_chat = new Intent(ProfileActivity.this, ChatActivity.class);
                intent_chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_chat.putExtra("myFireId", MainActivity.myFireId);
                intent_chat.putExtra("matchFireId", match_fire);
                intent_chat.putExtra("yourFireId", user_fire);
                intent_chat.putExtra("yourUserName", user_name);
                intent_chat.putExtra("pic1", img_arr.get(0));
                startActivity(intent_chat);
                finish();
            } else {

            }
        }
    }
}