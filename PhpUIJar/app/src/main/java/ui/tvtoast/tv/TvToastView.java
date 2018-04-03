package ui.tvtoast.tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fany.phpuijar.R;

public class TvToastView extends RelativeLayout {

    private static final String TAG = TvToastView.class.getSimpleName();

    private static final int ANIMATION_START_DELAY = 250;
    private static final int ANIMATION_DURATION = 250;
    private static final int MARQUEE_START_DELAY = 1000;
    private static final int MARQUEE_DP_PER_SECOND = 30;

    private TextView mTvToastMessage;
    private ImageView mTvToastMessageIcon;

    private float mMaxWidth;
    private float mIconWidth;
    private int mMsgRightPaddingWithImage;
    private int mMsgRightPaddingWithoutImage;
    private final float mPixelsPerSecond;

    private ValueAnimator mSlideOpenAnim;
    private MessageState mMessageState;
    private Animation mJumperAnimation;

    /*
 * Handle messages to be processed with a delay
 */
    private final class MessageStateHandler extends Handler {

        private static final int MESSAGE_START_ANIMATION = 1000;
        private static final int MESSAGE_START_MARQUEE = 1001;
        private static final int MESSAGE_STOP_MARQUEE = 1002;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_START_ANIMATION:
                    if (mMessageState.getState() == MessageState.MESSAGE_ATTACHED_TO_WINDOW) {
                        mMessageState.setState(MessageState.MESSAGE_ANIMATION_START);
                        mSlideOpenAnim.start();
                    }
                    break;

                case MESSAGE_START_MARQUEE:
                    if (mMessageState.getState() == MessageState.MESSAGE_ANIMATION_STOP) {
                        mMessageState.setState(MessageState.MESSAGE_MARQUEE_START);
                        mTvToastMessage.setSelected(true);
                    }

                    // After a delay stop marquee
                    mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_STOP_MARQUEE);
                    mMessageStateHandler.sendEmptyMessageDelayed(MessageStateHandler.MESSAGE_STOP_MARQUEE, msg.arg1 * 1000);
                    break;

                case MESSAGE_STOP_MARQUEE:
                    if (mMessageState.getState() == MessageState.MESSAGE_MARQUEE_START) {
                        mMessageState.setState(MessageState.MESSAGE_MARQUEE_STOP);
                        mTvToastMessage.setSelected(false);
                        mMessageState.setState(MessageState.MESSAGE_READ_ONCE);
                    }
                    break;
            }
        }
    }

    private MessageStateHandler mMessageStateHandler = new MessageStateHandler();

    public TvToastView(Context context) {
        this(context, null, 0);
    }

    public TvToastView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvToastView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "TvToastView Init");
        mMessageState = new MessageState();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.tv_toast_message_layout, this);

        mTvToastMessage = findViewById(R.id.tvToastMessage);
        mTvToastMessageIcon = findViewById(R.id.tvToastMessageIcon);

        setVisibility(View.INVISIBLE);

        mMaxWidth = getResources().getDimension(R.dimen.flashmessage_long_container_width);
        mIconWidth = getResources().getDimension(R.dimen.flashmessage_alert_icon_height);
        mMsgRightPaddingWithImage = (int) getResources().getDimension(R.dimen.flashmessage_alert_icon_padding_left_width);
        mMsgRightPaddingWithoutImage = (int) getResources().getDimension(R.dimen.flashmessage_alert_label_padding_left_width);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        mPixelsPerSecond = MARQUEE_DP_PER_SECOND * metrics.density;
        mJumperAnimation = AnimationUtils.loadAnimation(context, R.xml.tvtoast_anim);
        Log.d(TAG, "TvToastView End");
    }

    public int getMessageState() {
        return mMessageState.getState();
    }

    /*
 * reset To default Layout Params
 */
    private void resetToDefaultLayoutParams() {
        LayoutParams params = (LayoutParams) getLayoutParams();

        params.width = LayoutParams.WRAP_CONTENT;
        setLayoutParams(params);
    }

    /*
 * get Icon Width + left padding + right padding
 */
    private float getTotalIconWidth() {
        return mIconWidth + (getResources().getDimension(R.dimen.flashmessage_alert_icon_padding_right_width) * 2);
    }

    /*
 * initialize Slide Animation
 */
    private void initSlideAnimator(final float startValue, final float endValue, final int marqueeDuration) {
        if (mSlideOpenAnim == null) {
            mSlideOpenAnim = ValueAnimator.ofFloat(startValue, endValue);
        } else {
            mSlideOpenAnim.setFloatValues(startValue, endValue);
            mSlideOpenAnim.removeAllListeners();
            mSlideOpenAnim.removeAllUpdateListeners();
        }

        mSlideOpenAnim.setDuration(ANIMATION_DURATION);
        mSlideOpenAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mSlideOpenAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mMessageState.getState() == MessageState.MESSAGE_ANIMATION_START) {
                    mMessageState.setState(MessageState.MESSAGE_ANIMATION_STOP);
                    mTvToastMessage.setVisibility(View.VISIBLE);

                    if (endValue == mMaxWidth) {
                        // if msg is longer than maxWidth, then re-adjust width of text view
                        int textWidth = (int) ((mTvToastMessageIcon.getVisibility() == View.VISIBLE) ?
                                Math.floor(mMaxWidth - getTotalIconWidth()) :
                                Math.floor(mMaxWidth));
                        LayoutParams params = (LayoutParams) mTvToastMessage.getLayoutParams();
                        params.width = textWidth;
                        mTvToastMessage.setLayoutParams(params);

                        // if msg is longer than mMaxWidth, then marquee
                        mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_START_MARQUEE);
                        mMessageStateHandler.sendMessageDelayed(mMessageStateHandler.obtainMessage(MessageStateHandler.MESSAGE_START_MARQUEE, marqueeDuration, 0),
                                MARQUEE_START_DELAY);
                    } else {
                        // if msg is shorter than mMaxWidth, then skip marquee state
                        mMessageState.setState(MessageState.MESSAGE_READ_ONCE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mMessageState.reset();
                mTvToastMessage.setVisibility(View.VISIBLE);
                mTvToastMessage.setSelected(false);
                resetToDefaultLayoutParams();
            }
        });
        mSlideOpenAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float fraction = (Float) animation.getAnimatedValue();
                LayoutParams params = (LayoutParams) TvToastView.this.getLayoutParams();
                params.width = (int) Math.ceil(fraction.floatValue());
                TvToastView.this.setLayoutParams(params);
                TvToastView.this.invalidate();
            }
        });
    }

    /*
 * show message
 */
    private void slideOpenMessage() {
        if (isFocusable()) {
            requestFocus();
        }

        // Reset text width to wrap content before doing a measure
        LayoutParams textParams = (LayoutParams) mTvToastMessage.getLayoutParams();
        textParams.width = LayoutParams.WRAP_CONTENT;
        mTvToastMessage.setLayoutParams(textParams);
        invalidate();

        // Measure full view and adjust width accordingly
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        float absoluteWidth = getMeasuredWidth();

        final float startValue = getTotalIconWidth();
        final float endValue = absoluteWidth <= mMaxWidth ? absoluteWidth : mMaxWidth;

        // Measure time to scroll
        final int marqueeDuration;

        if (endValue == mMaxWidth) {
            float lineWidth = mTvToastMessage.getLayout().getLineWidth(0);
            int textWidth = (int) ((mTvToastMessageIcon.getVisibility() == View.VISIBLE) ?
                    Math.floor(mMaxWidth - getTotalIconWidth()) :
                    Math.floor(mMaxWidth));
            float distanceToMarquee = (lineWidth - textWidth) + (textWidth / 3.0f);
            marqueeDuration = (int) Math.ceil(distanceToMarquee / mPixelsPerSecond);
            Log.d(TAG, "lineWidth: " + lineWidth + " textWidth: " + textWidth + " marqueeDuration: " + marqueeDuration + " distanceToMarquee: " + distanceToMarquee);
        } else {
            marqueeDuration = 0;
        }

        // update params
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.width = (int) Math.ceil(startValue);
        setLayoutParams(params);
        invalidate();

        setVisibility(View.VISIBLE);
        mTvToastMessage.setVisibility(View.GONE);

        initSlideAnimator(startValue, endValue, marqueeDuration);

        // Show circle with/without image for some time, then open up the msg
        mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_START_ANIMATION);
        mMessageStateHandler.sendEmptyMessageDelayed(MessageStateHandler.MESSAGE_START_ANIMATION, ANIMATION_START_DELAY);
    }

    /*
 * cancel message
 */
    private void slideCloseMessage() {
        // Cancel any previous running animation
        if (mSlideOpenAnim != null && mSlideOpenAnim.isRunning()) {
            mSlideOpenAnim.cancel();
        }
        setVisibility(View.INVISIBLE);
        resetToDefaultLayoutParams();
        mTvToastMessage.setVisibility(View.VISIBLE);
        mTvToastMessage.setSelected(false);
    }

    /**
     * Set Message
     *
     * @param msg
     */
    public void setMessage(String msg) {
        mTvToastMessage.setText(msg);
    }

    /**
     * Set Icon
     *
     * @param icon
     */
    public void setIcon(Drawable icon) {
        mTvToastMessageIcon.setImageDrawable(icon);
        mTvToastMessageIcon.setVisibility(icon == null ? View.GONE : View.VISIBLE);
        mTvToastMessage.setPadding(mTvToastMessage.getPaddingLeft(), mTvToastMessage.getPaddingTop(),
                icon == null ? mMsgRightPaddingWithoutImage : mMsgRightPaddingWithImage, mTvToastMessage.getPaddingBottom());
    }

    /**
     * Set bitmap Icon
     *
     * @param bitmap
     */
    public void setIcon(Bitmap bitmap) {
        mTvToastMessageIcon.setImageBitmap(bitmap);
        mTvToastMessageIcon.setVisibility(bitmap == null ? View.GONE : View.VISIBLE);
        mTvToastMessage.setPadding(mTvToastMessage.getPaddingLeft(), mTvToastMessage.getPaddingTop(),
                bitmap == null ? mMsgRightPaddingWithoutImage : mMsgRightPaddingWithImage, mTvToastMessage.getPaddingBottom());
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow Enter");
        mMessageState.setState(MessageState.MESSAGE_ATTACHED_TO_WINDOW);
        slideOpenMessage();
        mJumperAnimation.reset();
        startAnimation(mJumperAnimation);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow Enter");
        // clean pending messages on queue and reset state
        mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_START_ANIMATION);
        mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_START_MARQUEE);
        mMessageStateHandler.removeMessages(MessageStateHandler.MESSAGE_STOP_MARQUEE);
        mMessageState.reset();

        slideCloseMessage();
        super.onDetachedFromWindow();
        clearAnimation();
    }

    public void setStateTransitionCallback(MessageState.IStateTransitionCallback callback) {
        if (mMessageState != null) {
            mMessageState.setStateTransitionCallback(callback);
        }
    }

    public final static class MessageState {

        public static final int MESSAGE_SHOW_READY = 0;
        public static final int MESSAGE_ATTACHED_TO_WINDOW = 1;
        public static final int MESSAGE_ANIMATION_START = 2;
        public static final int MESSAGE_ANIMATION_STOP = 3;
        public static final int MESSAGE_MARQUEE_START = 4;
        public static final int MESSAGE_MARQUEE_STOP = 5;
        public static final int MESSAGE_READ_ONCE = 6;

        private int mState = MESSAGE_SHOW_READY;
        private IStateTransitionCallback mStateTransitionCallback;

        public void setState(int newState) {
            if (newState != mState) {
                int oldState = mState;
                mState = newState;
                if (mStateTransitionCallback != null) {
                    mStateTransitionCallback.onMessageStateChanged(oldState, newState);
                    Log.d(TAG, "MessageStateTransition oldState: " + oldState + " newState: " + newState);
                }
            }
        }

        public int getState() {
            return mState;
        }

        public void reset() {
            setState(MESSAGE_SHOW_READY);
        }

        public void setStateTransitionCallback(IStateTransitionCallback callback) {
            mStateTransitionCallback = callback;
        }

        public static interface IStateTransitionCallback {
            public void onMessageStateChanged(int oldState, int newState);
        }
    }
}