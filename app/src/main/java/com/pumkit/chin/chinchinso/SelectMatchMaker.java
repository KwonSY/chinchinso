package com.pumkit.chin.chinchinso;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.pumkit.chin.vo.MatchData;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.PickerDateAdapter;
import com.pumkit.chin.widget.PickerMatchAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.MainActivity.my_id;

public class SelectMatchMaker extends AppCompatActivity {

    private OkHttpClient httpClient;

    private String match_id, match_name, match_fire, match_pic;
    private String blind_id, blind_name, blind_fire;

    ImageView img_match, img_blind;
    TextView txt_title;

    private PickerMatchAdapter mAdapter;
    private ListView mListView;
    private ArrayList<MatchData> mMatchList = new ArrayList<>();
    int current_nth;
    int int_btn_go = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_match_maker);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        final ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);


        ImageView img_my_pic = (ImageView) findViewById(R.id.img_my_pic);
        txt_title = (TextView) findViewById(R.id.txt_title);

        Intent intent = getIntent();
        String my_pic = intent.getStringExtra("my_pic");
        Picasso.with(this).load(Statics.main_url + my_pic).placeholder(R.drawable.icon_circle_none).error(R.drawable.stub_image).fit().centerCrop().tag(this).transform(new CircleTransform()).into(img_my_pic);

        new MatchMakerListTask().execute();

        img_match = (ImageView) findViewById(R.id.img_match);
        final Animation animation = AnimationUtils.loadAnimation(SelectMatchMaker.this, R.anim.rotate);
        img_match.startAnimation(animation);
        img_blind = (ImageView) findViewById(R.id.img_blind);

        mAdapter = new PickerMatchAdapter(mMatchList);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
//                    clickAvailable = true;

                    mListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.smoothScrollToPosition(mListView.getFirstVisiblePosition());
                        }
                    }, 200);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                clickAvailable = false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mAdapter.getCount() - 1) {
                    view.setSelected(false);
                } else {
                    view.setSelected(true);
                }
            }
        });
        mListView.setAdapter(mAdapter);

        final Button btn_select = (Button) findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("abc", "int_btn_go = " + int_btn_go);
                if (int_btn_go == 0) {
                    current_nth = mListView.getFirstVisiblePosition() + 1;

                    match_id = mMatchList.get(current_nth).getSid();

                    img_match.clearAnimation();
                    img_blind.startAnimation(animation);

                    Picasso.with(SelectMatchMaker.this).load(Statics.main_url + mMatchList.get(current_nth).getMatch_img()).placeholder(R.drawable.icon_circle_none).transform(new CircleTransform()).into(img_match);

                    new PickMatchTask().execute(mMatchList.get(current_nth).getSid());

                    btn_select.setText("소개팅 시작!");

                    btn_back.setVisibility(View.INVISIBLE);
                } else if (int_btn_go == 1) {
                    new PickBlindTask().execute(mMatchList.get(current_nth).getSid());

//                    Intent intent = new Intent(SelectMatchMaker.this, StartDate.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("match_id", match_id);
//                    intent.putExtra("blind_id", mMatchList.get(current_nth).getSid());
//                    startActivity(intent);
//                    finish();
                }
            }
        });


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
//                case R.id.btn_select:
//
//                    break;
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

    private void setDividerColor(NumberPicker picker, int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    //주선자 리스트
    private class MatchMakerListTask extends AsyncTask<Void, Void, String[]> {

        String[] matchList_info;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(Void... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_match_list")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    JSONArray matchmakers = obj.getJSONArray("matchmakers");

                    matchList_info = new String[matchmakers.length()];

                    MatchData matchData_0 = new MatchData();
                    matchData_0.setSid("");
                    matchData_0.setUser_name("");
                    matchData_0.setMatch_img("");
                    matchData_0.setCnt_blind(0);
                    mMatchList.add(matchData_0);

                    for (int i = 0; i < matchmakers.length(); i++) {
                        MatchData matchData = new MatchData();

                        JSONObject obj2 = matchmakers.getJSONObject(i);

                        String match_id = obj2.getString("sid");
                        String match_name = obj2.getString("user_name");
                        String match_img = obj2.getString("pic1");
                        String str_cnt_blind = obj2.getString("cnt_blind");
                        int cnt_blind = Integer.parseInt(str_cnt_blind);

                        String text_row = match_name + "의 친구 " + str_cnt_blind + "명  중  소개팅";
                        matchList_info[i] = text_row;

                        matchData.setSid(match_id);
                        matchData.setUser_name(match_name);
                        matchData.setMatch_img(match_img);
                        matchData.setCnt_blind(cnt_blind);

                        mMatchList.add(matchData);
                    }

                    MatchData matchData_last = new MatchData();
                    matchData_last.setSid("");
                    matchData_last.setUser_name("");
                    matchData_last.setMatch_img("");
                    matchData_last.setCnt_blind(0);
                    mMatchList.add(matchData_last);
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return matchList_info;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mAdapter.notifyDataSetChanged();
        }
    }


    //소개팅상대 - 주선자 선택
//    private ArrayList<MatchData> mBlindList = new ArrayList<>();

    private class PickMatchTask extends AsyncTask<String, Void, String[]> {

        String[] matchList_info;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PickerDateAdapter.listViewItemList.clear();
//            mAdapter = new PickerMatchAdapter(mBlindList);
            mMatchList.clear();
        }

        @Override
        protected String[] doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "pick_match")
                    .add("my_id", my_id)
                    .add("match_id", params[0])//match_id
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "PickMatchTask obj " + obj);

                    JSONArray matchmakers = obj.getJSONArray("blind_list");

//                    matchList_info = new String[matchmakers.length()];

                    MatchData matchData_0 = new MatchData();
                    matchData_0.setSid("");
                    matchData_0.setUser_name("");
                    matchData_0.setMatch_img("");
//                    matchData_0.setCnt_blind(0);
                    mMatchList.add(matchData_0);

                    for (int i = 0; i < matchmakers.length(); i++) {
                        MatchData matchData = new MatchData();

                        JSONObject obj2 = matchmakers.getJSONObject(i);

                        String blind_id = obj2.getString("sid");
                        String blind_name = obj2.getString("user_name");
                        String blind_img = obj2.getString("pic1");

                        matchData.setSid(blind_id);
                        matchData.setUser_name(blind_name);
                        matchData.setMatch_img(blind_img);
                        mMatchList.add(matchData);
                    }

                    MatchData matchData_last = new MatchData();
                    matchData_last.setSid("");
                    matchData_last.setUser_name("");
                    matchData_last.setMatch_img("");
//                    matchData_last.setCnt_blind(0);
                    mMatchList.add(matchData_last);
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return matchList_info;
        }

        @Override
        protected void onPostExecute(String[] result) {
            txt_title.setText("소개팅하고자 하는 상대를 선택해주세요.");
            mAdapter.notifyDataSetChanged();

            int_btn_go++;
        }
    }

    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();
    ArrayList<String> hash_name_arr = new ArrayList<>();

    private class PickBlindTask extends AsyncTask<String, Void, Void> {

        String result;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "pick_blind")
                    .add("my_id", MainActivity.my_id)
                    .add("match_id", match_id)
                    .add("blind_id", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "PickBlindTask obj = " + obj);

                    result = obj.getString("result");

                    JSONObject obj_my = obj.getJSONObject("my");
                    String my_name = obj_my.getString("user_name");
                    String my_pic = obj_my.getString("pic1");

                    JSONObject obj_match = obj.getJSONObject("match");
                    String match_id = obj_match.getString("sid");
                    match_name = obj_match.getString("user_name");
                    match_pic = obj_match.getString("pic1");
                    match_fire = obj_match.getString("id_firebase");

                    JSONObject obj_blind = obj.getJSONObject("blind");
                    String blind_id = obj_blind.getString("sid");
                    blind_name = obj_blind.getString("user_name");
                    blind_fire = obj_blind.getString("id_firebase");

                    JSONArray pic_arr = obj_blind.getJSONArray("pic1");
                    for (int i = 0; i < pic_arr.length(); i++) {
                        String img_url = pic_arr.getString(i);
                        if (img_url != null && !img_url.isEmpty()) {
                            img_url = Statics.main_url + img_url;
                        }
                        img_arr.add(img_url);
                        layouts2.add(R.layout.slide_profile1);

//                        Log.e("abc", "Profile obj = " + ", " + img_url);
//                        profileAdapter.addItem(sid, img_url, look_cnt, open);
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
//                        img_arr.add(img_url);
//                        layouts2.add(R.layout.slide_profile1);

//                        profileAdapter.addItem(sid, img_url, look_cnt, open);
                    }


//                    user_id = obj.getString("user_id");
//                    img_url = obj.getString("img_url");
//                    com_img_url =Statics.main_url + img_url;
//                    text = obj.getString("text");
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

//            txt_match_name.setText(match_name);
//            txt_blind_name.setText(blind_name + "님의 프로필");
//
//            viewPager.setAdapter(myViewPagerAdapter);
//            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
//
//            addBottomDots(0);

//            Picasso.with(StartDate.this).load(Statics.main_url + match_pic).fit().placeholder(R.drawable.icon_noprofile_s).into(img_fr_pic);
            if (result.equals("0")) {
                Intent intent = new Intent(SelectMatchMaker.this, StartDate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("match_id", match_id);
                intent.putExtra("match_name", match_name);
                intent.putExtra("match_fire", match_fire);
                intent.putExtra("match_pic", match_pic);

                intent.putExtra("blind_id", blind_id);
                intent.putExtra("blind_name", blind_name);
                intent.putExtra("blind_fire", blind_fire);
                intent.putExtra("blind_hash_name_arr", hash_name_arr);
                intent.putExtra("img_arr", img_arr);
                intent.putExtra("layouts2", layouts2);
                startActivity(intent);
                finish();
            } else if (result.equals("1")) {
                Toast.makeText(SelectMatchMaker.this, "이미 진행했던 소개팅 상대입니다.", Toast.LENGTH_SHORT).show();
            } else {

            }


        }
    }

    @Override
    public void onBackPressed() {
        if (int_btn_go == 0) {
            super.onBackPressed();
        } else if ((int_btn_go > 0)) {

        }
    }
}