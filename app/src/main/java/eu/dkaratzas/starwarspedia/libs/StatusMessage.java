package eu.dkaratzas.starwarspedia.libs;

import android.animation.Animator;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.libs.animations.YoYo;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.SlideInUpAnimator;
import eu.dkaratzas.starwarspedia.libs.animations.techniques.SlideOutDownAnimator;

public class StatusMessage {
    private static volatile StatusMessage sharedInstance;

    private static final int ANIMATION_DURATION = 300;
    private static int HIDE_DELAY = 5000;

    private View mContainer;
    private TextView mTextView;
    private Handler mHandler;

    private StatusMessage() {
        mHandler = new Handler();
    }

    public static void show(Activity activity, String message) {
        if (sharedInstance == null) {
            synchronized (StatusMessage.class) {
                if (sharedInstance == null) sharedInstance = new StatusMessage();
            }
        }
        sharedInstance.showMessage(activity, message);
    }

    public static void hide() {
        sharedInstance.hideMessage();
    }

    private void init(Activity activity) {
        // Remove handler callbacks
        mHandler.removeCallbacksAndMessages(null);
        // remove the view if already exists
        removeView();

        // inflate status layout view
        ViewGroup container = activity.findViewById(android.R.id.content);
        View view = activity.getLayoutInflater().inflate(R.layout.status_layout, container);

        mContainer = view.findViewById(R.id.statusMessageContainer);
        mContainer.setVisibility(View.GONE);
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mTextView = view.findViewById(R.id.tvStatusMessage);
    }

    private void showMessage(Activity activity, String message) {
        init(activity);

        mTextView.setText(message);

        YoYo.with(new SlideInUpAnimator())
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        mContainer.setVisibility(View.VISIBLE);
                    }
                })
                .duration(ANIMATION_DURATION)
                .playOn(mContainer);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideMessage();
            }
        }, HIDE_DELAY);
    }

    private void hideMessage() {
        if (mContainer != null) {
            YoYo.with(new SlideOutDownAnimator())
                    .duration(ANIMATION_DURATION)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            removeView();
                        }
                    })
                    .playOn(mContainer);
        }
    }

    private void removeView() {
        if (mContainer != null && mContainer.getParent() != null) {
            ((ViewGroup) mContainer.getParent()).removeView(mContainer);
            Logger.d("Removed status message container");
        }
    }
}
