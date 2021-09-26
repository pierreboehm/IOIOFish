package org.pb.android.ioiofish.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.pb.android.ioiofish.R;

@EViewGroup(R.layout.view_base_state)
public class BaseStateView extends LinearLayout {

    @ViewById(R.id.ivOval)
    ImageView ivOval;

    @ViewById(R.id.ivLine)
    AppCompatImageView ivLine;

    @ViewById(R.id.ivHexagon)
    ImageView ivHexagon;

    public boolean isConnected = false;

    public BaseStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    public void initView() {
        ivLine.setBackgroundResource(R.drawable.connection_state_line_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivLine.getBackground();
        frameAnimation.start();
    }

    public void setConnected() {
        isConnected = true;
        ivOval.setBackgroundResource(R.drawable.iv_oval_teal_200_full);
        ivLine.setBackgroundResource(R.drawable.iv_line_teal_200_full);
        ivHexagon.setBackgroundResource(R.drawable.iv_hexagon_teal_200_full);
    }

    public void setDisconnected() {
        isConnected = false;
        ivOval.setBackgroundResource(R.drawable.iv_oval_teal_200_quarter);
        ivLine.setBackgroundResource(R.drawable.connection_state_line_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivLine.getBackground();
        frameAnimation.start();
        ivHexagon.setBackgroundResource(R.drawable.iv_hexagon_teal_200_quarter);
    }
}
