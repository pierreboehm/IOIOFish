package org.pb.android.ioiofish.animation;

import org.pb.android.ioiofish.R;

public enum AnimationConfiguration {

    // define all specific animation content here
    SENSOR_DATA_INCOMING(50, new int[]{
            R.drawable.ani_line_10, R.drawable.ani_line_9, R.drawable.ani_line_8, R.drawable.ani_line_7,
            R.drawable.ani_line_6, R.drawable.ani_line_5, R.drawable.ani_line_4, R.drawable.ani_line_3,
            R.drawable.ani_line_2, R.drawable.ani_line_1, R.drawable.iv_line_dot_teal_200_full
    }),
    SENSOR_DATA_OUTGOING(50, new int[]{
            R.drawable.ani_line_1, R.drawable.ani_line_2, R.drawable.ani_line_3, R.drawable.ani_line_4,
            R.drawable.ani_line_6, R.drawable.ani_line_7, R.drawable.ani_line_8, R.drawable.ani_line_9,
            R.drawable.ani_line_10, R.drawable.iv_line_dot_teal_200_full
    }),
    GYRO_DATA_INCOMING_LEFT(50, new int[]{
            R.drawable.ani_line2_1, R.drawable.ani_line2_2, R.drawable.ani_line2_3, R.drawable.ani_line2_4,
            R.drawable.ani_line2_5, R.drawable.ani_line2_6, R.drawable.ani_line2_7, R.drawable.ani_line2_8,
            R.drawable.ani_line2_9, R.drawable.ani_line2_10, R.drawable.ani_line2_11, R.drawable.ani_line2_l1,
            R.drawable.ani_line2_l2, R.drawable.ani_line2_l3, R.drawable.iv_line2_teal_200_full
    }),
    GYRO_DATA_INCOMING_RIGHT(50, new int[]{
            R.drawable.ani_line2_1, R.drawable.ani_line2_2, R.drawable.ani_line2_3, R.drawable.ani_line2_4,
            R.drawable.ani_line2_5, R.drawable.ani_line2_6, R.drawable.ani_line2_7, R.drawable.ani_line2_8,
            R.drawable.ani_line2_9, R.drawable.ani_line2_10, R.drawable.ani_line2_r1, R.drawable.ani_line2_r1,
            R.drawable.ani_line2_r2, R.drawable.ani_line2_r3, R.drawable.iv_line2_teal_200_full
    });

    private final int duration;     // duration of single frame showing
    private final int[] resourceIds;

    AnimationConfiguration(int duration, int[] resourceIds) {
        this.duration = duration;
        this.resourceIds = resourceIds;
    }

    public int getDuration() {
        return duration;
    }

    public int[] getResourceIds() {
        return resourceIds;
    }
}
