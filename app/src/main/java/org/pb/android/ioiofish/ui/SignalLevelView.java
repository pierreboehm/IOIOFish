package org.pb.android.ioiofish.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;
import org.pb.android.ioiofish.R;

@EView
public class SignalLevelView extends View {

    private Context context;
    private Paint color;
    private boolean hasSignal = false;
    private float signalLevel = 0f;

    public SignalLevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @AfterViews
    public void initView() {
        setWillNotDraw(false);

        color = new Paint();
        color.setColor(context.getColor(R.color.blue_light));
        color.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawSignalLevel(canvas);
        super.onDraw(canvas);
    }

    @UiThread
    protected void updateUi() {
        if (hasSignal) {
            signalLevel -= 1f;

            if (signalLevel < 0f) {
                signalLevel = 0f;
                hasSignal = false;
            }
        }

        if (hasSignal) {
            invalidate();
        }
    }

    @UiThread
    public void setSignal() {
        if (hasSignal) {
            return;
        }

        signalLevel = 10f;
        hasSignal = true;
        invalidate();
    }

    private void drawSignalLevel(Canvas canvas) {
        float strokeWidth = canvas.getHeight() / 21f;

        color.setStrokeWidth(strokeWidth);

        int index = 10;

        for (float y = (strokeWidth + (strokeWidth / 2f)); y < (float) canvas.getHeight(); y += (strokeWidth * 2f)) {

            if ((int) signalLevel < index) {
                color.setColor(context.getColor(R.color.blue_light_transparent));
            } else {
                color.setColor(context.getColor(R.color.blue_light));
            }

            canvas.drawLine(strokeWidth, y, canvas.getWidth() - strokeWidth, y, color);
            index--;
        }

        color.setColor(context.getColor(R.color.blue_light));

        updateUi();
    }
}
