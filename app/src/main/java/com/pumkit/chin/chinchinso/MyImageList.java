package com.pumkit.chin.chinchinso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pumkit.chin.widget.MyPicsAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyImageList extends AppCompatActivity {

    private OkHttpClient httpClient;

    public static ListView listView_my_pic;
    public static MyPicsAdapter myImagesAdapter;

//    String user_name;
//    private ArrayList<String> img_arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_imagelist);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

//        Intent intent = getIntent();
//        img_arr = intent.getStringArrayListExtra("img_arr");

        TextView btn_add_article = (TextView) findViewById(R.id.btn_add_article);
        btn_add_article.setOnClickListener(onClickListener);

        myImagesAdapter = new MyPicsAdapter();
        listView_my_pic = (ListView) findViewById(R.id.listView_my_pic);
        listView_my_pic.setAdapter(myImagesAdapter);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyPicsTask().execute();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
                case R.id.btn_add_article:
                    myImagesAdapter.clearItemList();

                    Intent intent = new Intent(MyImageList.this, AddArticle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton b = (ImageButton)v;

            int action=event.getAction();

            if(action==MotionEvent.ACTION_DOWN) {
                b.setBackgroundResource(R.drawable.border_transparent);
            } else if(action==MotionEvent.ACTION_UP){
                b.setBackgroundResource(0);

                onBackPressed();
            } else if (action == MotionEvent.ACTION_CANCEL) {
                b.setBackgroundResource(0);
            }
            return false;
        }
    };

    private class MyPicsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_pics")
                    .add("my_id", MainActivity.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    String user_name = obj.getString("user_name");

                    JSONArray arr_pics = obj.getJSONArray("pics");
                    for (int j = 0; j < arr_pics.length(); j++) {
                        JSONObject obj_pics = arr_pics.getJSONObject(j);

                        String pic_id = obj_pics.getString("sid");
                        String img_url = obj_pics.getString("url");
                        String main = obj_pics.getString("main");

                        myImagesAdapter.addItem(pic_id, img_url, main);
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
            myImagesAdapter.notifyDataSetChanged();
        }
    }

    // 글 삭제
    public class DelArticleTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "del_article")
                    .add("my_id", MainActivity.my_id)
                    .add("profile_id", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
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
            myImagesAdapter.notifyDataSetChanged();
        }
    }
}