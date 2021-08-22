package org.pb.android.ioiofish.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;

@EView
public class BalanceView extends View {

    private Paint color;
    private float currentValue;

    public BalanceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    public void initView() {
        setWillNotDraw(false);

        color = new Paint();
        color.setColor(Color.YELLOW);
        color.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBalance(canvas);
        super.onDraw(canvas);
    }

    @UiThread
    public void update(float value) {
        currentValue = value;
        invalidate();
    }

    private void drawBalance(Canvas canvas) {
        // values: -180 < 0 < +180
        // 0 represents the y-center
        float yCenter = (float) canvas.getHeight() / 2f;
        float y = (yCenter * currentValue) / 90f;

        if (y < 0f) {
            canvas.drawRect(0f, yCenter, canvas.getWidth(), yCenter + y, color);
        } else {
            canvas.drawRect(0f, yCenter + y, canvas.getWidth(), yCenter, color);
        }
    }
}
