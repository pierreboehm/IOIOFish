package org.pb.android.ioiofish.animation;

import android.animation.ValueAnimator;
import android.widget.ImageView;

import org.androidannotations.annotations.EBean;
import org.pb.android.ioiofish.R;

import java.util.HashMap;
import java.util.Map;

@EBean(scope = EBean.Scope.Singleton)
public class AnimationManager {

    private Map<ImageView, ValueAnimator> animatorMap = new HashMap<>();

    public void startAnimation(ImageView imageView, AnimationConfiguration animationConfiguration) {

        if (animatorMap.containsKey(imageView)) {
            ValueAnimator animator = animatorMap.get(imageView);
            if (animator != null && (animator.isRunning() || animator.isStarted())) {
                return;
            }
        }

        startAnimation(
                0,
                animationConfiguration.getResourceIds().length - 1,
                animationConfiguration.getResourceIds(),
                animationConfiguration.getDuration(),       // will later be modified with reaction speed
                imageView);
    }

    public int getPitchLevelStateImageResource(float pitchLevel) {
        if (pitchLevel < 0f) {
            if (pitchLevel < -1f && pitchLevel >= -2f) {
                return R.drawable.ani_stripes_l1;
            } else if (pitchLevel < -2f && pitchLevel >= -4f) {
                return R.drawable.ani_stripes_l2;
            } else if (pitchLevel < -4f && pitchLevel >= -8f) {
                return R.drawable.ani_stripes_l3;
            } else if (pitchLevel < -8f && pitchLevel >= -16f) {
                return R.drawable.ani_stripes_l4;
            } else if (pitchLevel < -16f) {
                return R.drawable.ani_stripes_l5;
            }
        } else if (pitchLevel > 0f) {
            if (pitchLevel > 1f && pitchLevel <= 2f) {
                return R.drawable.ani_stripes_r1;
            } else if (pitchLevel > 2f && pitchLevel <= 4f) {
                return R.drawable.ani_stripes_r2;
            } else if (pitchLevel > 40f && pitchLevel <= 8f) {
                return R.drawable.ani_stripes_r3;
            } else if (pitchLevel > 8f && pitchLevel <= 16f) {
                return R.drawable.ani_stripes_r4;
            } else if (pitchLevel > 16f) {
                return R.drawable.ani_stripes_r5;
            }
        }

        return R.drawable.ani_stripes_0;
    }

    private void startAnimation(int startValue, int endValue, int[] resourceIds, int duration, ImageView imageView) {
        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.addUpdateListener(animation -> {
            int frameIndex = (int) animation.getAnimatedValue();
            imageView.setImageResource(resourceIds[frameIndex]);
        });

        animator.setDuration((endValue + 1) * duration);
        animator.start();

        animatorMap.put(imageView, animator);
    }

    /*private void stopAnimation() {
        if (animator != null) {
            animator.end();
            animator.cancel();
            animator = null;
        }
    }*/

}
