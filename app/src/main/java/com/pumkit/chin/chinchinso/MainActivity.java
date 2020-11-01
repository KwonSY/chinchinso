package com.pumkit.chin.chinchinso;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pumkit.chin.fragments.AnnounceFragment;
import com.pumkit.chin.fragments.ChatFragment;
import com.pumkit.chin.fragments.ChatListFragment;
import com.pumkit.chin.fragments.DateFragment;
import com.pumkit.chin.fragments.MatchFragment;
import com.pumkit.chin.fragments.MyPageFragment;
import com.pumkit.chin.fragments.OpenDateFragment;
import com.pumkit.chin.fragments.SettingFragment;
import com.pumkit.chin.widget.CircleTransform;
import com.pumkit.chin.widget.OkHttpClientSingleton;
import com.pumkit.chin.widget.SessionManager;
import com.pumkit.chin.widget.ViewPagerAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SessionManager session;
    private OkHttpClient httpClient;

    public static DrawerLayout drawerLayout;
    public static Fragment fragment;
    private FragmentManager fm;
    public static FragmentTransaction ft, ft2;

    public static String my_id, myGender;
    public static String myFireId;
    public static String splash_yn = "n";
    private static String update_token_yn;

    private String str_phones;

    private Toolbar toolbar;
    private TabLayout tabLayout;

    public static ArrayList<String> phones_arr = new ArrayList<>();
    public static ArrayList<String> names_arr = new ArrayList<>();

    private String main_url = Statics.main_url;
    private String opt_url = Statics.opt_url;

    View headerLayout;
    ImageView img_my_pic;
    private TextView txt_my_name, txt_have_heart, txt_cnt_poke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("abc", "Main splash_yn splash_yn : " + splash_yn);
        //Connection Check
        if (!isOnline()) {
            buildDialog(this);
        } else if (splash_yn.equals("n")) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

            //FireBase Chat
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            session = new SessionManager(getApplicationContext());

            HashMap<String, String> user = session.getUserDetails();
            my_id = user.get("my_id");
            Statics.my_gender = user.get("mygender");
            Log.e("abc", "my_id in Main = " + my_id + ", gender = " + Statics.my_gender + ", fire = " + firebaseAuth.getCurrentUser());

            if (my_id == null || my_id.equals(null) || firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                if (firebaseAuth.getCurrentUser() != null) {

                    FirebaseUser user_fire = firebaseAuth.getCurrentUser();
                    myFireId = user_fire.getUid();

                    //FCM
                    String token = FirebaseInstanceId.getInstance().getToken();
                    Log.e("abc", "현재 토큰값은 : " + token + ", myFireId = " + myFireId);

                    UpdateTokenFirebase(token);

                    //주소록 가져오기
                    getPhoneNums();

                    str_phones = "";

                    for (int i = 0; i < phones_arr.size(); i++) {
                        str_phones += phones_arr.get(i);

                        if (phones_arr.size() == 1) {

                        } else {
                            if (i < phones_arr.size() - 1) {
                                str_phones += ",";
                            }
                        }

                    }
                    new AutoFrTask().execute();
                    new AccountTask().execute();
                    new VersionTask().execute();
                } else {
                    finish();
                    Log.e("abc", "Go Main --------> Login (myFireId is NULL)" + myFireId);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }

            if (fragment == null) {
                fragment = new DateFragment();
            }

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            DateFragment dateFragment = new DateFragment();

            fm.findFragmentByTag("DATE_FRAGMENT");
            ft.add(R.id.fragment_main, dateFragment);
            ft.addToBackStack(null);

            ft.commit();


            drawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
            nav_view.setNavigationItemSelectedListener(this);

            NavigationView nav_view2 = (NavigationView) findViewById(R.id.nav_view2);
            nav_view2.setNavigationItemSelectedListener(this);

            ImageView btn_menu_date = (ImageView) findViewById(R.id.btn_menu_date);
            btn_menu_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    drawerLayout.openDrawer(nav_menudate);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
//                        drawerLayout.openDrawer(R.id.nav_view);
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
//                    Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }
            });

            ImageView btn_chatlist = (ImageView) findViewById(R.id.btn_chatlist);
            btn_chatlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }
                }
            });



            headerLayout = nav_view.getHeaderView(0);
            img_my_pic = headerLayout.findViewById(R.id.img_my_pic);
            img_my_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFragment.stopAnimate();

                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", my_id);
                    intent.putExtra("match_id", "");
                    startActivity(intent);
                }
            });
            View img_pay_heart = headerLayout.findViewById(R.id.img_pay_heart);
            img_pay_heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFragment.stopAnimate();

                    Intent intent = new Intent(MainActivity.this, PayHeartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            txt_my_name = (TextView) headerLayout.findViewById(R.id.txt_my_name);
            txt_have_heart = (TextView) headerLayout.findViewById(R.id.txt_have_heart);
            txt_cnt_poke = (TextView) headerLayout.findViewById(R.id.txt_cnt_poke);
            txt_cnt_poke.setOnClickListener(mOnClickListener);

//            ListView listview_in_nav = (ListView) findViewById(R.id.listview_in_nav);
//            ChatListAdapter chatListAdapter = new ChatListAdapter();
//            listview_in_nav.setAdapter(chatListAdapter);

            ft2 = getSupportFragmentManager().beginTransaction();
            ft2.add(R.id.fragment_chatlist, new ChatListFragment());
            ft2.commit();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_cnt_poke:
//                    Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("phones_arr", phones_arr);
//                    intent.putExtra("names_arr", names_arr);
//                    startActivity(intent);
                    Fragment fragment = new MyPageFragment();

                    if (fragment != null) {
                        DateFragment.stopAnimate();

                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_main, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }

//                    if (getSupportActionBar() != null) {
//                        getSupportActionBar().setTitle(title);
//                    }

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
                    drawer.closeDrawer(GravityCompat.START);

                    break;
            }
        }
    };

    long pressedTime = 0;

    @Override
    public void onBackPressed() {
//        Log.e("abc", "xxx fm.getFragments() = " + fm.getFragments());

        if (fm.getBackStackEntryCount() == 1) {

            if ( pressedTime == 0 ) {
                Toast.makeText(MainActivity.this, "한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            }
            else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if ( seconds > 2000 ) {
                    Toast.makeText(MainActivity.this, "한 번 더 누르면 종료됩니다." , Toast.LENGTH_LONG).show();
                    pressedTime = 0 ;
                } else {
//                    super.onBackPressed();
                    fm.beginTransaction().remove(fragment).commit();
//                    finish(); // app 종료 시키기
                    this.finishAffinity();
                }
            }
        } else {
            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_date) {
            fragment = new DateFragment();
            title = "Date";
        } else if (id == R.id.nav_opendate) {
            fragment = new OpenDateFragment();
            title = "OpenDate";
        } else if (id == R.id.nav_match) {
            fragment = new MatchFragment();
            title = "Match";
        } else if (id == R.id.nav_mypage) {
            fragment = new MyPageFragment();
            title = "MyPage";
        } else if (id == R.id.nav_announcement) {
            fragment = new AnnounceFragment();
            title = "Announce";
        } else if (id == R.id.nav_setting) {
//            Toast.makeText(this, "세팅", Toast.LENGTH_SHORT).show();
            fragment = new SettingFragment();
            title = "Setting";
        }



        Log.e("abc", "xxxxxxxxx fragment = " + fragment);
        Log.e("abc", "xxxxxxxxx getFragments = " + fm.getFragments());

        if (fragment != null) {
            DateFragment.stopAnimate();

//            ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.fragment_main, fragment);
//            ft.addToBackStack(null);
//            ft.commit();

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            fragment = new DateFragment();

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }



        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //    public void setupViewPager(ViewPager viewPager, Fragment fragment) {
    public void setupViewPager(Fragment fragment) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (fragment == null) {
            viewPagerAdapter.addFrag(new DateFragment(), "ONE");
        } else {
            viewPagerAdapter.addFrag(fragment, "ONE");
        }
        viewPagerAdapter.addFrag(new ChatFragment(), "TWO");
//        viewPager.setAdapter(viewPagerAdapter);
    }

    public static void changeFragment(View view, Fragment fragment) {
        FragmentActivity activity = (FragmentActivity) view.getContext();
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
//
//        if (fragment == null) {
//            viewPagerAdapter.addFrag(new DateFragment(), "ONE");
//        } else {
//            viewPagerAdapter.addFrag(fragment, "ONE");
//        }
//        viewPagerAdapter.addFrag(new ChatFragment(), "TWO");

        ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_main, fragment);
        ft.commit();
    }



//    class ViewPagerAdapterxx extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapterxx(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFrag(Fragment fragment, String title) {
////            Fragment fragment1 = new Fragment();
////            fragment1 = fragment;
////            fragment.setTa
//
////            View view = new View(MainActivity.this);
////            view.setTag("fr1");
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
////            return mFragmentTitleList.get(position);
//            return null;
//        }
//    }


    //전화번호부 가져오기
    public void getPhoneNums() {
        String[] arrProjection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        String[] arrPhoneProjection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // get user list
        Cursor clsCursor = this.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                arrProjection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                null, null
        );

        int count = 0;

        while (clsCursor.moveToNext()) {
            String strContactId = clsCursor.getString(0);
            String strContactName= clsCursor.getString(1);
            Log.e("Unity", count + ", 연락처 strContactId : " + strContactId);

            // phone number
            Cursor clsPhoneCursor = this.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrPhoneProjection,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
                    null, null
            );

            while (clsPhoneCursor.moveToNext()) {
                String dial = clsPhoneCursor.getString(0);

                Log.e("Unity", count + ", 연락처 사용자 폰번호 : " + dial);
                dial = dial.replace("-", "");
                Log.e("Unity", count + ", 연락처 사용자 dial : " + dial);
                phones_arr.add(dial);
                names_arr.add(strContactName);

                count++;
            }

            clsPhoneCursor.close();
        }
        clsCursor.close();
    }

    // 자동친구추가 쿼리
    private class AutoFrTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            FormBody body = new FormBody.Builder()
                    .add("opt", "auto_fr_req")
                    .add("my_id", my_id)
                    .add("phones", str_phones)
                    .build();

            Request request = new Request.Builder().url(opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

//                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
//                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    //토근값 업데이트 - update token xxxxxxxxxxx
    private class FCMTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody body = new FormBody.Builder()
                    .add("my_id", my_id)
                    .add("token", params[0])
                    .build();
            Log.e("FCM", "FCM 111 = " + params[0]);

            Request request = new Request.Builder().url(main_url + "fcm/register.php").post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("FCM", "FCM_obj 222 = " + bodyStr);
//                    JSONObject obj = new JSONObject(bodyStr);
//                    Log.e("FCM", "FCM_obj 333 = " + obj);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    private class AccountTask extends AsyncTask<Void, Void, Void> {

        String my_name, my_pic_url, cnt_heart, cnt_poke_to_me, cnt_my_poke;
        int i_cnt_poke_to_me = 0;
        int i_cnt_my_poke = 0;
        int i_total = 0;

        @Override
        protected void onPreExecute() {
//            txt_have_heart = (TextView) findViewById(R.id.txt_have_heart);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("user_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "AccountTask obj = " + obj);

                    my_name = obj.getString("user_name");
                    my_pic_url = obj.getString("pic1");
                    cnt_heart = obj.getString("cnt_heart");

                    cnt_poke_to_me = obj.getString("cnt_poke_to_me");
                    JSONArray pokeToMe_arr = obj.getJSONArray("poke_to_me");
                    for (int i=0; i<pokeToMe_arr.length(); i++) {
                        JSONObject pokeToMe_obj = pokeToMe_arr.getJSONObject(i);
                        String type = pokeToMe_obj.getString("type");
                        JSONObject match_obj = pokeToMe_obj.getJSONObject("match");
                    }

                    cnt_my_poke = obj.getString("cnt_my_poke");

                    i_cnt_poke_to_me = Integer.parseInt(cnt_poke_to_me);
                    i_cnt_my_poke = Integer.parseInt(cnt_my_poke);
                    i_total = i_cnt_poke_to_me + i_cnt_my_poke;

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
            Picasso.with(headerLayout.getContext()).load(Statics.main_url+my_pic_url).fit().centerCrop().placeholder(R.drawable.icon_noprofile).transform(new CircleTransform()).into(img_my_pic);
//            Picasso.with(context).load(R.drawable.stub_image).placeholder(R.drawable.stub_image).error(R.drawable.stub_image).tag(context).transform(new CircleTransform()).into(holder.image_datelist);
            txt_my_name.setText(my_name);
            txt_have_heart.setText(cnt_heart);
            if (i_total == 0) {
                txt_cnt_poke.setVisibility(View.GONE);
            } else {
                txt_cnt_poke.setText("새로운 소개팅 " + i_total + "개가 있습니다.");
            }
        }
    }

    private class VersionTask extends AsyncTask<Void, Void, Void> {
        String app_version = null;

        double old_version, new_version;

        @Override
        protected void onPreExecute() {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                app_version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "version")
                    .add("device", "android")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();



            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "AccountTask obj = " + obj);

                    String version = obj.getString("version");

                    old_version = Double.parseDouble(app_version);
                    new_version = Double.parseDouble(version);
//                    Log.e("abc", "old_version = " + old_version);
//                    Log.e("abc", "new_version = " + new_version);


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

            if (old_version < new_version) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                alert_confirm.setMessage("업데이트 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES'
//                            downloadFile("http://pumkit.com/apks/app-release.apk", );
                                new UpdateTask().execute();
//                                download_WebLink();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();

            }


        }
    }


    private class UpdateTask extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double... params) {
            String path = Environment.getExternalStorageDirectory() + "/Android/data/com.pumkit.chin.chinchinso/apk";

            File dir = new File(path);

            try {
                if (!dir.exists()) dir.mkdirs();

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "ERROR: 업데이트 폴더를 생성할 수 없습니다.", Toast.LENGTH_LONG).show();
                return null;
            }

            try {
//                String url = appUpdateMap.get("url").toString();
                String url = "http://pumkit.com/apks/ChinChin-release.apk";
                String apkPath = path + "/ChinChin-release.apk";

                long s = download(url, apkPath);

                if (s > 0) {
                    File apk = new File(apkPath);

                    if (apk.exists()) {
                        Uri uri = Uri.fromFile(apk);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "ERROR: 업데이트 파일을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                    }

                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }

            return null;
        }
    }

    public long download(String url, String path) {

        long start = System.currentTimeMillis();

        FileOutputStream fos = null;
        InputStream is = null;

        try {
            File f = new File(path);
            if (f.exists()) {
                if (!f.delete()) return -1;
            }
            URL u  = new URL(url);
            URLConnection conn = u.openConnection();
            conn.setReadTimeout(5000);

            is = conn.getInputStream();
            fos = new FileOutputStream(f);

            byte[] b = new byte[10240];

            for (int n; (n = is.read(b)) != -1;)
                fos.write(b, 0, n);
        } catch (Exception e) {
            return -1;
        } finally {
            try {
                if (fos != null) fos.close();
                if (is != null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        return (end - start);
    }











    // UPDATE token in Firebase  //token값 업데이트하기 in Firebase
    private void UpdateTokenFirebase(String token) {
        token = FirebaseInstanceId.getInstance().getToken();

        if (TextUtils.isEmpty(update_token_yn)) {
            Log.e("abc", "토근값이 업데이트 됩니다 " + update_token_yn);
            databaseReference.child("users").child(myFireId).child("token").setValue(token);
            update_token_yn = "y";
        } else {
            Log.e("abc", "이미 토큰 업데이트함 " + update_token_yn);

        }
//        databaseReference.child("users").child(myFireId).child("token").setValue(userVo);

//        String key = databaseReference.child("users").child(myFireId).push().getKey();
//        UserVo userVo = new UserVo(user_name, my_id, token);
//        Map<String, Object> postValues = userVo.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/users/" + myFireId + "/token/" + key, postValues);
//        childUpdates.put("/users/" + userId + "/" + key, postValues);

//        databaseReference.updateChildren(childUpdates);


    }

    //xxxxxxxxx
    private void sendRegistrationToServer(String token, String my_id) {

        RequestBody body = new FormBody.Builder()
                .add("my_id", my_id)
                .add("token", token)
                .build();

//        FormBody body = new FormBody.Builder()
//                .add("my_id", my_id)
//                .add("token", token)
//                .build();

        //request
        Request request = new Request.Builder()
                .url(main_url+"fcm/register.php")
                .post(body)
                .build();

        try {
//            client.newCall(request).execute();
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("FCM_Token_222", "obj = " + obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Connection WiFi
    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("네트워크 연결 재시도");
        builder.setMessage("네트워크가 불안정합니다.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainActivity.this.startActivity(intent);
                finish();

//                SinInternetFragment firstFragment = new SinInternetFragment();
//                ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction()
//                        .add(R.id.frame, firstFragment).commit();
            }
        });
        builder.show();

        return builder;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void setLogoClick(final View view) {
        final ImageView logo_chinchin = (ImageView) view.findViewById(R.id.logo_chinchin);
        logo_chinchin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DateFragment();

                changeFragment(logo_chinchin, fragment);
            }
        });
    }

}