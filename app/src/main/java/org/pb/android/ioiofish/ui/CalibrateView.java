package org.pb.android.ioiofish.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.event.Events;

@EViewGroup(R.layout.view_calibrate)
public class CalibrateView extends LinearLayout {

    private static final int MIN = 0;
    private static final int MAX = 1500;

    @ViewById(R.id.etValue)
    EditText positionValue;

    public CalibrateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @TextChange(R.id.etValue)
    public void onTextChange() {
        EventBus.getDefault().post(new Events.ValueChangedEvent(positionValue.getText().toString()));
    }

    @Click(R.id.btnMaxLeft)
    public void onMaxLeftClick() {
        positionValue.setText(Integer.toString(MIN));
    }

    @Click(R.id.btnStepLeft)
    public void onStepLeftClick() {
        int value = Integer.parseInt(positionValue.getText().toString()) - 1;
        positionValue.setText(Integer.toString(value));
    }

    @Click(R.id.btnStepRight)
    public void onStepRightClick() {
        int value = Integer.parseInt(positionValue.getText().toString()) + 1;
        positionValue.setText(Integer.toString(value));
    }

    @Click(R.id.btnMaxRight)
    public void onMaxRightClick() {
        positionValue.setText(Integer.toString(MAX));
    }
}
