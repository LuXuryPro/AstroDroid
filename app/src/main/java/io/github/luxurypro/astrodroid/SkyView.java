package io.github.luxurypro.astrodroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import io.github.luxurypro.astrodroid.astronomy.Moon;
import io.github.luxurypro.astrodroid.astronomy.Sun;


public class SkyView extends View {
    private final Rect rect;
    private Paint paint;
    private Moon moon;
    private Sun sun;
    private int progress;
    private double rotation = 0;

    public SkyView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.paint = new Paint();
        int x = getWidth() / 2;
        int y = getHeight() / 2;

        int rectW = 10;
        int rectH = 2 * Math.min(x / 2, y / 2) + 50;
        int left = x - (rectW / 2);
        int top = y - (rectH / 2);
        int right = x + (rectW / 2);
        int bottom = y + (rectH / 2);
        this.rect = new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.paint.setColor(Color.WHITE);
        canvas.drawPaint(this.paint);

        int x = getWidth();
        int y = getHeight();

        canvas.rotate((float) Math.toDegrees(-this.rotation), x / 2, y / 2);

        this.paint.setStyle(Paint.Style.FILL);


        paint.setTextSize(80);
        this.paint.setColor(Color.RED);
        canvas.drawText("N", (float) (getLeft() + (getRight() - getLeft()) * 0.47), getTop() + 100, paint);

        this.paint.setColor(Color.BLUE);
        canvas.drawText("S", (float) (getLeft() + (getRight() - getLeft()) * 0.47), getBottom() - 40, paint);

        int radius = Math.min(x / 2, y / 2) - 40;
        this.paint.setColor(Color.parseColor("#5194ff"));
        if (sun.getAltitude() < 0)
            this.paint.setColor(Color.BLACK);
        canvas.drawCircle(x / 2, y / 2, radius, paint);

        this.paint.setColor(Color.RED);


        canvas.drawRect(
                (float) (getLeft() + (getRight() - getLeft()) * 0.49),
                (float) (getTop() + (getBottom() - getTop()) * 0.1),
                (float) (getRight() - (getRight() - getLeft()) * 0.49),
                (float) (getBottom() - (getBottom() - getTop()) * 0.5), paint);

        this.paint.setColor(Color.BLUE);

        canvas.drawRect(
                (float) (getLeft() + (getRight() - getLeft()) * 0.49),
                (float) (getTop() + (getBottom() - getTop()) * 0.5),
                (float) (getRight() - (getRight() - getLeft()) * 0.49),
                (float) (getBottom() - (getBottom() - getTop()) * 0.1), paint);
        this.paint.setAlpha(256);
        this.paint.setColor(Color.YELLOW);
        if (sun.getAltitude() < 0)
            this.paint.setColor(Color.GRAY);
        double r = radius * Math.cos(sun.getAltitude());
        double alpha = sun.getAzimunt() + Math.PI / 2;
        int sx = (int) (r * Math.cos(alpha));
        int sy = (int) (r * Math.sin(alpha));
        canvas.drawCircle(x / 2 + sx, y / 2 + sy, (float) (radius * 0.1), paint);

        //moon
        this.paint.setColor(Color.LTGRAY);
        if (moon.getAltitude() < 0)
            this.paint.setColor(Color.DKGRAY);
        r = radius * Math.cos(moon.getAltitude());
        alpha = moon.getAzimunt() + Math.PI / 2;
        sx = (int) (r * Math.cos(alpha));
        sy = (int) (r * Math.sin(alpha));
        canvas.drawCircle(x / 2 + sx, y / 2 + sy, (float) (radius * 0.05), paint);

    }

    public void setData(Sun sun, Moon moon, double rotation) {
        this.moon = moon;
        this.sun = sun;
        this.rotation = rotation;
        this.invalidate();
    }
}
