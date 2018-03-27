package ui;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import fany.phpuijar.R;

class BackTrace extends LinearLayout {
    private ArrayList<View> freeViews;
    private static final int BACKTRACE_NORMAL = 1;
    private static final int BACKTRACE_COLLAPSED = 2;
    private Animator defaultDisappearingAnim, defaultChangeDisappearingAnim, defaultAppearingAnim, defaultChangeAppearingAnim;

    private int MAX_VISIBLE_DEPTH_COUNT;


    private int mCurrentState;

    private boolean mIsWizard;
    private final LayoutTransition transitioner = new LayoutTransition();

    public BackTrace(Context context) {
        this(context, null, 0);
    }

    public BackTrace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackTrace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mCurrentState = BACKTRACE_NORMAL;
        freeViews = new ArrayList<View>();
        loadLayout(context);
        setClickable(false);
        setLongClickable(false);
    }


    void setDefaultAniamtion() {

        // for removing the default start delay

        //
        transitioner.setAnimator(LayoutTransition.DISAPPEARING, defaultDisappearingAnim);
        transitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, defaultChangeDisappearingAnim);
        transitioner.setAnimator(LayoutTransition.APPEARING, defaultAppearingAnim);
        transitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, defaultChangeAppearingAnim);
    }

    void setExpansionAmiation() {

        Log.d(getClass().getSimpleName(), "setExpansionAmiation");

        transitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, defaultDisappearingAnim);

        transitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, defaultAppearingAnim);
    }


    /**
     * @param isActivate true if the current depth is Active
     * @author manjunatha.sg
     */
    private void markDepthActiveOrDeactive(View v, boolean isActivate) {
        if (isActivate) {
            ((VerticalText) v).setTextColor(getResources().getColor(R.color.npanelbrowser_textcolor_activated, null));
            ((VerticalText) v).setTextAppearance(R.style.hnr);
        } else {

            ((VerticalText) v).setTextColor(getResources().getColor(R.color.npanelbrowser_textcolor_normal, null));
            ((VerticalText) v).setTextAppearance(R.style.hnl);
        }
    }

    /**
     * convinience method for activating the depth when a child is added or removed
     *
     * @author manjunatha.sg
     */
    private void activateCurrentDepth() {

        View lLeafChild = getChildAt(getChildCount() - 1);

        if (lLeafChild.getVisibility() != VISIBLE) {
            lLeafChild.setVisibility(VISIBLE);
        }

        markDepthActiveOrDeactive(lLeafChild, true);


        if (!mIsWizard) {
            if (super.getChildCount() > 1) {

                getChildAt(super.getChildCount() - 2).setVisibility(VISIBLE);
                markDepthActiveOrDeactive(getChildAt(super.getChildCount() - 2), false);
            }

        }

        //update the root
        if (getChildCount() > MAX_VISIBLE_DEPTH_COUNT) {
            showDotsonRoot(true);
        } else {
            showDotsonRoot(false);
        }

    }

    private void loadLayout(Context context) {

        this.setOrientation(HORIZONTAL);

        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        this.setGravity(Gravity.TOP);

        this.setLayoutTransition(transitioner);
        defaultAppearingAnim = transitioner.getAnimator(LayoutTransition.APPEARING);
        defaultChangeAppearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_APPEARING);
        defaultDisappearingAnim = transitioner.getAnimator(LayoutTransition.DISAPPEARING);
        defaultChangeDisappearingAnim = transitioner.getAnimator(LayoutTransition.CHANGE_DISAPPEARING);


        transitioner.setDuration(100);
        transitioner.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.APPEARING, 100);
        transitioner.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);

        setDefaultAniamtion();


    }


    @Override
    public void addView(View v)

    {
        if (super.getChildCount() >= MAX_VISIBLE_DEPTH_COUNT) {
            super.getChildAt(getChildCount() - (MAX_VISIBLE_DEPTH_COUNT)).setVisibility(GONE);
        }

        if (mIsWizard && getChildCount() == 1) {
            markDepthActiveOrDeactive(getChildAt(0), false);
        }
        super.addView(v);
        activateCurrentDepth();
    }


    /**
     * AN-64998 removed click handling in Backtrack.
     *
     * @param v
     */
    public void handleClicksfromBT(View v) {

    }

    /**
     * collpases the Back trace to the needs of Npanelbrowser - Maximize and collapse states
     */
    void collapse(boolean collapse) {

        if (mCurrentState == BACKTRACE_NORMAL) {
            if (getChildCount() > 1) {
                mCurrentState = BACKTRACE_COLLAPSED;
            }
        }

    }

    /**
     * '
     * used to restore the Backtrace to normal
     * called from Npanel browser
     */
    void restore() {

        if (mCurrentState == BACKTRACE_COLLAPSED) {

            moveToPreviousDepth();
            mCurrentState = BACKTRACE_NORMAL;
        }

    }


    void moveToPreviousDepth() {

        //Scroll to previous depth if we aren't on root
        if (getChildCount() > (MAX_VISIBLE_DEPTH_COUNT - 2)) {
            View remView = super.getChildAt(super.getChildCount() - 1);
            super.removeView(remView);
            freeViews.add(remView);
            if (getChildCount() > 0) {
                activateCurrentDepth();
            }

        }

    }

    /**
     * resets the backtrace to the root
     */
    void reset() {
        removeAndaddViewstoFreeViews(0);

    }

    /**
     * Convinience method for changing VD for ROOT
     *
     * @param show if its expandable then
     * @author manjunatha.sg
     */
    private void showDotsonRoot(boolean show) {
        if (show) {
            if (mIsWizard) {
                ((VerticalText) super.getChildAt(getChildCount() - (MAX_VISIBLE_DEPTH_COUNT))).setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.npanel_dot_icon));
            } else {
                ((VerticalText) super.getChildAt(getChildCount() - (MAX_VISIBLE_DEPTH_COUNT))).setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.npanel_dot_icon));
                ((VerticalText) super.getChildAt(getChildCount() - (MAX_VISIBLE_DEPTH_COUNT - 1))).setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.drawable_text_view));
            }
        } else {
            if (mIsWizard) {
                ((VerticalText) super.getChildAt(0)).setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.drawable_text_view));
            }
            ((VerticalText) super.getChildAt(getChildCount() - 1)).setCompoundDrawables(null, null, null, getResources().getDrawable(R.drawable.drawable_text_view));
        }

    }

    /**
     * @param start , starting index from which all views to be removed
     * @author manjunatha.sg
     */
    private void removeAndaddViewstoFreeViews(int start) {
        while (getChildCount() > start) {

            View addToFreeView = getChildAt(getChildCount() - 1);
            removeView(addToFreeView);
            freeViews.add(addToFreeView);

        }
    }

    View getFreeView() {
        if (freeViews.size() > 0) {
            View v = freeViews.remove(0);
            v.setVisibility(VISIBLE);
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.vertical_text_view, null);
    }

    interface BackTraceListener {
        ViewGroup.LayoutParams getLayoutData();

        void restore();

        void onDepthClicked(int depth);

        void onBackTraceExpanded();

        boolean getBacktraceType();

        boolean isCompactWizard();
    }


    void setBackTraceListener(BackTraceListener listener) {
        mIsWizard = listener.getBacktraceType();
        if (mIsWizard) {
            MAX_VISIBLE_DEPTH_COUNT = 1;
        } else {
            MAX_VISIBLE_DEPTH_COUNT = 2;
        }

    }

    void clear() {
        removeAndaddViewstoFreeViews(0);
    }

    public void refreshUI() {
        for (int i = 0; i < getChildCount(); i++) {
            VerticalText view = ((VerticalText) getChildAt(i));
            int stringId = ((VerticalText) getChildAt(i)).getStringResourceId();
            if (stringId != 0 && stringId != -1) {
                view.setText(getResources().getString(stringId));
            }
            view.invalidate();
        }
    }
}
