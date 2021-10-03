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
            animationManager.startAnimation(ivLine2, AnimationConfiguration.GYRO_DATA_INCOMING_RIGHT);
        } else {
            animationManager.startAnimation(ivLine2, AnimationConfiguration.GYRO_DATA_INCOMING_LEFT);
        }
    }

    private void setPitchLevelState(float pitchLevel) {

        ivStripesRight.setImageResource(R.drawable.ani_stripes_0);
        ivStripesLeft.setImageResource(R.drawable.ani_stripes_0);

        if (pitchLevel < -5f && pitchLevel >= -10f) {
            ivStripesLeft.setImageResource(R.drawable.ani_stripes_l1);
        } else if (pitchLevel < -10f && pitchLevel >= -20f) {
            ivStripesLeft.setImageResource(R.drawable.ani_stripes_l2);
        } else if (pitchLevel < -20f && pitchLevel >= -30f) {
            ivStripesLeft.setImageResource(R.drawable.ani_stripes_l3);
        } else if (pitchLevel < -30f && pitchLevel >= -40f) {
            ivStripesLeft.setImageResource(R.drawable.ani_stripes_l4);
        } else if (pitchLevel < -40f) {
            ivStripesLeft.setImageResource(R.drawable.ani_stripes_l5);
        }

        if (pitchLevel > 5f && pitchLevel <= 10f) {
            ivStripesRight.setImageResource(R.drawable.ani_stripes_r1);
        } else if (pitchLevel > 10f && pitchLevel <= 20f) {
            ivStripesRight.setImageResource(R.drawable.ani_stripes_r2);
        } else if (pitchLevel > 20f && pitchLevel <= 30f) {
            ivStripesRight.setImageResource(R.drawable.ani_stripes_r3);
        } else if (pitchLevel > 30f && pitchLevel <= 40f) {
            ivStripesRight.setImageResource(R.drawable.ani_stripes_r4);
        } else if (pitchLevel > 40f) {
            ivStripesRight.setImageResource(R.drawable.ani_stripes_r5);
        }
    }
}
