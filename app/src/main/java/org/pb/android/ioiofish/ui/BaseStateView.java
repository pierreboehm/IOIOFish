package org.pb.android.ioiofish.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.pb.android.ioiofish.R;
import org.pb.android.ioiofish.animation.AnimationConfiguration;
import org.pb.android.ioiofish.animation.AnimationManager;

@SuppressLint("NonConstantResourceId")
@EViewGroup(R.layout.view_base_state)
public class BaseStateView extends LinearLayout {

    @ViewById(R.id.ivOval)
    ImageView ivOval;

    @ViewById(R.id.ivLine)
    ImageView ivLine;

    @ViewById(R.id.ivLine2)
    ImageView ivLine2;

    @ViewById(R.id.ivHexagon)
    ImageView ivHexagon;

    @ViewById(R.id.ivStripesLeft)
    ImageView ivStripesLeft;

    @ViewById(R.id.ivStripesRight)
    ImageView ivStripesRight;

    public boolean isConnected = false;

    @Bean
    AnimationManager animationManager;

    public BaseStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    public void initView() {

    }

    public void setConnecting() {
        ivLine.setBackgroundResource(R.drawable.sensor_outgoing_signal_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivLine.getBackground();
        frameAnimation.start();
    }

    public void setConnected() {
        ivOval.setImageResource(R.drawable.iv_oval_teal_200_full);
        ivLine.setImageResource(R.drawable.iv_line_dot_teal_200_full);
        ivHexagon.setImageResource(R.drawable.iv_hexagon_teal_200_full);
        ivLine2.setImageResource(R.drawable.iv_line2_teal_200_full);
    }

    public void setDisconnected() {
        ivOval.setImageResource(R.drawable.iv_oval_teal_200_quarter);
        ivLine.setImageResource(R.drawable.iv_line_dot_teal_200_quarter);
        ivHexagon.setImageResource(R.drawable.iv_hexagon_teal_200_quarter);
        ivLine2.setImageResource(R.drawable.iv_line2_teal_200_quarter);
    }

    public void receiveSensorSignal() {
        animationManager.startAnimation(ivLine, AnimationConfiguration.SENSOR_DATA_INCOMING);
    }

    public void receiveGyroSignal(float pitchLevel) {
        setPitchLevelState(pitchLevel);

        if (pitchLevel >= 0f) {
            animationManager.startAnimation(ivLine2, pitchLevel, AnimationConfiguration.GYRO_DATA_INCOMING_RIGHT);
        } else {
            animationManager.startAnimation(ivLine2, pitchLevel, AnimationConfiguration.GYRO_DATA_INCOMING_LEFT);
        }
    }

    // FIXME?: not very elegant, but works so far ...
    private void setPitchLevelState(float pitchLevel) {
        int pitchLevelImageResourceLeft = pitchLevel < 0f
                ? animationManager.getPitchLevelStateImageResource(pitchLevel)
                : R.drawable.ani_stripes_0;

        int pitchLevelImageResourceRight = pitchLevel > 0f
                ? animationManager.getPitchLevelStateImageResource(pitchLevel)
                : R.drawable.ani_stripes_0;

        ivStripesRight.setImageResource(pitchLevelImageResourceRight);
        ivStripesLeft.setImageResource(pitchLevelImageResourceLeft);
    }
}
