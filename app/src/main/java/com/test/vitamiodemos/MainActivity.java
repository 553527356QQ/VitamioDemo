package com.test.vitamiodemos;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vitamio.demo.R;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends Activity implements io.vov.vitamio.MediaPlayer.OnInfoListener, io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener {

    private VideoView mVideoView;
    private TextView downloadRateView;
    private TextView loadRateView;
    private ProgressBar pb;
    private String path = "http://baobab.wdjcdn.com/145076769089714.mp4";
    private MediaController controller;
    private CustomMediaController customMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = getWindow();
        window.setFlags(flag, flag);
        Vitamio.isInitialized(this);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }


    /**
     * 初始化布局控件
     */
    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.buffer);
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);
        pb = (ProgressBar) findViewById(R.id.probar);
        controller = new MediaController(this);
        customMediaController = new CustomMediaController(this, mVideoView, this);
        customMediaController.setVideoName("测试视频播放");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.setMediaController(customMediaController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);//高画质

        controller.show(5000);

        mVideoView.requestFocus();

        mVideoView.setOnInfoListener(this);

        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(io.vov.vitamio.MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
            }
        });

    }


    /**
     * 监听器
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onInfo(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
        switch (what) {

            case MediaPlayer.MEDIA_INFO_BUFFERING_START:

                if (mVideoView.isPlaying()) {

                    mVideoView.pause();

                    pb.setVisibility(View.VISIBLE);

                    downloadRateView.setText("");

                    loadRateView.setText("");

                    downloadRateView.setVisibility(View.VISIBLE);

                    loadRateView.setVisibility(View.VISIBLE);

                }

                break;

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:

                mVideoView.start();

                pb.setVisibility(View.GONE);

                downloadRateView.setVisibility(View.GONE);

                loadRateView.setVisibility(View.GONE);

                break;

            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:

                downloadRateView.setText("" + extra + "kb/s" + "  ");

                break;

        }

        return true;
    }

    /**
     *  设置当前的进度
     * @param mp      the MediaPlayer the update pertains to
     * @param percent the percentage (0-100) of the buffer that has been filled thus
     */
    @Override
    public void onBufferingUpdate(io.vov.vitamio.MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕切换时，设置全屏

        if (mVideoView != null) {

            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);

        }
        super.onConfigurationChanged(newConfig);
    }
}

