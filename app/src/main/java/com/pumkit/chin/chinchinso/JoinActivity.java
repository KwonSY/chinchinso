package com.pumkit.chin.chinchinso;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pumkit.chin.fragments.JoinFragment1;
import com.pumkit.chin.fragments.JoinFragment2;
import com.pumkit.chin.widget.SessionManager;

public class JoinActivity extends AppCompatActivity {
//extends ActionBarActivity
    public static ViewPager mViewPager;
    public static SessionManager session;

    private VideoView videoView;

    public static String psw, phone, user_name, age;
    public static String email = null;
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        session = new SessionManager(getApplicationContext());

        token = FirebaseInstanceId.getInstance().getToken();

        //튜토리얼 영상 - Tutorial Video
        videoView = (VideoView) findViewById(R.id.tuto_join);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        videoView.setMediaController(mc);

        videoView.setMediaController(mc);
        videoView.setVideoURI(Uri.parse(Statics.main_url + "mp4_process.mp4"));
        //https://www.dropbox.com/s/s235l4clfhdyvci/mp4_process.mp4?dl=0
        videoView.requestFocus();
        videoView.setMediaController(null);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
/** set the adapter for ViewPager */
        mViewPager.setAdapter(new SamplePagerAdapter(
                getSupportFragmentManager()));
    }

    /** Defining a FragmentPagerAdapter class for controlling the fragments to be shown when user swipes on the screen. */
    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            if (position == 0) {
                return new JoinFragment1();
            } else
                return new JoinFragment2();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


}
