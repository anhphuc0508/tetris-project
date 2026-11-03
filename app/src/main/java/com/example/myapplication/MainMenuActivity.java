package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;

public class MainMenuActivity extends AppCompatActivity {
    private MediaPlayer bgmPlayer;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        android.view.View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        android.content.SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        float bgmVolume = prefs.getFloat("bgm_volume", 1.0f);

        bgmPlayer = MediaPlayer.create(this, R.raw.main_menu);
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(bgmVolume, bgmVolume);
            bgmPlayer.setLooping(true);
            bgmPlayer.start();
        }

        ImageView imgLogo = findViewById(R.id.imgLogo);
        FrameLayout btnPlay = findViewById(R.id.btnPlay);
        FrameLayout btnTutorial = findViewById(R.id.btnTutorial);
        FrameLayout tutorialLayer = findViewById(R.id.tutorialLayer);
        ImageView btnCloseTutorial = findViewById(R.id.btnCloseTutorial);

        Animation pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imgLogo.startAnimation(pulseAnim);

        Animation floatUpAnim = AnimationUtils.loadAnimation(this, R.anim.float_up);
        btnPlay.startAnimation(floatUpAnim);
        
        Animation floatUpAnim2 = AnimationUtils.loadAnimation(this, R.anim.float_up);
        floatUpAnim2.setStartOffset(200);
        btnTutorial.startAnimation(floatUpAnim2);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).start();
                    }
                }).start();
            }
        });

        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        tutorialLayer.setVisibility(View.VISIBLE);
                    }
                }).start();
            }
        });

        btnCloseTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        tutorialLayer.setVisibility(View.GONE);
                    }
                }).start();
            }
        });
        float sfxVolume = prefs.getFloat("sfx_volume", 1.0f);

        ImageView btnSound = findViewById(R.id.btnSound);
        FrameLayout soundMenuLayer = findViewById(R.id.soundMenuLayer);
        ImageView btnCloseSoundMenu = findViewById(R.id.btnCloseSoundMenu);
        android.widget.SeekBar seekBarBgm = findViewById(R.id.seekBarBgm);


        seekBarBgm.setProgress((int) (bgmVolume * 100));


        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        soundMenuLayer.setVisibility(View.VISIBLE);
                    }
                }).start();
            }
        });

        btnCloseSoundMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        soundMenuLayer.setVisibility(View.GONE);
                    }
                }).start();
            }
        });

        seekBarBgm.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                float newVolume = progress / 100f;
                if (bgmPlayer != null) {
                    bgmPlayer.setVolume(newVolume, newVolume);
                }
                prefs.edit().putFloat("bgm_volume", newVolume).apply();
            }
            @Override public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgmPlayer != null) {
            bgmPlayer.start();
            android.content.SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
            float bgmVolume = prefs.getFloat("bgm_volume", 1.0f);
            bgmPlayer.setVolume(bgmVolume, bgmVolume);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }
}