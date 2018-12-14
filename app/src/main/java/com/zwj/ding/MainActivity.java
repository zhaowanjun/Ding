package com.zwj.ding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zwj.ding.fragment.ActionCardFragment;
import com.zwj.ding.fragment.FeedCardFragment;
import com.zwj.ding.fragment.LinkMsgFragment;
import com.zwj.ding.fragment.MarkdownFragment;
import com.zwj.ding.fragment.TextMsgFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new TextMsgFragment());
        fragments.add(new LinkMsgFragment());
        fragments.add(new ActionCardFragment());
        fragments.add(new MarkdownFragment());
        fragments.add(new FeedCardFragment());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(myFragmentPagerAdapter);
    }

}
