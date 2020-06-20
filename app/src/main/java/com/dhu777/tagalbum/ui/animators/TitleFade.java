package com.dhu777.tagalbum.ui.animators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

public class TitleFade {
    private static AnimatorSet animatorSet;

    @FunctionalInterface
    public interface Callback {
        void setTitle(Toolbar toolbar);
    }

    private static ValueAnimator getDefaultValueAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }

    public static void process(@NonNull final Toolbar toolbar,TitleFade.Callback callback){
        if(animatorSet!=null)
            animatorSet.cancel();

        TextView titleView = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v instanceof TextView) {
                titleView = (TextView) v;
                break;
            }
        }
        final TextView finalTextView = titleView;

        ValueAnimator fadeOut = ValueAnimator.ofInt(0, 100);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOut.setDuration(250);
        if (finalTextView != null) {
            fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    finalTextView.setAlpha(1 - valueAnimator.getAnimatedFraction());
                }
            });
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (callback != null)
                        callback.setTitle(toolbar);
                }
            });
        }

        ValueAnimator fadeIn = ValueAnimator.ofInt(0, 100);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setDuration(250);
        if (finalTextView != null) {
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    finalTextView.setAlpha(1.0f);
                }
            });
        }

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(fadeOut, fadeIn);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet = null;
            }
        });
        animatorSet.start();
    }
}
