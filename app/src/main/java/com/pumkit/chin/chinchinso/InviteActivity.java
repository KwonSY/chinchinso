package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.pumkit.chin.fragments.OpenDateFragment;
import com.pumkit.chin.widget.SessionManager;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class InviteActivity extends Activity {

    private OkHttpClient httpClient;

    private AutoCompleteTextView auto_to_kakao;
    EditText edit_kakao_contents;
    private Button btn_go_public, btn_invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        SessionManager session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        String invite_cnt = user.get("invite_cnt");
        Log.e("abc", "xxxxxxxx invite_cnt = " + invite_cnt);
        if (invite_cnt == null) {
            session.saveInviteSession("1");
        } else if (invite_cnt.equals("1")) {
            Intent intent = new Intent(InviteActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            session.saveInviteSession("1");
        }
        session.saveInviteSession("1");

        ImageView btn_go_main = (ImageView) findViewById(R.id.btn_go_main);
        btn_go_main.setOnClickListener(onClickListener);

        auto_to_kakao = (AutoCompleteTextView) findViewById(R.id.auto_to_kakao);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, MainActivity.names_arr);
        auto_to_kakao.setAdapter(adapter);
        auto_to_kakao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_kakao_contents = (EditText) findViewById(R.id.edit_kakao_contents);

        btn_go_public = (Button) findViewById(R.id.btn_go_public);
        btn_go_public.setOnClickListener(onClickListener);
        btn_invite = (Button) findViewById(R.id.btn_invite);
//        btn_invite.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_go_main:
                    Intent intent = new Intent(InviteActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_go_public:
                    Fragment fragment = new OpenDateFragment();

                    MainActivity.fragment = fragment;

                    Intent intent2 = new Intent(InviteActivity.this, MainActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

//                    MainActivity.changeFragment(InviteActivity.this, fragment);

                    break;
                case R.id.btn_invite:
//                    Intent intent = new Intent(getActivity(), InviteActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);

                    try {
                        final KakaoLink kakaoLink = KakaoLink.getKakaoLink(InviteActivity.this);
                        final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

                        kakaoBuilder.addText(edit_kakao_contents.getText().toString());

                        String url = Statics.main_url + "full-ci.png";
                        kakaoBuilder.addImage(url, 1080, 1920);

                        kakaoBuilder.addAppButton("앱 실행");

                        kakaoLink.sendMessage(kakaoBuilder, InviteActivity.this);
                    } catch (KakaoParameterException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    public void shareKakao(View v) {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoBuilder.addText(edit_kakao_contents.getText().toString());

//            String url = Statics.main_url + "full-ci.png";
//            kakaoBuilder.addImage(url, 1080, 1920);

            kakaoBuilder.addAppButton("친친소 시작");

            kakaoLink.sendMessage(kakaoBuilder, this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

}