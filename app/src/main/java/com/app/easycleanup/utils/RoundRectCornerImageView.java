package com.app.easycleanup.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;

import androidx.appcompat.widget.AppCompatImageView;

public class RoundRectCornerImageView extends AppCompatImageView {

    public float radius = 35.0f;
    private Path path;
    private RectF rect;

    public RoundRectCornerImageView(Context context) {
        super(context);
        init();
    }


    public RoundRectCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        rect = new RectF(0, 0, this.getWidth(), this.getHeight());

        String dimention = getScreenDimention();
        String[] sizes = dimention.split("-");
        int width = Integer.parseInt(sizes[0]);
        int height = Integer.parseInt(sizes[1]);

        if(width == 480 && height == 782){
            radius = 20.0f;
        }
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    public String getScreenDimention() {
        Display display = getDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        String str = width +"-"+height;
        return str;
    }
}