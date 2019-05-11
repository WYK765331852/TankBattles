package com.tju.tank.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LifeView extends View {
    public int life;
    private int x;
    private int y;
    private int width = 600;
    private int height = 60;
    private int color;

    public LifeView(Context context) {
        this(context, null);
    }

    public LifeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LifeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void getSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void getlife(int life) {
        this.life = life;
    }

    public void getColor(int color) {
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getLeft();
        int y = getTop();

        Paint paintB = new Paint();
        paintB.setColor(Color.WHITE);
        paintB.setStyle(Paint.Style.STROKE);
        paintB.setStrokeWidth(3);
        canvas.drawRect(x, y, x + width, y + height, paintB);
        Paint paintL = new Paint();
        paintL.setColor(color);
        canvas.drawRect(x + 1, y + 1, x + width * (float) life / 100, y + height - 1, paintL);


    }

}