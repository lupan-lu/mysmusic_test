package com.example.app2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.Toast;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver receiver;
    private String currentMusicPath;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            // Auto-play next song when current finishes
            // Could implement next song logic here
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            if (this != null) {
                Toast.makeText(this, "播放错误", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter("PAUSE_MUSIC"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String musicPath = intent.getStringExtra("music_path");
            if (musicPath != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(musicPath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    if (this != null) {
                        Toast.makeText(this, "无法播放音乐: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
