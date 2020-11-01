package com.pumkit.chin.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.pumkit.chin.chinchinso.InviteActivity;
import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.ProfileActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.SelectMatchMaker;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.DatedListData;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.DateListSwipeAdapter;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.PickerDateAdapter;
import com.pumkit.chin.widget.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.Statics.main_url;

public class DateFragment extends Fragment {

    private OkHttpClient httpClient;

    //소개팅리스트 몇 번쨰?
    public static int cnt_dated = 0;
    //소개팅 가능한지? 가능 이성수
    public static int i_cnt_blind;

    private String match_id, match_name, match_pic;
    private String blind_id, blind_name, blind_id_firebase, blind_pic;

    ViewPager viewpager_datelist;
    DateListSwipeAdapter dateListSwipeAdapter;
    private RelativeLayout layout_my_box, layout_match_box;
    private ImageView img_my_pic, img_match;
//    ImageView img_blind;
//    private ImageSwitcher imageSwitcher_blind;
//    private ImageSwitcherPicasso mImageSwitcherPicasso;
    private TextView txt_my_name, title_match1, txt_match1;
    private TextView txt_match, txt_blind;

    private TextView txt_date_date;
//    private Button btn_new_date;
    private ImageView btn_left, btn_right;

    private static AnimatorSet animatorSet;

    private ArrayList<DatedListData> mDatedListList = new ArrayList<>();

    private Handler mHandler = new Handler();

    public DateFragment() {
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_date, container, false);
//        ViewGroup rootView;
//        View v = new CustomDrawingFragment.DemoView(rootView.getContext());
//        View v = new DateAnimationView(rootView.getContext());
//        rootView.addView(v);

//        return inflater.inflate(R.layout.fragment_date, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initControls();
    }

    private void initControls() {

        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        String invite_cnt = user.get("invite_cnt");
        Log.e("abc", "xxxxxxx invite_cnt = " + invite_cnt);
        if (invite_cnt == null) {
            Intent intent = new Intent(getActivity(), InviteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        new DatedListTask().execute();

        final LottieAnimationView lottie_circle = (LottieAnimationView) getActivity().findViewById(R.id.lottie_circle);
        lottie_circle.setAnimation("circle_datedlist.json");
        lottie_circle.loop(false);
//        lottie_circle.playAnimation();

        viewpager_datelist = (ViewPager) getActivity().findViewById(R.id.viewpager_datelist);
        dateListSwipeAdapter = new DateListSwipeAdapter(getActivity());
        viewpager_datelist.addOnPageChangeListener(viewListener);

//        viewPager.setAdapter(new DateListSwipeAdapter(getChildFragmentManager()));

        layout_my_box = (RelativeLayout) getActivity().findViewById(R.id.layout_my_box);
        img_my_pic = (ImageView) getActivity().findViewById(R.id.img_my_pic);
        img_my_pic.setOnClickListener(onClickListener);

        layout_match_box = (RelativeLayout) getActivity().findViewById(R.id.layout_match_box);
        img_match = (ImageView) getActivity().findViewById(R.id.img_match);
        img_match.setOnClickListener(onClickListener);

        title_match1 = (TextView) getActivity().findViewById(R.id.title_match1);
        txt_match1 = (TextView) getView().findViewById(R.id.txt_match1);

        txt_match = (TextView) getView().findViewById(R.id.txt_match);
        txt_blind = (TextView) getView().findViewById(R.id.txt_blind);


        txt_date_date = (TextView) getActivity().findViewById(R.id.date_date);

        fadeInWholeLayout(layout_my_box, 410);
        fadeInWholeLayout(viewpager_datelist, 700);
        fadeInWholeLayout(layout_match_box, 1000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                lottie_circle.playAnimation();
            }
        }, 150);

        btn_left = (ImageView) getView().findViewById(R.id.btn_left);
        btn_right = (ImageView) getView().findViewById(R.id.btn_right);
        btn_left.setOnClickListener(onClickListener);
        btn_right.setOnClickListener(onClickListener);

    }

    public void fadeInWholeLayout(View root, int delay_time) {

        root.setVisibility(View.VISIBLE);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(root, View.ALPHA, 0, 1);
        fadeIn.setDuration(delay_time);
        fadeIn.setStartDelay(delay_time);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();
    }

    public static void stopAnimate() {
//        animatorSet.cancel();
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_my_pic:
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", MainActivity.my_id);
                    intent.putExtra("match_id", "");
                    startActivity(intent);

                    break;
                case R.id.img_match:
                    if (dateListSwipeAdapter.getCurrentItem() == 0) {

                    } else {
                        Toast.makeText(getActivity(), match_name + "님은 " + blind_name + "님을 주선한 친구입니다.", Toast.LENGTH_SHORT).show();
                    }

                    break;
//                case R.id.txt_new_date:
//                    if (cnt_dated == 0 || i_cnt_blind > 0) {
//                        mStartSelectMatchMaker();
//                    } else {
//                        Toast.makeText(getActivity(),  "친친팅을 할 주선자 친구를 먼저 초대해주세요.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    break;
                case R.id.btn_left:
                    cnt_dated--;
                    setDatedList(cnt_dated);

                    break;
                case R.id.btn_right:
                    cnt_dated++;
                    setDatedList(cnt_dated);

                    break;
                case R.id.btn_go_public:
                    Fragment fragment = new OpenDateFragment();

                    MainActivity.changeFragment(getView(), fragment);

                    break;
                case R.id.btn_invite:
                    try {
                        final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
                        final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

                        kakaoBuilder.addText("친친소 테스트");

                        String url = Statics.main_url + "full-ci.png";
                        kakaoBuilder.addImage(url, 1080, 1920);

                        kakaoBuilder.addAppButton("앱 실행");

                        kakaoLink.sendMessage(kakaoBuilder, getActivity());
                    } catch (KakaoParameterException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
//            int mCurrentPage;
            cnt_dated = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            setDatedList(cnt_dated);
        }
    };

    private void setDatedList(int cnt_dated) {
        Log.e("abc", "xxxxx cnt_dated = " + cnt_dated);
        Log.e("abc", "xxxxx mDatedListList.size() = " + mDatedListList.size());

        if (cnt_dated == mDatedListList.size()) {
            cnt_dated = 0;
        } else if (cnt_dated < 0) {
            cnt_dated = mDatedListList.size() - 1;
        }

        if (cnt_dated == 0) {
            Picasso.with(getActivity()).load(R.drawable.btn_invite_fr).placeholder(R.drawable.btn_invite_fr).error(R.drawable.btn_invite_fr).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);
            title_match1.setText("주선자");
            txt_match1.setText("초대");

            txt_date_date.setText("");
            txt_match.setText("새로운 소개팅을");
            txt_blind.setText("진행해보세요.");

        } else {
            Picasso.with(getActivity()).load(main_url + mDatedListList.get(cnt_dated).getMatch_pic()).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);
//        Picasso.with(getActivity()).load(main_url + mDatedListList.get(cnt_dated).getBlind_pic()).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(mImageSwitcherPicasso);
            title_match1.setText("주선자");
            txt_match1.setText(mDatedListList.get(cnt_dated).getMatch_name());

            txt_date_date.setText(mDatedListList.get(cnt_dated).getDate_date());
            txt_match.setText(mDatedListList.get(cnt_dated).getMatch_name() + "의 친구");
            txt_blind.setText(mDatedListList.get(cnt_dated).getBlind_name());
        }

        viewpager_datelist.setCurrentItem(cnt_dated);
    }

    public static void mStartSelectMatchMaker(Context context) {
//        animatorSet.cancel();


        Intent intent = new Intent(context, SelectMatchMaker.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("my_pic", my_pic);

//        View rootView = ((Activity)context).Window.DecorView.FindViewById(Android.Resource.Id.Content);
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(R.id.layout_date);

        Pair<View, String> pair1 = Pair.create(rootView.findViewById(R.id.img_my_pic), "myImage");
        Pair<View, String> pair2 = Pair.create(rootView.findViewById(R.id.img_match), "ImageMatch");
        Pair<View, String> pair3 = Pair.create(rootView.findViewById(R.id.viewpager_datelist), "ImageBlind");
//vvvvvvvvvvvv
//        Drawable backgroundDrawable = DrawableCompat.wrap(rootView.findViewById(R.id.viewpager_datelist).getBackgroundDrawable()).mutate();
//        DrawableCompat.setTint(rootView.findViewById(R.id.viewpager_datelist).getDrawableState(), Color.RED);
        //ContextCompat.getColor(context, R.color.another_nice_color)
        rootView.findViewById(R.id.viewpager_datelist).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.set

                return false;
            }
        });

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, pair1, pair2, pair3);
        context.startActivity(intent, optionsCompat.toBundle());
    }

    // 소개팅 받았던 리스트
    public static String my_name, my_pic;

    private class DatedListTask extends AsyncTask<Void, Void, Void> {
        ProgressBar progressBar;
//        RelativeLayout layout_nofriend;
        RelativeLayout layout_date;

        String cnt_blind;

        String str_time_date;

        @Override
        protected void onPreExecute() {
            PickerDateAdapter.listViewItemList.clear();

            progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
//            layout_nofriend = (RelativeLayout) getView().findViewById(R.id.layout_nofriend);

            layout_date = (RelativeLayout) getView().findViewById(R.id.layout_date);
            txt_my_name = (TextView) getView().findViewById(R.id.txt_my_name);
            txt_match1 = (TextView) getView().findViewById(R.id.txt_match1);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "dated_list")
                    .add("my_id", MainActivity.my_id)
                    .build();
            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "dated_list root = " + obj);

                    my_pic = obj.getString("my_pic");
                    my_name = obj.getString("my_name");
                    cnt_blind = obj.getString("cnt_blind");
                    i_cnt_blind = Integer.parseInt(cnt_blind);

                    mDatedListList.add(new DatedListData("","", "", "", "", "", "R.drawable.icon_circle_none_dotted_l", ""));
                    dateListSwipeAdapter.addItem("R.drawable.btn_circle_new_date");

                    JSONArray arr = obj.getJSONArray("dated_list");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj2 = arr.getJSONObject(i);

                        str_time_date = obj2.getString("time_date");

                        JSONObject match_obj = obj2.getJSONObject("match");
                        match_id = match_obj.getString("sid");
                        match_name = match_obj.getString("user_name");
                        match_pic = match_obj.getString("pic1");

                        JSONObject blind_obj = obj2.getJSONObject("blind");
                        blind_id = blind_obj.getString("sid");
                        blind_name = blind_obj.getString("user_name");
                        blind_pic = blind_obj.getString("pic1");
                        blind_id_firebase = blind_obj.getString("id_firebase");
                        Log.e("abc", "애드 몇 번했게? = ");
                        DatedListData datedListData = new DatedListData(str_time_date, match_id, match_name, match_pic, blind_id, blind_name, blind_pic, blind_id_firebase);
                        mDatedListList.add(datedListData);
                        dateListSwipeAdapter.addItem(blind_pic);
                    }
                }
            } catch (Exception e) {
                Log.e("abc", "Record 실패 = " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressBar.setVisibility(View.GONE);

//            dateListSwipeAdapter.notifyDataSetChanged();

            Picasso.with(getActivity()).load(main_url + my_pic).placeholder(R.drawable.icon_noprofile_m).error(R.drawable.stub_image).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_my_pic);
            Log.e("abc", "mDatedListList.size() = " + mDatedListList.size());
            if (mDatedListList.size() == 1) {
                btn_left.setVisibility(View.GONE);
                btn_right.setVisibility(View.GONE);

                txt_date_date.setText("");
                txt_match.setText("새로운 소개팅을 진행해보세요");
            } else {
//                if (mDatedListList.size() == 1) {
//                    btn_left.setVisibility(View.GONE);
//                    btn_right.setVisibility(View.GONE);
//                }

//                layout_nofriend.setVisibility(View.GONE);
                layout_date.setVisibility(View.VISIBLE);

                if (Statics.my_gender.equals("m")) {
                    Picasso.with(getActivity()).load(R.drawable.icon_noprofile_m).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);

//                    Picasso.with(getActivity()).load(main_url + mDatedListList.get(1).getMatch_pic()).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);
//                    Picasso.with(getActivity()).load(main_url + mDatedListList.get(0).getBlind_pic()).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(mImageSwitcherPicasso);
                } else if (Statics.my_gender.equals("f")) {
                    Picasso.with(getActivity()).load(R.drawable.icon_noprofile_m).placeholder(R.drawable.icon_noprofile_f).error(R.drawable.icon_noprofile_f).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);

//                    Picasso.with(getActivity()).load(main_url + mDatedListList.get(1).getMatch_pic()).placeholder(R.drawable.icon_noprofile_m).error(R.drawable.icon_noprofile_m).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(img_match);
//                    Picasso.with(getActivity()).load(main_url + mDatedListList.get(0).getBlind_pic()).placeholder(R.drawable.icon_noprofile_m).error(R.drawable.icon_noprofile_m).fit().centerCrop().tag(getActivity()).transform(new CircleTransform()).into(mImageSwitcherPicasso);
                }
                txt_my_name.setText(my_name);
                title_match1.setText("친구");
                txt_match1.setText("초대");

//                txt_date_date.setText(mDatedListList.get(0).getDate_date());
//                txt_match.setText(mDatedListList.get(0).getBlind_name());
//                txt_blind.setText(mDatedListList.get(0).getMatch_name());
                txt_date_date.setText("");
                txt_match.setText("새로운 소개팅을");
                txt_blind.setText("진행해보세요.");
            }

//            if (i_cnt_blind == 0) {
//                btn_new_date.setVisibility(View.GONE);
//                btn_new_date.setText("카톡친구초대");
//            } else {
//                btn_new_date.setVisibility(View.VISIBLE);
//            }
            viewpager_datelist.setAdapter(dateListSwipeAdapter);
        }
    }

//    public void shareKakao(View v) {
//        try {
//            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
//            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//            kakaoBuilder.addText("친친소 테스트");
//
//            String url = Statics.main_url + "full-ci.png";
//            kakaoBuilder.addImage(url, 1080, 1920);
//
//            kakaoBuilder.addAppButton("앱 실행");
//
//            kakaoLink.sendMessage(kakaoBuilder, getActivity());
//        } catch (KakaoParameterException e) {
//            e.printStackTrace();
//        }
//    }
}