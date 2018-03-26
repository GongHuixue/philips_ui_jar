package ui;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/6/29.
 */

public class ColorKeys extends RelativeLayout {
    private TextView mRedKey;
    private TextView mGreenKey;
    private TextView mYellowKey;
    private TextView mBlueKey;

    private DateTimeView mDateTimeView;
    private IColorKeyCallback mColorKeyCallback;
    private static final String TAG = "ColorKeyBar";

    private boolean isRedKeyEnabled = true;
    private boolean isGreenKeyEnabled = true;
    private boolean isBlueKeyEnabled = true;
    private boolean isYellowKeyEnabled = true;
    private boolean isColorKeyBarVisisble = true;

    private ObjectAnimator mAppearingAnimation, mDisappearingAnimation;
    private int mRedLabelId, mYellowLabelId, mGreenLabelId, mBlueLabelId;

    public ColorKeys(Context context) {
        this(context, null, 0);
    }

    public ColorKeys(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorKeys(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "ColorKeys Constructor");
        loadLayout();
        loadAttributes(attrs, defStyle);
    }

    /**
     * Loads the color key layout XML
     */
    private void loadLayout() {
        LayoutInflater lInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup v = (ViewGroup) lInflater.inflate(R.layout.color_keys, this, true);
        LinearLayout colourKeyBarLayout = (LinearLayout) v.findViewById(R.id.colorkeylayout);

        mRedKey = (TextView) v.findViewById(R.id.redColorKey);
        mGreenKey = (TextView) v.findViewById(R.id.greenColorKey);
        mYellowKey = (TextView) v.findViewById(R.id.yellowColorKey);
        mBlueKey = (TextView) v.findViewById(R.id.blueColorKey);

        mDateTimeView = (DateTimeView) v.findViewById(R.id.colorkey_datetime);

        // Initially disabling all the keys
        setRedLabel("");
        setGreenLabel("");
        setYellowLabel("");
        setBlueLabel("");

        mAppearingAnimation = ObjectAnimator.ofFloat(this, "translationY", 50f, 0f);
        mDisappearingAnimation = ObjectAnimator.ofFloat(this, "translationY", 0f, 50f);

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mDisappearingAnimation);
        layoutTransition.setAnimator(LayoutTransition.APPEARING, mAppearingAnimation);

        colourKeyBarLayout.setLayoutTransition(layoutTransition);

        setPadding(getResources().getDimensionPixelSize(R.dimen.colorkey_bar_padding_left), 0, getResources()
                .getDimensionPixelSize(R.dimen.colorkey_bar_padding_right_width), 0);
    }

    /**
     * Loads the custom attributes
     *
     * @param attrs    = mentioned in XML
     * @param defStyle = styles if any
     */
    private void loadAttributes(AttributeSet attrs, int defStyle) {

        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.ColorKeys, defStyle, 0);

            setRedLabel(attributes.getString(R.styleable.ColorKeys_colorKeyRedText));
            setBlueLabel(attributes.getString(R.styleable.ColorKeys_colorKeyBlueText));
            setYellowLabel(attributes.getString(R.styleable.ColorKeys_colorKeyYellowText));
            setGreenLabel(attributes.getString(R.styleable.ColorKeys_colorKeyGreenText));

            attributes.recycle();
        }
    }


    /**
     * Provide functionality for animate whole ColorKeyBar through visibility
     *
     * @param visibility True means animate ColorKeybar from bottom to top and false
     *                   means animate ColorKeybar from top to bottom
     */
    public void setColorKeyBarVisibility(boolean visibility) {
        if (visibility) {
            isColorKeyBarVisisble = true;
            mAppearingAnimation.start();
        } else {
            isColorKeyBarVisisble = false;
            mDisappearingAnimation.start();
        }
    }

    public void setTimeBarVisibility(boolean visibility) {
        if (visibility) {
            mDateTimeView.setVisibility(View.VISIBLE);
        } else {
            mDateTimeView.setVisibility(View.GONE);
        }
    }

    /**
     * KeyEvent handler for the color keys.
     * <p>
     * This method should be called in the onKeyDown callback in the activity.
     *
     * @param keyCode The code of the pressed key
     * @return true if the key is handled.
     */
    public boolean handleKeyDown(int keyCode) {
        if (mColorKeyCallback == null || !isColorKeyBarVisisble) {
            return false;
        }

        boolean lHandled = true;
        switch (keyCode) {
            // RED
            case KeyEvent.KEYCODE_PROG_RED:
                if (isRedKeyEnabled) {
                    mColorKeyCallback.onRedKeyPressed();
                } else {
                    lHandled = false;
                }
                break;
            // GREEN
            case KeyEvent.KEYCODE_PROG_GREEN:
                if (isGreenKeyEnabled) {
                    mColorKeyCallback.onGreenKeyPressed();
                } else {
                    lHandled = false;
                }
                break;
            // YELLOW
            case KeyEvent.KEYCODE_PROG_YELLOW:
                if (isYellowKeyEnabled) {
                    mColorKeyCallback.onYellowKeyPressed();
                } else {
                    lHandled = false;
                }
                break;
            // BLUE
            case KeyEvent.KEYCODE_PROG_BLUE:
                if (isBlueKeyEnabled) {
                    mColorKeyCallback.onBlueKeyPressed();
                } else {
                    lHandled = false;
                }
                break;
            default:
                lHandled = false;
                break;
        }
        return lHandled;
    }

    /**
     * Register a callback.
     * <p>
     * The implemented IColorKeyCallback object will receive a callback when a
     * specific key is pressed.
     *
     * @param callback IColorKeyCallback implementation
     */
    public void registerCallback(IColorKeyCallback callback) {
        mColorKeyCallback = callback;
    }

    /**
     * Unregister the registered callback.
     */
    public void unRegisterCallback() {
        mColorKeyCallback = null;
    }

    /**
     * Sets the red label text
     *
     * @param label = string to be set as label. If pass blank or null string then
     *              red key automatically hide with animation
     */
    public void setRedLabel(String label) {
        Log.d(TAG, "setRedLabel: " + label);
        if (label == null || label.equals("")) {
            isRedKeyEnabled = false;
            mRedKey.setVisibility(View.GONE);
        } else {
            mRedKey.setText(label);
            isRedKeyEnabled = true;
            mRedKey.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the Red label text to ColorKeyBar
     *
     * @param id Resource id for the format string. 0 in case of nothing want to set
     */
    public void setRedLabelId(int id) {
        this.mRedLabelId = id;
        if (mRedLabelId > 0) {
            setRedLabel(getResources().getString(id));
        } else {
            setRedLabel(null);
        }
    }

    /**
     * Sets the yellow label text
     *
     * @param label = string to be set as label. If pass blank or null string then
     *              yellow key automatically hide with animation
     */
    public void setYellowLabel(String label) {
        Log.d(TAG, "setYellowLabel: " + label);
        if (label == null || label.equals("")) {
            isYellowKeyEnabled = false;
            mYellowKey.setVisibility(View.GONE);
        } else {
            mYellowKey.setText(label);
            isYellowKeyEnabled = true;
            mYellowKey.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the yellow label text to ColorKeyBar
     *
     * @param id Resource id for the format string. 0 in case of nothing want to set
     */
    public void setYellowLabelId(int id) {
        this.mYellowLabelId = id;
        if (mYellowLabelId > 0) {
            setYellowLabel(getResources().getString(id));
        } else {
            setYellowLabel(null);
        }
    }

    /**
     * Sets the green label text
     *
     * @param label = string to be set as label. If pass blank or null string then
     *              green key automatically hide with animation
     */
    public void setGreenLabel(String label) {
        Log.d(TAG, "setGreenLabel: " + label);
        if (label == null || label.equals("")) {
            isGreenKeyEnabled = false;
            mGreenKey.setVisibility(View.GONE);

        } else {
            isGreenKeyEnabled = true;
            mGreenKey.setVisibility(View.VISIBLE);
            mGreenKey.setText(label);

        }
    }

    /**
     * Sets the Green label text to ColorKeyBar
     *
     * @param id Resource id for the format string. 0 in case of nothing want to set
     */
    public void setGreenLabelId(int id) {
        this.mGreenLabelId = id;
        if (mGreenLabelId > 0) {
            setGreenLabel(getResources().getString(id));
        } else {
            setGreenLabel(null);
        }
    }

    /**
     * Sets the blue label text
     *
     * @param label = string to be set as label.If pass blank or null string then
     *              blue key automatically hide with animation
     */
    public void setBlueLabel(String label) {
        Log.d(TAG, "setBlueLabel: " + label);
        if (label == null || label.equals("")) {
            isBlueKeyEnabled = false;
            mBlueKey.setVisibility(View.GONE);
        } else {
            isBlueKeyEnabled = true;
            mBlueKey.setVisibility(View.VISIBLE);
            mBlueKey.setText(label);
        }
    }

    /**
     * Sets the Blue label text to ColorKeyBar
     *
     * @param id Resource id for the format string. 0 in case of nothing want to set
     */
    public void setBlueLabelId(int id) {
        this.mBlueLabelId = id;
        if (mBlueLabelId > 0) {
            setBlueLabel(getResources().getString(id));
        } else {
            setBlueLabel(null);
        }
    }

    /**
     * Refresh the colorkey bar. Refreshing date and time for configuration
     * changes. Use this method to refresh color key bar, when needed explicitly
     * from parent.
     */

    public void refreshColorKeyBar() {
        mDateTimeView.updateTime();
    }

    public void setAllColorKeyLabelIDs(int redLabelID, int greenLabelID, int yellowLableId, int blueLabelId) {
        setAllColorKeyLabel(redLabelID > 0 ? getResources().getString(redLabelID) : null,
                greenLabelID > 0 ? getResources().getString(greenLabelID) : null, yellowLableId > 0 ? getResources()
                        .getString(yellowLableId) : null, blueLabelId > 0 ? getResources().getString(blueLabelId) : null);
    }

    /**
     * This method will set All the keys' label in one call
     *
     * @param redLabel    : Label to set red key
     * @param greenLabel  : Label to set green key
     * @param yellowLabel : Label to set yellow key
     * @param blueLabel   : Label to set blue key
     */
    public void setAllColorKeyLabel(String redLabel, String greenLabel, String yellowLabel, String blueLabel) {
        boolean redKey = false, greenKey = false, yellowKey = false, blueKey = false;
        if (mRedKey.getVisibility() == View.VISIBLE) {
            setRedLabel(redLabel);
            redKey = true;
        }

        if (mGreenKey.getVisibility() == View.VISIBLE) {
            setGreenLabel(greenLabel);
            greenKey = true;
        }

        if (mYellowKey.getVisibility() == View.VISIBLE) {
            setYellowLabel(yellowLabel);
            yellowKey = true;
        }

        if (mBlueKey.getVisibility() == View.VISIBLE) {
            setBlueLabel(blueLabel);
            blueKey = true;
        }

        if (!redKey) {
            setRedLabel(redLabel);
        }

        if (!greenKey) {
            setGreenLabel(greenLabel);
        }

        if (!yellowKey) {
            setYellowLabel(yellowLabel);
        }

        if (!blueKey) {
            setBlueLabel(blueLabel);
        }
    }

    /**
     * Refresh the colorkey bar. Refreshing all label based on configuration(language) changed.
     */
    public void refreshColorKeyBarOnConfigChanged() {
        refreshColorKeyBar();
        if (mRedLabelId > 0) {
            setRedLabel(getResources().getString(mRedLabelId));
        }
        if (mYellowLabelId > 0) {
            setYellowLabel(getResources().getString(mYellowLabelId));
        }
        if (mGreenLabelId > 0) {
            setGreenLabel(getResources().getString(mGreenLabelId));
        }
        if (mBlueLabelId > 0) {
            setBlueLabel(getResources().getString(mBlueLabelId));
        }
    }

    /**
     * The Colorkey callback interface.
     * <p>
     * This interface should be implemented and registered with
     * registerCallback(IColorKeyCallback callback) to receive callback events.
     */
    public interface IColorKeyCallback {

        boolean onRedKeyPressed();

        boolean onGreenKeyPressed();

        boolean onYellowKeyPressed();

        boolean onBlueKeyPressed();

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public boolean onHoverEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            params.height = getResources().getDimensionPixelSize(R.dimen.colorkey_bar_container_height);
        } else {
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                    R.dimen.colorkey_bar_container_height)));
        }
    }
}

