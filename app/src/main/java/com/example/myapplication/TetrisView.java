package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TetrisView extends View {
    private Paint particlePaint = new Paint();
    private List<Particle> particles = new ArrayList<>();
    private Paint paintEmpty;
    private Paint paintBrighten;
    private Paint paintGrid;
    private TetrisLogic gameLogic;

    private Bitmap[] blockImages;
    private Bitmap imgGrid;
    private final Rect dstRect = new Rect();

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBrighten = new Paint();
        paintEmpty = new Paint();
        paintEmpty.setColor(Color.DKGRAY);
        paintEmpty.setStyle(Paint.Style.FILL);
        paintBrighten.setColorFilter(new LightingColorFilter(0xFFFFFF, 0x444444));
        paintGrid = new Paint();
        paintGrid.setColor(Color.BLACK);
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setStrokeWidth(2);

        imgGrid = BitmapFactory.decodeResource(getResources(), R.drawable.grid);

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
    public void spawnExplosion(int r, int c, int blockType) {
        int cellW = getWidth() / TetrisLogic.COLS;
        int cellH = getHeight() / TetrisLogic.ROWS;
        int cellSize = Math.min(cellW, cellH);

        int offsetX = (getWidth() - (cellSize * TetrisLogic.COLS)) / 2;
        int offsetY = (getHeight() - (cellSize * TetrisLogic.ROWS)) / 2;

        float startX = offsetX + c * cellSize + cellSize / 2f;
        float startY = offsetY + r * cellSize + cellSize / 2f;

        int color = Color.YELLOW;
        if (blockType == 1) color = Color.MAGENTA;      // T
        else if (blockType == 2) color = Color.YELLOW;  // O
        else if (blockType == 3) color = Color.CYAN;    // I
        else if (blockType == 4) color = Color.RED;     // Z
        else if (blockType == 5) color = Color.GREEN;   // S
        else if (blockType == 6) color = Color.BLUE;    // J
        else if (blockType == 7) color = Color.rgb(255, 105, 180); // L

        for (int i = 0; i < 8; i++) {
            particles.add(new Particle(startX, startY, color, cellSize));
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (specWidth <= 0 || specHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int cellW = specWidth / TetrisLogic.COLS;
        int cellH = specHeight / TetrisLogic.ROWS;
        int cellSize = Math.min(cellW, cellH);

        int measuredWidth = cellSize * TetrisLogic.COLS;
        int measuredHeight = cellSize * TetrisLogic.ROWS;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameLogic == null) return;

        int cellW = getWidth() / TetrisLogic.COLS;
        int cellH = getHeight() / TetrisLogic.ROWS;
        int cellSize = Math.min(cellW, cellH);

        if (cellSize <= 0) return;

        int offsetX = (getWidth() - (cellSize * TetrisLogic.COLS)) / 2;
        int offsetY = (getHeight() - (cellSize * TetrisLogic.ROWS)) / 2;

        int[][] board = gameLogic.getBoard();
        int[][] piece = gameLogic.getCurrentPiece();
        int px = gameLogic.getCurrentX();
        int py = gameLogic.getCurrentY();

        for (int r = 0; r < TetrisLogic.ROWS; r++) {
            for (int c = 0; c < TetrisLogic.COLS; c++) {
                int cellValue = board[r][c];

                if (!gameLogic.isGameOver()) {
                    if (r >= py && r < py + piece.length && c >= px && c < px + piece[0].length) {
                        if (piece[r - py][c - px] != 0) {
                            cellValue = piece[r - py][c - px];
                        }
                    }
                }

                int left = offsetX + (c * cellSize);
                int top = offsetY + (r * cellSize);
                int right = left + cellSize;
                int bottom = top + cellSize;

                dstRect.set(left, top, right, bottom);

                if (cellValue >= 1 && cellValue <= 7 && blockImages[cellValue] != null) {
                    canvas.drawBitmap(blockImages[cellValue], null, dstRect, null);
                } else if (imgGrid != null) {
                    canvas.drawBitmap(imgGrid, null, dstRect, paintBrighten);
                }
            }
        }
        if (!particles.isEmpty()) {
            java.util.Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                p.update();
                if (p.isDead()) {
                    iterator.remove();
                } else {
                    p.draw(canvas, particlePaint);
                }
            }
            invalidate();
        }

    }
}