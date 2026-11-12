package com.velthon.bibletv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import java.util.Random;

/**
 * Temporary weather-on-glass overlay between verses.
 * The actual Bible display, artwork, theme and controls remain untouched beneath it.
 */
public final class RainGlassTransitionView extends View {
    public static final long DURATION_MS = 17000L;
    private final Bitmap previous;
    private final Runnable finished;
    private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint dropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] x = new float[220];
    private final float[] y = new float[220];
    private final float[] speed = new float[220];
    private final float[] radius = new float[220];
    private final float[] length = new float[220];
    private long started;
    private boolean completed = false;

    public RainGlassTransitionView(Context context, Bitmap priorScreen, Runnable onFinished) {
        super(context);
        previous = priorScreen;
        finished = onFinished;
        Random rng = new Random(260411);
        for (int i = 0; i < x.length; i++) {
            x[i] = rng.nextFloat();
            y[i] = rng.nextFloat();
            speed[i] = 0.012f + rng.nextFloat() * 0.052f;
            radius[i] = 0.6f + rng.nextFloat() * 2.9f;
            length[i] = 4f + rng.nextFloat() * 25f;
        }
        setWillNotDraw(false);
        setClickable(false);
        started = SystemClock.uptimeMillis();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (completed || getWidth() == 0 || getHeight() == 0) return;
        long elapsed = SystemClock.uptimeMillis() - started;
        float t = Math.min(1f, elapsed / (float) DURATION_MS);
        int w = getWidth(), h = getHeight();

        // The previous full screen stays beneath the glass initially, then reveals
        // the already-rendered next verse. No scene assets or theme colors are changed.
        float oldOpacity = t < .22f ? 1f : t < .73f ? 1f - (t - .22f) / .51f : 0f;
        if (previous != null && !previous.isRecycled() && oldOpacity > 0f) {
            imagePaint.setAlpha(Math.round(255f * oldOpacity));
            canvas.drawBitmap(previous, null, new android.graphics.Rect(0, 0, w, h), imagePaint);
        }

        // Water builds, washes the virtual window, then gradually drains away.
        float wet = t < .18f ? t / .18f : t < .56f ? 1f : Math.max(0f, (1f - t) / .44f);
        if (wet > 0f) {
            dropPaint.setShader(null);
            dropPaint.setColor(Color.argb(Math.round(48 * wet), 220, 235, 245));
            canvas.drawRect(0, 0, w, h, dropPaint);
            for (int i = 0; i < x.length; i++) {
                float px = x[i] * w;
                // Continuous, independent top-to-bottom droplets with staggered starts.
                float progress = (y[i] + elapsed * speed[i] / 1000f) % 1.18f;
                float py = progress * h - .09f * h;
                float r = radius[i] * getResources().getDisplayMetrics().density;
                int opacity = Math.round((45f + radius[i] * 25f) * wet);
                dropPaint.setColor(Color.argb(Math.min(180, opacity), 247, 251, 255));
                dropPaint.setStrokeWidth(Math.max(1.1f, r * .55f));
                dropPaint.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawLine(px, py - length[i] * (1 + speed[i] * 9), px, py, dropPaint);
                canvas.drawCircle(px, py, r, dropPaint);
                dropPaint.setColor(Color.argb(Math.round(35f * wet), 20, 38, 55));
                canvas.drawCircle(px + r * .3f, py + r * .2f, r * .5f, dropPaint);
            }
        }

        // Light slowly returns as the glass clears; a restrained transparent highlight,
        // not a replacement background or global color filter.
        if (t > .63f) {
            float sunlight = Math.min(1f, (t - .63f) / .37f) * (1f - t);
            dropPaint.setShader(new RadialGradient(w * .87f, h * .19f, Math.max(w,h) * .85f,
                new int[]{Color.argb(Math.round(75f * sunlight),255,240,194), Color.TRANSPARENT},
                null, Shader.TileMode.CLAMP));
            canvas.drawRect(0,0,w,h,dropPaint);
            dropPaint.setShader(null);
        }

        if (t >= 1f) { finish(); } else { postInvalidateDelayed(33); }
    }

    public void finish() {
        if (completed) return;
        completed = true;
        if (finished != null) finished.run();
    }
}
