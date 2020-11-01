package com.pumkit.chin.chinchinso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EditRecommActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    private EditText editRecomm;

    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editrecomm);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        text = intent.getStringExtra("text");

        editRecomm = (EditText) findViewById(R.id.edit_recomm);
        editRecomm.setText(text);

        Button btn_save_edit_reomm = (Button) findViewById(R.id.btn_save_edit_reomm);
        btn_save_edit_reomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEditString = editRecomm.getText().toString();
                try {
                    getEditString = URLEncoder.encode(getEditString, "utf-8");
                    Log.e("abc", "getEditString : " + getEditString);
                    new EditRecommTask().execute(getEditString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);
        btn_back.setOnTouchListener(mOnTouchListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton b = (ImageButton)v;

            int action=event.getAction();
            Log.e("abc", "클릭이 되었습니다용 = " + action);

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

    // 추천글 업데이트
    public class EditRecommTask extends AsyncTask<String, Void, String> {
//        String result;

        @Override
        protected void onPreExecute() {
//            text =
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            RequestBody body = new FormBody.Builder()
                    .add("opt", "edit_rocomm")
                    .add("my_id", MainActivity.my_id)
                    .add("text", params[0])
                    .build();

//            Request request = new Request.Builder().url(main_url+"fcm/register.php").post(body).build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "bodyStr : " + bodyStr);
                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "obj : " + obj);
                    result = obj.getString("result");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("0")) {
//                Handler hd = new Handler();
//                hd.postDelayed(new Waithandler(), 3000); // 3초 후에 hd Handler 실행

//                Intent intent = new Intent(EditRecommActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(), "일시적인 네트워크 오류가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Waithandler implements Runnable {
        public void run() {
            Intent intent = new Intent(EditRecommActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}