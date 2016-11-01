package com.angcyo.texturedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.TextureView;
import android.view.View;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    VideoView mVideoView;
    TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mTextureView = (TextureView) findViewById(R.id.texture_view);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String filePath = "/storage/emulated/0/BaiduNetdisk/demo.mp4";
        if (new File(filePath).exists()) {
            mVideoView.setVideoPath(filePath);
            mVideoView.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.pause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mVideoView.resume();
    }
}
