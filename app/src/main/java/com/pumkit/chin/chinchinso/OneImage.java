package com.pumkit.chin.chinchinso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import uk.co.senab.photoview.PhotoView;

public class OneImage extends AppCompatActivity {
    private PhotoView one_pic;

    private String img_url;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_image);

        queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        img_url = intent.getStringExtra("img_url");

        one_pic = (PhotoView) findViewById(R.id.one_pic);

        //#f1594a
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Volley ImageView
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("사진 읽어오는 중...");
        progress.show();

        ImageRequest request = new ImageRequest(img_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                one_pic.setImageBitmap(response);
                progress.dismiss();
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Log.e("abc", "사진이 없습니다 Exception", error);
                    }
                }
        );
        queue.add(request);
    }

}