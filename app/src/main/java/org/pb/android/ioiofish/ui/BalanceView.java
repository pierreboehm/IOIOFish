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

    private static final String TAG = BalanceView.class.getSimpleName();

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
        color.setStyle(Paint.Style.STROKE);
        color.setStrokeWidth(1f);
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

        // rotate away, values become positive (top level - right)
        // rotate towards, values become negative (bottom level - left)

        if (y > 0f) {
            for (float yPos = yCenter - 4f; yPos > yCenter - y; yPos -= 8f) {
                canvas.drawLine(0f, yPos, canvas.getWidth(), yPos, color);
            }
        } else if (y < 0f) {
            for (float yPos = yCenter + 4f; yPos < yCenter - y; yPos += 8f) {
                canvas.drawLine(0f, yPos, canvas.getWidth(), yPos, color);
            }
        }

    }
}
