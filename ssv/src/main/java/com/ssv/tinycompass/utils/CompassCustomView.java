package com.ssv.tinycompass.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassCustomView extends ImageView {
    //region constants
    private static final int DEGREES_180 = 180;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final float BIG_RADIUS_COEFFICIENT = 0.645f;
    private static final float RED_DOT_RADIUS_COEFFICIENT = 0.02f;
    //endregion

    //region fields
    private Paint paint;
    private float position = 0;
    //endregion

    //region constructor
    public CompassCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }
    //endregion

    //region overrides
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawRedDot(canvas);

        super.onDraw(canvas);
    }
    //endregion

    //region methods
    //region public methods
    public void updateData(float position) {
        this.position = position;

        if (this.position != position) {
            invalidate();
        }
    }
    //endregion

    //region private methods
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(ONE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.RED);
    }

    private void drawRedDot(Canvas canvas) {
        int xPoint = getMeasuredWidth() / TWO;
        int yPoint = getMeasuredHeight() / TWO;

        float compassRoseRadius = Math.max(xPoint, yPoint) * BIG_RADIUS_COEFFICIENT;
        float redDotRadius = Math.max(xPoint, yPoint) * RED_DOT_RADIUS_COEFFICIENT;

        if (canvas != null) {
            canvas.drawCircle(
                    (float) (xPoint + compassRoseRadius * Math.sin((double) (-position) / DEGREES_180 * Math.PI)),
                    (float) (yPoint - compassRoseRadius * Math.cos((double) (-position) / DEGREES_180 * Math.PI)),
                    redDotRadius, paint);
        }
    }
    //endregion
    //endregion
}
