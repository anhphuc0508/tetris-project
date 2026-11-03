package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Random;

public class Particle {
    private float x, y;
    private float vx, vy;
    private float size;
    private int alpha = 255;
    private int color;
    private static final Random random = new Random();

    public Particle(float startX, float startY, int color, float cellSize) {
        this.x = startX;
        this.y = startY;
        this.color = color;

        this.size = cellSize / 3f;

        this.vx = (random.nextFloat() - 0.5f) * 14f;
        this.vy = (random.nextFloat() - 0.5f) * 14f - 3f;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.6f;
        alpha -= 10;
        if (alpha < 0) alpha = 0;
    }

    public boolean isDead() {
        return alpha <= 0;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (isDead()) return;

        paint.setColor(color);
        paint.setAlpha(alpha);

        canvas.drawRect(x, y, x + size, y + size, paint);
    }
}