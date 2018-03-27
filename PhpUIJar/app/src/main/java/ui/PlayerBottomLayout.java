package ui;

import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.content.Context;

import ui.utils.LogHelper;

import android.view.KeyEvent;
import android.view.MotionEvent;

public class PlayerBottomLayout extends LinearLayout {
    private IDelegateEventListener mIDelegateEventListener = null;
    private static String LOG_TAG = PlayerBottomLayout.class.getSimpleName();

    public interface IDelegateEventListener {
        public boolean keyEventDispatcherPBC(KeyEvent event);

        public boolean touchEventDispatcherPBC(MotionEvent event);
    }

    ;

    public PlayerBottomLayout(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
    }

    public PlayerBottomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerBottomLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.e(LOG_TAG, "dispatchKeyEvent " + event.toString() + " " + toString());

        if (null != mIDelegateEventListener) {
            return mIDelegateEventListener.keyEventDispatcherPBC(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogHelper.e(LOG_TAG, "onTouchEvent " + event.toString() + " " + toString());
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        if ((event.getAction() == MotionEvent.ACTION_DOWN)
                && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
            if (null != mIDelegateEventListener) {
                return mIDelegateEventListener.touchEventDispatcherPBC(event);
            }
            //dismiss();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (null != mIDelegateEventListener) {
                return mIDelegateEventListener.touchEventDispatcherPBC(event);
            }
            //dismiss();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public void setIDelegateEventListener(IDelegateEventListener iIDelegateEventListener) {
        mIDelegateEventListener = iIDelegateEventListener;
    }

}
