package com.example.mytest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCircle extends View {

    private static final int DEFAULT_PROGRESS_WIDTH = 5;
    private static final int DEFAULT_PROGRESS_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_PROGRESS_COLOR = Color.WHITE;

    private int mProgressWidth;
    private int mProgressBackgroundColor;
    private int mProgressColor;
    private RectF mProgressArcRect;
    private Paint mPaintProgressBackground;
    private Paint mPaintProgress;
    private TextPaint mPaintTextProgress;
    private Paint mPaintPointerProgress;

    private float mProgress;

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProgressCircle,
                0, 0);

        try {
            mProgressWidth = a.getDimensionPixelSize(R.styleable.ProgressCircle_progressStrokeWidth, DEFAULT_PROGRESS_WIDTH);
            mProgressBackgroundColor = a.getColor(R.styleable.ProgressCircle_progressBackgroundColor, DEFAULT_PROGRESS_BACKGROUND_COLOR);
            mProgressColor = a.getColor(R.styleable.ProgressCircle_progressAccentColor, DEFAULT_PROGRESS_COLOR);
        } finally {
            a.recycle();
        }

        mPaintProgressBackground = new Paint() {{
            this.setColor(mProgressBackgroundColor);
            this.setStrokeWidth(mProgressWidth);
            this.setStyle(Style.STROKE);
        }};

        mPaintProgress = new Paint() {{
            this.setColor(mProgressColor);
            this.setStrokeWidth(mProgressWidth);
            this.setStyle(Style.STROKE);
        }};

        mPaintTextProgress = new TextPaint() {{
            this.setStyle(Style.FILL);
            this.setTextSize(20);
            this.setColor(Color.BLACK);
        }};

        mPaintPointerProgress = new Paint() {{
            this.setColor(mProgressColor);
            this.setStrokeWidth(mProgressWidth);
            this.setStyle(Style.STROKE);
        }};

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int mWidth = w;
        int mHeight = h;
        int circleDiameter = 200;

        // markaz circle vasate view & ghotr circle 200
        mProgressArcRect = new RectF(mWidth / 2 - circleDiameter, mHeight / 2 - circleDiameter, mWidth / 2 + circleDiameter, mHeight / 2 + circleDiameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background circle
        drawCircle(canvas, mPaintProgressBackground);

        float startAngle = -90.0f + 360.0f * 0.0f;
        float currentAngle = 360.0f * mProgress;

        // draw progress circle
        drawCircle(canvas, startAngle, currentAngle, mPaintProgress);

        // draw circle on pointer
        drawCircleOnPointer(startAngle, currentAngle, mProgressArcRect, canvas);

        // draw text on pointer
        drawText("Hello \n How are you ? ", startAngle, currentAngle, mProgressArcRect, canvas);

    }

    protected void drawCircle(Canvas canvas, Paint paint) {
        float startAngle = -90.0f + 360.0f * 0.0f;
        float currentAngle = 360.0f * 1.0f;

        drawCircle(canvas, startAngle, currentAngle, paint);
    }

    protected void drawCircle(Canvas canvas, float startAngle, float currentAngle, Paint paint) {
        canvas.drawArc(mProgressArcRect, startAngle, currentAngle, false, paint);
    }

    private void drawText(String text, float startAngle, float currentAngle, RectF mProgressArcRect, Canvas canvas) {
        float[] pos = prepareTextLoc(getPointerLoc(startAngle, currentAngle, mProgressArcRect), getTextBounds(mPaintTextProgress, text));


      /*  for (String line: text.split("\n")) {
            canvas.drawText(line, pos[0], pos[1], mPaintTextProgress);
            pos[1] += mPaintTextProgress.descent() - mPaintTextProgress.ascent();
        }*/

        canvas.drawText(text, pos[0], pos[1], mPaintTextProgress);



    }

    private void drawCircleOnPointer(float startAngle, float currentAngle, RectF mProgressArcRect, Canvas canvas) {
        float[] pos = getPointerLoc(startAngle, currentAngle, mProgressArcRect);
        canvas.drawCircle(pos[0], pos[1], 15f, mPaintPointerProgress);
    }

    private float[] getTextBounds(Paint mTextPaint, String mText) {

        // Now lets calculate the size of the text
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), textBounds);
        float mTextWidth = textBounds.width();
        //float mTextWidth = mTextPaint.measureText(mText); // Use measureText to calculate width
        float mTextHeight = textBounds.height();

        return new float[]{mTextWidth, mTextHeight};
    }

    float[] oldPos = {0, 0};

    private float[] prepareTextLoc(float[] pos, float[] textBounds) {

        float[] newPos = pos;
        if (oldPos[0] == 0 && oldPos[1] == 0) {
            oldPos = newPos;
        }
        float currentX = newPos[0];
        float currentY = newPos[1];
        float lastX = oldPos[0];
        float lastY = oldPos[1];

        float outX = 0;
        float outY = 0;

        if (currentX - lastX >= 0 && currentY - lastY >= 0) { // area1
            outX = currentX + textBounds[1] + 10;
            outY = currentY - textBounds[1] + 10;
        } else if (currentX - lastX <= 0 && currentY - lastY >= 0) { // area2
            outX = currentX + textBounds[1] + 10;
            outY = currentY + textBounds[1] + 10;
        } else if (currentX - lastX <= 0 && currentY - lastY <= 0) { // area3
            outX = currentX - (textBounds[1] + textBounds[0]);
            outY = currentY + (textBounds[1] + textBounds[1]);

        } else if (currentX - lastX >= 0 && currentY - lastY <= 0) { // area4
            outX = currentX - (textBounds[1] + textBounds[0]);
            outY = currentY - (textBounds[1] + textBounds[1]);

        }
        oldPos = pos;
        return new float[]{outX, outY};
    }

    // get last pointer location x , y
    private float[] getPointerLoc(float startAngle, float currentAngle, RectF bgRect) {
        // since currentAngle is a "sweep" angle, the
        // final angle should be current + start
        float thetaD = startAngle + currentAngle;
        if (thetaD > 360F) {
            thetaD -= 360F;
        }
        // convert degrees to radians
        float theta = (float) Math.toRadians(thetaD);

        // polar to Cartesian coordinates
        float x = (float) (Math.cos(theta) * bgRect.width() / 2) + bgRect.centerX();
        float y = (float) (Math.sin(theta) * bgRect.height() / 2) + bgRect.centerY();

        return new float[]{x, y};
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

}