package com.pumkit.chin.chinchinso;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.pumkit.chin.widget.DateListSwipeAdapter;

public class TestActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_date);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_datelist);
        DateListSwipeAdapter dateListSwipeAdapter = new DateListSwipeAdapter(this);
        viewPager.setAdapter(dateListSwipeAdapter);
    }
}