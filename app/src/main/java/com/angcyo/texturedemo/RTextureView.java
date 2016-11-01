package com.angcyo.texturedemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by angcyo on 2016-11-01.
 */

public class RTextureView extends TextureView implements TextureView.SurfaceTextureListener, Runnable {

    Paint mPaint;
    boolean isAvailable = false;
    boolean isExit = false;
    int fps = 0;

    long startTime = 0l;

    String fpsString = "0";

    Xfermode CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    Xfermode SRC = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    int width, height;

    float degrees = 0f;

    Drawable mDrawable, mDrawableRight, mDrawableLeft;

    ValueAnimator mValueAnimator;

    public RTextureView(Context context) {
        super(context);
        initView();
    }

    public RTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public static void log() {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        Log.i("angcyo", "(" + stackTrace[1].getFileName() + ":" + stackTrace[1].getLineNumber() + ") "
                + Thread.currentThread().getName() + " " + stackTrace[1].getMethodName() + " ");
    }

    public static void log(String msg) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        Log.i("angcyo", "(" + stackTrace[1].getFileName() + ":" + stackTrace[1].getLineNumber() + ") "
                + Thread.currentThread().getName() + " " + stackTrace[1].getMethodName() + " " + msg);
    }

    private static void setXY(Drawable drawable, int x, int y) {
        drawable.setBounds(x, y, x + drawable.getIntrinsicWidth(), y + drawable.getIntrinsicHeight());
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setTextSize(40);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        log(isHardwareAccelerated() + "");
        setSurfaceTextureListener(this);

        mDrawable = getResources().getDrawable(R.drawable.ic_anim_plane1_body);
        mDrawableRight = getResources().getDrawable(R.drawable.ic_anim_plane1_screw);
        mDrawableLeft = getResources().getDrawable(R.drawable.ic_anim_plane1_screw);

//        updatePath(width + mDrawable.getIntrinsicWidth() / 2, height - mDrawable.getIntrinsicHeight() / 2);

//        updatePath(width / 2, height / 2);
        new Thread(this).start();
    }

    private void updatePath(int cx, int cy) {
        final int width = mDrawable.getIntrinsicWidth();
        final int height = mDrawable.getIntrinsicHeight();
        final int left = cx - width / 2;
        final int top = cy - height / 2;
        setXY(mDrawable, left, top);

        int lLeft = cx - 190;
        int lTop = cy - 30;
        setXY(mDrawableLeft, lLeft, lTop);


        int rLeft = cx - 30;
        int rTop = cy + 35;
        setXY(mDrawableRight, rLeft, rTop);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        log(width + " " + height);

//        updatePath(width + mDrawable.getIntrinsicWidth() / 2, height - mDrawable.getIntrinsicHeight() / 2);
        updatePath(width / 2, height / 2);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        log();
        isAvailable = true;

        mValueAnimator = ObjectAnimator.ofInt(width + mDrawable.getIntrinsicWidth() / 2, -mDrawable.getIntrinsicWidth() / 2);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int value = (int) animation.getAnimatedValue();
                updatePath(value, (int) ((1 - value * 1f / width) * height));
            }
        });
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.setDuration(3000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        log();

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        log();
        isAvailable = false;
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//        log();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isExit = true;
    }

    @Override
    public void run() {
        while (!isExit) {
            if (isAvailable) {
                final Canvas canvas = lockCanvas();
                if (canvas == null) {
                    continue;
                }
//                canvas.drawColor(Color.RED);
                mPaint.setXfermode(CLEAR);
                canvas.drawPaint(mPaint);
                mPaint.setXfermode(SRC);

                canvas.drawText("FPS:" + fpsString, 100, 100, mPaint);

                mDrawable.draw(canvas);
                canvas.save();
                canvas.rotate(degrees, mDrawableRight.getBounds().centerX(), mDrawableRight.getBounds().centerY());
                mDrawableRight.draw(canvas);
                canvas.restore();

                canvas.save();
                canvas.rotate(degrees, mDrawableLeft.getBounds().centerX(), mDrawableLeft.getBounds().centerY());
                mDrawableLeft.draw(canvas);
                canvas.restore();

                unlockCanvasAndPost(canvas);

                long nowTime = System.currentTimeMillis();
                if (nowTime - startTime > 1000) {
                    final float f = fps * 1f / (nowTime - startTime);
                    fpsString = String.valueOf(f * 1000);
                    fps = 0;
                    startTime = nowTime;
                }

                fps++;
                degrees += 10;
                //log(nowTime + "");
            }
        }
    }
}
