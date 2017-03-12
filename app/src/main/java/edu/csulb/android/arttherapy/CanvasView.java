package edu.csulb.android.arttherapy;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

public class CanvasView extends View {

    private int lineColor = 0xFF006600;

    private Path drawPath;
    private Paint paint, canvasBackground;
    private Canvas canvas;
    private Bitmap bitmap;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        drawPath = new Path();
        paint = new Paint();
        paint.setColor(lineColor);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(22);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvasBackground = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, canvasBackground);
        canvas.drawPath(drawPath, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(drawPath, paint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void eraseEverything() {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
