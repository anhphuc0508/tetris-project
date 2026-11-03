package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class NextPieceView extends View {
    private TetrisLogic gameLogic;
    private Bitmap[] blockImages;
    private final Rect dstRect = new Rect();

    public NextPieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        blockImages = new Bitmap[8];
        blockImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.purple_block); // T
        blockImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_block); // O
        blockImages[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cyan_block);   // I
        blockImages[4] = BitmapFactory.decodeResource(getResources(), R.drawable.red_block);    // Z
        blockImages[5] = BitmapFactory.decodeResource(getResources(), R.drawable.green_block);  // S
        blockImages[6] = BitmapFactory.decodeResource(getResources(), R.drawable.blue_block);   // J
        blockImages[7] = BitmapFactory.decodeResource(getResources(), R.drawable.pink_block);   // L
    }

    public void setGameLogic(TetrisLogic logic) {
        this.gameLogic = logic;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameLogic == null || gameLogic.getNextPiece() == null) return;

        int[][] piece = gameLogic.getNextPiece();
        int n = piece.length;

        int cellSize = Math.min(getWidth(), getHeight()) / 4;
        if (cellSize <= 0) return;

        int offsetX = (getWidth() - (n * cellSize)) / 2;
        int offsetY = (getHeight() - (n * cellSize)) / 2;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int val = piece[r][c];
                if (val >= 1 && val <= 7 && blockImages[val] != null) {
                    int left = offsetX + c * cellSize;
                    int top = offsetY + r * cellSize;
                    dstRect.set(left, top, left + cellSize, top + cellSize);

                    canvas.drawBitmap(blockImages[val], null, dstRect, null);
                }
            }
        }
    }
}