package org.pb.android.ioiofish.animation;

import android.animation.ValueAnimator;
import android.widget.ImageView;

import org.androidannotations.annotations.EBean;

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
                animationConfiguration.getDuration(),
                imageView);
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
