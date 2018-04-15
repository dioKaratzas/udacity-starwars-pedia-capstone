package eu.dkaratzas.starwarspedia.libs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;

public class Animations {

    public static void SlideInUpAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) view.getParent();
        int distance = parent.getHeight() - view.getTop();

        Animator anim = ObjectAnimator.ofFloat(view, "translationY", distance, 0);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(500);
        anim.start();
    }

    public static void SlideOutDownAnimation(final View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        int distance = parent.getHeight() - view.getTop();

        Animator anim = ObjectAnimator.ofFloat(view, "translationY", 0, distance);
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        anim.setDuration(500);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

}
