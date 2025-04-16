package com.example.app2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private static final int REQUEST_PERMISSION = 1;
    private Button btnPlay, btnPause, btnStop;
    private ListView musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        musicList = findViewById(R.id.musicList);

        checkPermission();
        setupButtons();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, 
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            loadMusic();
        }
    }

    private void setupButtons() {
        btnPlay.setOnClickListener(v -> startService(new Intent(this, MusicService.class)));
        btnPause.setOnClickListener(v -> sendBroadcast(new Intent("PAUSE_MUSIC")));
        btnStop.setOnClickListener(v -> stopService(new Intent(this, MusicService.class)));
    }

    private void loadMusic() {
        File musicDir = new File("/storage/FE7C-19FA/Music");
        ArrayList<String> musicFiles = new ArrayList<>();

        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".mp3"));
            
            if (files != null) {
                for (File file : files) {
                    musicFiles.add(file.getAbsolutePath());
                }
            }
        }

        if (musicFiles.isEmpty()) {
            Toast.makeText(this, R.string.no_music_found, Toast.LENGTH_SHORT).show();
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            musicFiles
        );
        musicList.setAdapter(adapter);
        
        musicList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("music_path", musicFiles.get(position));
            startService(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0 
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusic();
        }
    }
}
