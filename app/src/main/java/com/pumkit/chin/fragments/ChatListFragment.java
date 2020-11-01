package com.pumkit.chin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pumkit.chin.chinchinso.ChatActivity;
import com.pumkit.chin.chinchinso.MainActivity;
import com.pumkit.chin.chinchinso.R;
import com.pumkit.chin.chinchinso.Statics;
import com.pumkit.chin.vo.UserData;
import com.pumkit.chin.vo.ChatData;
import com.pumkit.chin.widget.ChatListAdapter;
import com.pumkit.chin.vo.ChatListData;
import com.pumkit.chin.widget.OkHttpClientSingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.pumkit.chin.chinchinso.MainActivity.myFireId;

public class ChatListFragment extends Fragment {

    private DatabaseReference mDatabase;
    private OkHttpClient httpClient;

    private String sid, user_name, password, phone, email, gender, age, token, id_facebook, join_time, login_time, leave_time, leave_yn, visit_count, pic1 = null;

//    private String partner_FireId;
    private String match_FireId, blind_FireId;

    private ListView listView;
    public ChatListAdapter chatListAdapter;

    public ChatListData chatListData;
    private ChatData chatData;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_chatlist, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initControls();
    }

    private void initControls() {
        listView = (ListView) getView().findViewById(R.id.listView);
        chatListAdapter = new ChatListAdapter();
        listView.setAdapter(chatListAdapter);
        //안 씀 xxxxxxxxxxxxxxxx
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                chatListData = (ChatListData) parent.getItemAtPosition(position);

                if (chatListData.getMy_id().equals(myFireId)) {
                    Log.e("abc","Case 111");
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("myFireId", chatListData.getMy_id());
                    intent.putExtra("matchFireId", chatListData.getMatch_id());
                    intent.putExtra("yourFireId", chatListData.getYourId());
                    intent.putExtra("yourUserName", chatListData.getYourName());
                    intent.putExtra("pic1", chatListData.getPic1());
                    startActivity(intent);
                } else {
                    Log.e("abc","Case 222");//xxxxxxxxx
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("myFireId", chatListData.getYourId());
                    intent.putExtra("matchFireId", chatListData.getMatch_id());
                    intent.putExtra("yourFireId", chatListData.getMy_id());
                    intent.putExtra("yourUserName", chatListData.getYourName());
                    intent.putExtra("pic1", chatListData.getPic1());
                    startActivity(intent);
                }

            }
        });


        // 1. 나의 메시지를 찾아보자
        mDatabase.child("user-match-messages").child(myFireId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                match_FireId = dataSnapshot.getKey();
                Log.e("abc", "match_FireId = " + match_FireId);
                mDatabase.child("user-match-messages").child(myFireId).child(match_FireId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        blind_FireId = dataSnapshot.getKey();
                        Log.e("abc", "blind_FireId = " + blind_FireId);
                        mDatabase.child("user-match-messages").child(myFireId).child(match_FireId).child(blind_FireId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                Map<String, Object> map = (HashMap<String,Object>) dataSnapshot2.getValue();

                                for ( String id_message : map.keySet() ) {

                                    // 2. 메세지의 내용을 찾아보자
                                    Log.e("abc", "id_message = " + id_message);
                                    mDatabase.child("messages").child(id_message).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot2) {
                                            chatData = dataSnapshot2.getValue(ChatData.class);
                                            Log.e("abc", "myFireId = " + myFireId);
                                            Log.e("abc", "chatData.getFromId() = " + chatData.getFromId());
                                            if (myFireId.equals(chatData.getFromId())) {
                                                blind_FireId = chatData.getToId();
                                            } else {
                                                blind_FireId = chatData.getFromId();
                                            }

                                            chatListAdapter.addItem(myFireId, match_FireId, blind_FireId, "홍길동", chatData.getText(), chatData.getTimestampLong());



                                            mDatabase.child("users").child(blind_FireId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot3) {
//                                    partner_id = (String) dataSnapshot.getValue();
                                                    UserData userVo = dataSnapshot3.getValue(UserData.class);

//                                    new AccountTask().execute(userVo.getmy_id(), dataSnapshot3.getKey());

//                                    Log.e("abc", "userVo.getUsername() = " + userVo.getUsername() + ", dataSnapshot3.getKey() = " +dataSnapshot3.getKey());
                                                    chatListAdapter.changeUserName(dataSnapshot3.getKey(), userVo.getUsername(), userVo.getImg_url());
                                                    listView.setAdapter(chatListAdapter);

                                                    chatListAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
//                            new AccountTask().execute(partner_id);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageView btn_back = (ImageView) getView().findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("abc", "xxxxxxxxxx");
                MainActivity.drawerLayout.closeDrawer(GravityCompat.END);
            }
        });
    }



    private class AccountTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String[] doInBackground(String... partnerId) {
            Log.e("abc", "chatList1 partnerId[0] = " + partnerId[0] + "partnerId[1] = " + partnerId[1]);
            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("my_id", partnerId[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    user_name = obj.getString("user_name");
                    pic1 = obj.getString("pic1");
                    token = obj.getString("token");
                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            if (pic1 != null && !pic1.isEmpty()) {
                pic1 = Statics.main_url + pic1;
            } else {
                Log.e("abc", "NULL");
                pic1 = "";
            }

            String[] array_user= {partnerId[1], user_name, pic1};

            return array_user;
        }

        @Override
        protected void onPostExecute(String[] array_user) {
//            adapter.notifyDataSetChanged();

//            chatListAdapter.addItem(myFireId, partner_FireId, array_user[1], chatData.getText(), chatData.getTimestampLong());

            Log.e("abc", "on Post array_user[0] = " + array_user[0] + ", array_user[1] = " + array_user[1]+ ", pic1 = " + pic1);

            chatListAdapter.changeUserName(array_user[0], array_user[1], pic1);
            listView.setAdapter(chatListAdapter);

            chatListAdapter.notifyDataSetChanged();
        }


    }
}