package com.pumkit.chin.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.MyImageList;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.MemberData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyPicsAdapter extends BaseAdapter {

    private OkHttpClient httpClient;

    public ArrayList<MemberData> listViewItemList = new ArrayList<MemberData>() ;

    public MyPicsAdapter() {
        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    public class ViewHolder {
        ImageView img_my;
//        TextView user_name;

        ImageView img_main, btn_more;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();
        final View finalConvertView = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_mypics, parent, false);
        }

        holder = new ViewHolder();
        holder.img_my = (ImageView) convertView.findViewById(R.id.poker_img);
//        holder.user_name = (TextView) convertView.findViewById(R.id.poker_name);
        holder.img_main = (ImageView) convertView.findViewById(R.id.img_main);
        holder.btn_more = (ImageView) convertView.findViewById(R.id.btn_more);

        final MemberData memberData = listViewItemList.get(position);

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenu().add(Menu.NONE, 1, 1, "대표사진 설정");
                popupMenu.getMenu().add(Menu.NONE, 2, 2, "삭제");
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if(i == 1) {
//                            String.valueOf(finalConvertView)
                            Toast.makeText(v.getContext(), "선택한 프로필이 대표사진으로 등록하였습니다.", Toast.LENGTH_SHORT).show();
                            new SetProfileMainTask().execute(memberData.getSid());

                            return true;
                        } else if (i == 2) {
//                            new OneArticle.DelArticle().execute();
//                            MyImageList.DelArticleTask = new MyImageList.DelArticleTask.execute(memberData.getSid());
//                            listViewItemList.remove(getItem(pos));
//                            removeItem(pos);
                            String str_pos = String.valueOf(pos);
                            new DelArticleTask().execute(memberData.getSid(), str_pos);

                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        });

        Picasso.with(context).load(Statics.main_url+memberData.getPic1()).fit().centerCrop().placeholder(R.drawable.icon_noprofile).into(holder.img_my);
//        holder.user_name.setText(memberData.getUser_name());

        if (memberData.getMain().equals("0")) {
            holder.img_main.setVisibility(View.INVISIBLE);
        } else {
            holder.img_main.setVisibility(View.VISIBLE);
        }


        if (memberData.getSid().equals("") || memberData.getSid().isEmpty()) {

            Log.e("abc", "사진 읍다");

//            Picasso.with(context).load(Statics.main_url+memberData.getPic1()).fit().centerCrop().placeholder(R.drawable.icon_noprofile).transform(new CircleTransform()).into(holder.img_my);
//            holder.user_name.setText(memberData.getUser_name());


            //        holder.btn_set_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

//            holder.btn_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("abc", "더보기 버튼");
//
//                    PopupMenu popupMenu = new PopupMenu(context, v);
//                    popupMenu.getMenu().add(Menu.NONE, 1, 1, "대표사진 설정");
//                    popupMenu.getMenu().add(Menu.NONE, 2, 2, "삭제");
//                    popupMenu.show();
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            int i = item.getItemId();
//                            if(i == 1) {
////                            new OneArticle.UpdateMainTask().execute();
//
//                                return true;
//                            } else if (i == 2) {
////                            new OneArticle.DelArticle().execute();
//
//                                return true;
//                            } else {
//                                return false;
//                            }
//                        }
//                    });
//                }
//            });
        }



        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String sid, String img_url, String main) {
        MemberData item = new MemberData();

        item.setSid(sid);
        item.setPic1(img_url);
        item.setMain(main);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }

    private void removeItem(int position) {
        listViewItemList.remove(position);

    }

    // 프로필 이미지로 업데이트
    public class SetProfileMainTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "set_profile_main")
                    .add("my_id", MainActivity.my_id)
                    .add("profile_id", params[0])
                    .build();
            Log.e("abc", "profile_id : " + params[0]);
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
//            View v = View.inflate();
//            View v = MyImageList.this;
//            Toast.makeText(v.getContext(), "선택한 프로필필을 대표사진으로 등록하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 글 삭제
    public class DelArticleTask extends AsyncTask<String, Void, Void> {
        String str_pos;

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

            str_pos = params[1];
            Log.e("abc", "positon : " + str_pos);
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
            int i_pos = Integer.parseInt(str_pos);

            removeItem(i_pos);
            MyImageList.myImagesAdapter.notifyDataSetChanged();
        }
    }
}