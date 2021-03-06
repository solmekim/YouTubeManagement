package com.solmekim.youtubemanage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.solmekim.youtubemanage.Util.InterfaceClass;
import com.solmekim.youtubemanage.VideoTab.VideoAddActivity;
import com.solmekim.youtubemanage.VideoTab.VideoTab;
import com.solmekim.youtubemanage.provider.YouTubeManageContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.ACTION_SEND;

public class YouTubeManageMain extends AppCompatActivity implements InterfaceClass.SendNewVideoTabInfoToActivity, InterfaceClass.SendDeleteVideoTabInfoToActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerFragmentAdapter viewPagerFragmentApater;
    private String tabTitle;

    private ArrayList<String> totalTabNameList;
    private ArrayList<String> videoNameList;

    private List<HashMap<String, ArrayList<VideoTab>>> totalVideoTabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (getIntent().getAction().equals(ACTION_SEND)
                && getIntent().getType().equals("text/plain")) {
            getIntent().setClass(this, VideoAddActivity.class);
            startActivity(getIntent());
        }
    }

    private void init() {

        //   ActionBar actionBar = getActionBar();
        //   actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        totalVideoTabList = new ArrayList<>();

        totalTabNameList = YouTubeManageContract.getTotalVideoTabNameList(this);
        videoNameList = YouTubeManageContract.getVideoTabNameList(this);

        getTotalVideoValue();

        viewPagerFragmentApater = new ViewPagerFragmentAdapter(getSupportFragmentManager(), this, totalTabNameList, videoNameList, totalVideoTabList, YouTubeManageMain.this);

        viewPager.setAdapter(viewPagerFragmentApater);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabTitle = tab.getText().toString();
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getTotalVideoValue() {

        for(int i=0; i< videoNameList.size(); i++) {
            HashMap<String, ArrayList<VideoTab>> videoTabList = new HashMap<>();
            ArrayList<VideoTab> videoTab = new ArrayList<>();
            videoTabList.put(videoNameList.get(i), videoTab);

            totalVideoTabList.add(videoTabList);
        }

        Cursor cursor = YouTubeManageContract.selectVideoTabValueAllColumns(this);
        if(cursor.getCount()!=0) {
            while(cursor.moveToNext()) {
                String VideoTabName, VideoTitle, VideoUploadTime, VideoUrl, VideoDescription;
                int VideoViewCount, VideoDuration;

                VideoTabName = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.VideoTab)));
                VideoTitle = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.VideoTitle)));
                VideoUploadTime = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.VideoUploadTime)));
                VideoUrl = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.VideoUrl)));
                VideoDescription = cursor.getString(cursor.getColumnIndex(getResources().getString(R.string.VideoDescription)));
                VideoViewCount = cursor.getInt(cursor.getColumnIndex(getResources().getString(R.string.VideoViewCount)));
                VideoDuration = cursor.getInt(cursor.getColumnIndex(getResources().getString(R.string.VideoDuration)));

                for (int i = 0; i < videoNameList.size(); i++) {
                    if (videoNameList.get(i).equals(VideoTabName)) {
                        VideoTab videoTab = new VideoTab(VideoTabName, VideoTitle, VideoViewCount, VideoDuration, VideoUploadTime, VideoUrl, VideoDescription);
                        totalVideoTabList.get(i).get(videoNameList.get(i)).add(videoTab);
                    }
                }
            }
        }

        cursor.close();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPagerFragmentApater.destory();
    }

    @Override
    public void sendNewVideoTabInfo(String videoType) {
        viewPagerFragmentApater.addVideoType(videoType);
    }

    @Override
    public void sendDeleteVideoTabInfo(String videoType) {
        viewPagerFragmentApater.deleteVideoType(videoType);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SEND.equals(intent.getAction())
                && intent.getType().equals("text/plain")) {
            intent.setClass(this, VideoAddActivity.class);
            startActivity(intent);
        }
    }
}