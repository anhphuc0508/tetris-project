package com.example.myapplication;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer bgmPlayer;
    private int[] playlist = {R.raw.bgm, R.raw.a_beautiful_song, R.raw.gerudo_valley , R.raw.city_ruins};
    private java.util.List<Integer> musicBag = new java.util.ArrayList<>();
    private SoundPool soundPool;
    private int soundClear, soundDrop, soundRotate;
    private NextPieceView nextPieceView;
    private TetrisLogic gameLogic;
    private TetrisView gameView;
    private Handler handler;
    private TextView tvScore;
    private android.widget.FrameLayout imgPausedMenu;
    private android.widget.FrameLayout btnPause;
    private android.widget.ImageView btnResume;
    private android.widget.ImageView btnQuit;
    private float currentSfxVolume = 1.0f;

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
        setContentView(R.layout.activity_main);

        android.content.SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        float bgmVolume = prefs.getFloat("bgm_volume", 1.0f);
        final float[] sfxVolume = {prefs.getFloat("sfx_volume", 1.0f)};

        playNextSong();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundClear = soundPool.load(this, R.raw.clear, 1);
        soundDrop = soundPool.load(this, R.raw.drop, 1);
        soundRotate = soundPool.load(this, R.raw.rotate, 1);

        this.currentSfxVolume = sfxVolume[0];

        gameView = findViewById(R.id.gameView);
        tvScore = findViewById(R.id.tvScore);
        nextPieceView = findViewById(R.id.nextPieceView);
        
        imgPausedMenu = findViewById(R.id.imgPausedMenu);
        btnPause = findViewById(R.id.pause);
        btnResume = findViewById(R.id.btnResume);
        btnQuit = findViewById(R.id.btnQuit);
        
        android.widget.ImageView btnSound = findViewById(R.id.btnSound);
        android.widget.FrameLayout soundMenuLayer = findViewById(R.id.soundMenuLayer);
        android.widget.ImageView btnCloseSoundMenu = findViewById(R.id.btnCloseSoundMenu);
        android.widget.SeekBar seekBarBgm = findViewById(R.id.seekBarBgm);
        android.widget.SeekBar seekBarSfx = findViewById(R.id.seekBarSfx);
        
        android.widget.FrameLayout gameOverLayer = findViewById(R.id.gameOverLayer);
        android.widget.TextView tvFinalScore = findViewById(R.id.tvFinalScore);
        android.widget.ImageView btnReplay = findViewById(R.id.btnReplay);
        android.widget.ImageView btnQuitGameOver = findViewById(R.id.btnQuitGameOver);

        btnReplay.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        
                        gameOverLayer.clearAnimation();
                        gameOverLayer.setVisibility(android.view.View.GONE);
                        
                        gameLogic = new TetrisLogic();
                        
                        gameLogic.setOnLineClearListener(new TetrisLogic.OnLineClearListener() {
                            @Override
                            public void onLineCleared(int row, int col, int blockType) {
                                gameView.spawnExplosion(row, col, blockType);
                                playSound(soundClear);
                            }
                        });
                        
                        gameView.setGameLogic(gameLogic);
                        nextPieceView.setGameLogic(gameLogic);
                        updateScore();
                        gameView.invalidate();
                        nextPieceView.invalidate();
                    }
                }).start();
            }
        });

        btnQuitGameOver.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }).start();
                    }
                }).start();
            }
        });

        // Set giá trị thanh trượt khớp với âm lượng đã lưu
        seekBarBgm.setProgress((int) (bgmVolume * 100));
        seekBarSfx.setProgress((int) (sfxVolume[0] * 100));

        btnPause.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (!gameLogic.isGameOver() && !isPaused) {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    isPaused = true;
                                    imgPausedMenu.setVisibility(android.view.View.VISIBLE);
                                }
                            }).start();
                        }
                    }).start();
                }
            }
        });

        btnResume.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                isPaused = false;
                                imgPausedMenu.setVisibility(android.view.View.GONE);
                            }
                        }).start();
                    }
                }).start();
            }
        });

        btnQuit.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                finish(); // Tắt MainActivity để quay trở về MainMenuActivity
                            }
                        }).start();
                    }
                }).start();
            }
        });

        btnSound.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        soundMenuLayer.setVisibility(android.view.View.VISIBLE);
                    }
                }).start();
            }
        });

        btnCloseSoundMenu.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        soundMenuLayer.setVisibility(android.view.View.GONE);
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

        seekBarSfx.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                float newVolume = progress / 100f;
                currentSfxVolume = newVolume;
                prefs.edit().putFloat("sfx_volume", newVolume).apply();
            }
            @Override public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });

        gameLogic = new TetrisLogic();
        gameLogic.setOnLineClearListener(new TetrisLogic.OnLineClearListener() {
            @Override
            public void onLineCleared(int row, int col, int blockType) {
                gameView.spawnExplosion(row, col, blockType);
                playSound(soundClear);
            }
        });

        gameView.setGameLogic(gameLogic);
        nextPieceView.setGameLogic(gameLogic);

        startGameLoop();
    }

    private float startX, startY;
    private long startTime; // Thêm biến lưu thời gian bắt đầu chạm
    private boolean isDragging;
    private boolean hasHardDroppedInGesture = false;
    private final float DRAG_THRESHOLD_X = 50f;
    private final float DRAG_THRESHOLD_Y = 120f;
    private final long CLICK_DURATION_THRESHOLD = 200;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameLogic != null && (gameLogic.isGameOver() || isPaused)) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                startTime = System.currentTimeMillis();
                isDragging = false;
                hasHardDroppedInGesture = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();
                float deltaX = currentX - startX;
                float deltaY = currentY - startY;

                
                if (Math.abs(deltaX) > DRAG_THRESHOLD_X) {
                    isDragging = true;
                    if (deltaX > 0) {
                        gameLogic.moveRight();
                    } else {
                        gameLogic.moveLeft();
                    }
                    gameView.invalidate();
                    startX = currentX;
                    startY = currentY;
                }

                if (deltaY > DRAG_THRESHOLD_Y && !hasHardDroppedInGesture) {
                    isDragging = true;
                    hasHardDroppedInGesture = true;
                    gameLogic.hardDrop();
                    gameView.invalidate();
                    nextPieceView.invalidate();
                    updateScore();
                    playSound(soundDrop);
                    startY = currentY;
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long duration = System.currentTimeMillis() - startTime;
                float totalDeltaX = Math.abs(event.getX() - startX);
                float totalDeltaY = Math.abs(event.getY() - startY);
                
                if (!isDragging && !hasHardDroppedInGesture 
                        && duration < CLICK_DURATION_THRESHOLD 
                        && totalDeltaX < 15f && totalDeltaY < 15f) {
                    gameLogic.Rotate90Clockwise();
                    gameView.invalidate();
                    playSound(soundRotate);
                }
                hasHardDroppedInGesture = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void playSound(int soundId) {
        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, currentSfxVolume, currentSfxVolume, 0, 0, 1f);
        }
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }

    private void updateScore() {
        if (tvScore != null && gameLogic != null) {
            tvScore.setText(String.valueOf(gameLogic.getScore()));
        }
    }

    private boolean isPaused = false;


    private void playNextSong() {
        if (bgmPlayer != null) {
            try {
                bgmPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bgmPlayer = null;
        }

        if (musicBag.isEmpty()) {
            for (int songId : playlist) {
                musicBag.add(songId);
            }
            java.util.Collections.shuffle(musicBag);
        }
        
        int nextSong = musicBag.remove(0);
        bgmPlayer = MediaPlayer.create(this, nextSong);

        if (bgmPlayer != null) {
            float currentBgmVol = getSharedPreferences("GameSettings", MODE_PRIVATE).getFloat("bgm_volume", 1.0f);
            bgmPlayer.setVolume(currentBgmVol, currentBgmVol);
            bgmPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });
            bgmPlayer.start();
        }
    }

    private void startGameLoop() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameLogic.isGameOver()){
                    android.widget.FrameLayout gameOverLayer = findViewById(R.id.gameOverLayer);
                    android.widget.TextView tvFinalScore = findViewById(R.id.tvFinalScore);
                    
                    if (gameOverLayer.getVisibility() != android.view.View.VISIBLE) {
                        tvFinalScore.setText(String.valueOf(gameLogic.getScore()));
                        gameOverLayer.setVisibility(android.view.View.VISIBLE);
                        
                        // Bảng Game Over trượt từ trên trời rơi xuống và nhún một cái (Overshoot)
                        android.view.animation.Animation slideDownAnim = android.view.animation.AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
                        gameOverLayer.startAnimation(slideDownAnim);
                    }
                    handler.postDelayed(this, 500); // Vẫn lặp để chờ người dùng bấm nút
                    return;
                }
                if (!isPaused) {
                    gameLogic.moveDown();
                    gameView.invalidate();
                    nextPieceView.invalidate();
                    updateScore();
                }
                handler.postDelayed(this, 500);
            }
        }, 500);
    }
}