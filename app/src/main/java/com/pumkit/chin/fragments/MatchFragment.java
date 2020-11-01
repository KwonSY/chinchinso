package com.pumkit.chin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pumkit.chin.chinchinso.EditRecommActivity;
import com.pumkit.chin.chinchinso.FriendsListActivity;
import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.MyFrDateAdapter;
import com.pumkit.chin.widget.MyWantFrAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.MainActivity.my_id;

public class MatchFragment extends Fragment {

    private OkHttpClient httpClient;

    private String my_name, my_pic, fr_id, fr_name, fr_pic, my_recomm;

    TextView title_my_poke;
    private ImageView img_my_pic, img_fr_pic;
    private TextView txt_fr_name, txt_my_recomm;

//    private MyPokeAdapter myPokeAdapter;
    MyWantFrAdapter myWantFrAdapter;
    private ListView list_fr_status;
    private MyFrDateAdapter myFrDateAdapter;


    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {

        new IamMatchTask().execute();

        TextView btn_edit_recomm = (TextView) getActivity().findViewById(R.id.btn_edit_recomm);
        btn_edit_recomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditRecommActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("text", my_recomm);
                startActivity(intent);
            }
        });
//        img_my.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ProfileActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("user_id", my_id);
//                startActivity(intent);
//            }
//        });

//        ListView list_my_poke = (ListView) getView().findViewById(R.id.list_my_poke);
//        myPokeAdapter = new MyPokeAdapter();
//        list_my_poke.setAdapter(myPokeAdapter);
//
//        ListView list_want_fr = (ListView) getView().findViewById(R.id.list_want_fr);
//        myWantFrAdapter = new MyWantFrAdapter();
//        list_want_fr.setAdapter(myWantFrAdapter);
        TextView btn_change_fr = (TextView) getView().findViewById(R.id.btn_change_fr);
        btn_change_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        list_fr_status = (ListView) getView().findViewById(R.id.list_fr_status);
        myFrDateAdapter = new MyFrDateAdapter();
        list_fr_status.setAdapter(myFrDateAdapter);

        MainActivity.setLogoClick(getView());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // 내가 주선자다
    public class IamMatchTask extends AsyncTask<Void, Void, Void> {
        int cnt_arr = 0;
        TextView title_fr_status;

        @Override
        protected void onPreExecute() {
//            title_my_poke = (TextView) getView().findViewById(R.id.title_my_poke);

            img_my_pic = (ImageView) getView().findViewById(R.id.img_my);
//            txt_my_name = (TextView) getView().findViewById(R.id.txt_my_name);

            img_fr_pic = (ImageView) getView().findViewById(R.id.img_fr);
            txt_fr_name = (TextView) getView().findViewById(R.id.txt_fr_name);
            txt_my_recomm = (TextView) getView().findViewById(R.id.txt_my_recomm);

            title_fr_status = (TextView) getView().findViewById(R.id.title_fr_status);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "iam_match")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Profile obj = " + obj);

                    JSONObject obj_my = obj.getJSONObject("my");
                    my_name= obj_my.getString("user_name");
                    my_pic = obj_my.getString("pic1");

                    JSONObject obj_fr = obj.getJSONObject("my_friend");
                    fr_id = obj_fr.getString("sid");
                    fr_name = obj_fr.getString("user_name");
                    fr_pic = obj_fr.getString("pic1");
                    my_recomm = obj_fr.getString("recomm_note");

                    JSONArray want_my_fr = obj.getJSONArray("poke_to_my_fr");
                    cnt_arr = want_my_fr.length();
                    for (int i=0; i<want_my_fr.length(); i++) {
                        JSONObject obj_want = want_my_fr.getJSONObject(i);

                        JSONObject obj_poke = obj_want.getJSONObject("poker");
                        String poke_id = obj_poke.getString("sid");
                        String poke_name = obj_poke.getString("user_name");
                        String poke_pic = obj_poke.getString("pic1");

                        JSONObject obj_friend = obj_want.getJSONObject("friend");
                        String fr_id = obj_friend.getString("sid");
                        String fr_name = obj_friend.getString("user_name");
                        String fr_pic = obj_friend.getString("pic1");
                        Log.e("abc", "fr_id obj = " + fr_id +"," + fr_name +","+ fr_pic);

//                        myWantFrAdapter.addItem(poke_id, poke_name, poke_pic, fr_id, fr_name, fr_pic);
                        myFrDateAdapter.addItem(poke_id, poke_name, poke_pic, fr_id, fr_name, fr_pic);
                    }

                } else {
                    Log.e("abc", "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //1 My Profile
            Log.e("abc", "my_pic : " + my_pic);
            Picasso.with(getContext()).load(Statics.main_url + my_pic).placeholder(R.drawable.icon_noprofile_circle_m).error(R.drawable.icon_noprofile_circle_m).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_my_pic);
//            txt_my_name.setText(my_name);

//            if (my_poke_id.equals("null")) {
//                title_my_poke.setVisibility(View.GONE);
//            }
//            Picasso.with(getContext()).load(my_pic).fit().placeholder(R.drawable.icon_noprofile_m).transform(new CircleTransform()).into(img_my);
//            txt_my_name.setText(my_name);

            //2 My Poke
//            myPokeAdapter.notifyDataSetChanged();

            //3 My Fr
            //.transform(new CircleTransform())


            Picasso.with(getContext()).load(Statics.main_url + fr_pic).placeholder(R.drawable.circle_dotted).error(R.drawable.circle_dotted).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_fr_pic);
            if (fr_name.equals("null")) {
                txt_fr_name.setText("추천할 친구를 선택해주세요");
            } else {
                txt_fr_name.setText(fr_name);
            }
//            txt_fr_appeal.setText(fr_appeal);
            txt_my_recomm.setText(my_recomm);

            //4 list_want_fr
//            myWantFrAdapter.notifyDataSetChanged();

            //5
            if (cnt_arr == 0) {
                title_fr_status.setVisibility(View.GONE);
                list_fr_status.setVisibility(View.GONE);
            } else {
                title_fr_status.setVisibility(View.VISIBLE);
                list_fr_status.setVisibility(View.VISIBLE);

                myFrDateAdapter.notifyDataSetChanged();
            }
        }
    }
}