package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.SessionManager;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ResetPswActivity extends Activity {

    private OkHttpClient httpClient;
    private FirebaseAuth firebaseAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpsw);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        firebaseAuth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());



        final ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundResource(R.drawable.border_transparent);
                } else if (action == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundResource(0);

                    onBackPressed();
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    btn_back.setBackgroundResource(0);
                }

                return true;
            }
        });
    }

    private class ResetPswTask extends AsyncTask<String, Void, Void> {
        String yn;

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "reset_psw")
                    .add("my_id", MainActivity.my_id)
                    .add("new_psw", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "UPublicYnTask obj = " + obj);

                    String result = obj.getString("result");
                    yn = obj.getString("yn");
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
//            if (yn.equals("y")) {
//                bool_public_yn = true;
//            } else {
//                bool_public_yn = false;
//            }
//
//            setTextPublicYn(bool_public_yn);
        }
    }
}