package ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import fany.phpuijar.R;
import ui.utils.FPSUtil;

public class NPanelBrowser extends LinearLayout {
    private int noOfPanels;
    private BackTrace bkTrace;
    private String TAG = "NpanelBrowser";
    private Panel p1, p2, p3;
    private int btDepth, depth;

    private int firstPanelWidth;
    private LayoutParams lp1, lp2;

    private NPanelBrowserListener brzrListner = null;
    private NPanelBrowserAnimationListener mAnimationListener = null;
    private NPanelBrowserRootClickListener mNPanelBrowserRootClickListener = null;

    private boolean mIsWizard, mIsCompactWizard, mIsOptions;
    private ViewGroup.LayoutParams lp;
    private int mBackTraceHeight, mPanelHeight, mPanelHeightWizard;

    private Context mContext = null;
    private NPanelBrowser mNPanelBrowser = null;
    private ForwardAnimation mForwardAnimation = null;
    private BackwardAnimation mbackwardAnimation = null;
    private CollapseAnimation mCollapseAnimation = null;
    private RestoreAnimation mRestoreAnimation = null;
    private boolean mEnableAnimation = true;
    private boolean mMoveToPreviousPage = false;
    private float mPanel1Width;

    public NPanelBrowser(Context context) {
        this(context, null, 0);
    }

    public static final int VIEWSTATE_NORMAL = 0;

    public static final int VIEWSTATE_COLLAPSED = 1;

    public static final int VIEWSTATE_MAXIMIZED = 2;
    private int mCurrentViewState = VIEWSTATE_NORMAL;

    private NPanelBrowserViewStateListener mViewStateListner;

    public NPanelBrowser(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NPanelBrowser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "in constructor");
        mContext = context;
        mNPanelBrowser = this;
        depth = 0;
        btDepth = depth - 1;
        loadLayout(context);
        loadAttributes(context, attrs, defStyle);
        setPadding(getResources().getDimensionPixelOffset(R.dimen.npanel_rootlevel_padding_left_width), 0, 0, 0);
        mForwardAnimation = new ForwardAnimation();
        mbackwardAnimation = new BackwardAnimation();
        mCollapseAnimation = new CollapseAnimation();
        mRestoreAnimation = new RestoreAnimation();

        // add callbacks to the listener
        bkTrace.setBackTraceListener(new BackTrace.BackTraceListener() {

            public void onDepthClicked(int depth) {
                selectDepth(depth);
            }

            public void onBackTraceExpanded() {
                // called if clicked on root when bactrace is in Normal Mode
                handleBkTrcExpansion();
            }

            @Override
            public ViewGroup.LayoutParams getLayoutData() {
                // TODO Auto-generated method stub
                ViewGroup.LayoutParams lp = getLayoutParams();
                return lp;
            }

            @Override
            public void restore() {
                // TODO Auto-generated method stub

                restoreWithResult();

            }

            @Override
            public boolean getBacktraceType() {
                // TODO Auto-generated method stub
                return mIsWizard;
            }

            @Override
            public boolean isCompactWizard() {
                // TODO Auto-generated method stub
                return mIsCompactWizard;
            }
        });

        Log.i(TAG, "leaving constructor");
    }

    private void loadLayout(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.npanel_browser_layout, this);
        bkTrace = (BackTrace) findViewById(R.id.backTrace1);
        p1 = (Panel) findViewById(R.id.panel1);
        p2 = (Panel) findViewById(R.id.panel2);
        p3 = (Panel) findViewById(R.id.panel3);
        Log.d("TPVision", "========= loadLayout===============");
    }

    private void handleBkTrcExpansion() {
        if (mNPanelBrowserRootClickListener != null) {
            mNPanelBrowserRootClickListener.onRootExpand();
        }
        mCurrentViewState = VIEWSTATE_NORMAL;
        Log.d("TPVision", "========= handleBkTrcExpansion===============");
        if (noOfPanels == 1) {
            super.getChildAt(1).setVisibility(GONE);
        } else if (noOfPanels == 2) {
            if (!isEnableAnimation()) {
                super.getChildAt(1).setVisibility(GONE);
                super.getChildAt(2).setVisibility(GONE);
            } else {
                mCollapseAnimation.onAnimStart();
            }
        }

    }

    private void selectDepth(int depth) {
        Log.d("TPVision", "========= Select Depth===============");
        btDepth = depth + 1;
        this.depth = btDepth + 1;
        if (mNPanelBrowserRootClickListener != null) {
            mNPanelBrowserRootClickListener.onRootCollapse(btDepth);
        }
        if (mIsWizard) {
            btDepth = btDepth + 1;
        }

        if (noOfPanels == 1) {
            Panel p = (Panel) (super.getChildAt(1));
            p.removeViewAt(0);
            brzrListner.setDepth(this.depth);
            p.addView(brzrListner.getPanelView(this.depth));
            p.setVisibility(VISIBLE);
        } else if (noOfPanels == 2) {
            Panel p = (Panel) (super.getChildAt(1));
            Panel p1 = (Panel) (super.getChildAt(2));
            View removedView = p.getChildAt(0);
            /*
			* Issue fix for  AN-66148, doing a null check
			*/
            if (removedView != null) {
                p.removeViewAt(0);
            }
            if (brzrListner instanceof NPanelBrowserExtnListener) {
                Log.d("NPanel", "viewRemoved: " + removedView);
                ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
            }

            removedView = p1.getChildAt(0);
			/*
			 * Issue fix for  AN-66148, doing a null check
			 */
            if (removedView != null) {
                p1.removeViewAt(0);
            }
            if (brzrListner instanceof NPanelBrowserExtnListener) {
                Log.d("NPanel", "viewRemoved: " + removedView);
                ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
            }

            brzrListner.setDepth(this.depth);

            p.addView(brzrListner.getPanelView(this.depth));

            p1.addView(brzrListner.getPanelView(this.depth + 1));
            if (!isEnableAnimation()) {
                p.setVisibility(VISIBLE);
                p1.setVisibility(VISIBLE);
            } else {
                mRestoreAnimation.aninStart();
            }
        }
    }

    private void loadAttributes(Context context, AttributeSet attrs, int defStyle) {
        Log.d("TPVision", "========= Load Attribute ===============");
        if (attrs != null) {
            if (attrs.getStyleAttribute() == R.style.PanelBrowserContentExplorer_Wizards
                    || attrs.getStyleAttribute() == R.style.PanelBrowserContentExplorer_CompactWizards) {
                mIsWizard = true;
            } else {
                mIsWizard = false;
            }

            if (attrs.getStyleAttribute() == R.style.PanelBrowserContentExplorer_CompactWizards) {
                mIsCompactWizard = true;
            } else {
                mIsCompactWizard = false;
            }

            if (attrs.getStyleAttribute() == R.style.PanelBrowserContentExplorer_Options) {
                mIsOptions = true;
            } else {
                mIsOptions = false;
            }

            TypedArray attrbs = context.obtainStyledAttributes(attrs, R.styleable.PanelBrowser, defStyle, 0);
            if (attrbs.getIndexCount() == 0) {
                int defaultAttrs[] = {R.attr.nPbEnableAnimation, R.attr.nPbFirstPanelWidth, R.attr.nPbNoOfVisiblePanels};
                attrbs = context.obtainStyledAttributes(R.style.PanelBrowserContentExplorer, defaultAttrs);
            }
            mEnableAnimation = attrbs.getBoolean(R.styleable.PanelBrowser_nPbEnableAnimation, true);
            setEnableAnimation(mEnableAnimation);
            noOfPanels = attrbs.getInt(R.styleable.PanelBrowser_nPbNoOfVisiblePanels, -1);
            if (noOfPanels == 0) {
                attrbs.recycle();
                return;
            }

            try {
                firstPanelWidth = attrbs.getDimensionPixelSize(R.styleable.PanelBrowser_nPbFirstPanelWidth, -1);
            } catch (Exception e) {
                Log.d("TPVision", "Exception: " + e.getMessage());
            }

            if (attrs.getStyleAttribute() == R.style.PanelBrowserContentExplorer_CompactWizards) {
                mBackTraceHeight = getResources().getDimensionPixelSize(
                        R.dimen.wizard_compact_step02_panels_npanel_compact_wizard_bg_height);
                bkTrace.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mBackTraceHeight));
                mPanelHeight = mBackTraceHeight;
            } else {
                mPanelHeight = getResources().getDimensionPixelSize(R.dimen.npanel_firstlevel_panel01_height);
                mPanelHeightWizard = getResources().getDimensionPixelSize(R.dimen.panel_height_wizard);
            }
            if (noOfPanels == 1) {
                super.removeViewAt(3);
                p2.setVisibility(GONE);
                p3 = null;

                if (firstPanelWidth == 0) {
                    lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, mPanelHeight);
                } else {
                    lp1 = new LayoutParams(firstPanelWidth, mPanelHeight);
                }
                if (mIsWizard && !mIsCompactWizard) {
                    LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                            mPanelHeightWizard);
                    p1.setLayoutParams(layoutParam);
                    p2.setLayoutParams(layoutParam);
                } else {
                    p1.setLayoutParams(lp1);
                    p2.setLayoutParams(lp1);
                }
            } else if (noOfPanels == 2) {
                lp1 = new LayoutParams(firstPanelWidth, mPanelHeight);
                lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, mPanelHeight);
                if (mIsWizard && !mIsCompactWizard) {
                    LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                            mPanelHeightWizard);
                    p1.setLayoutParams(layoutParam);
                    p2.setLayoutParams(layoutParam);
                } else {
                    p1.setLayoutParams(lp1);
                    p2.setLayoutParams(lp2);
                }

                p1.setVisibility(VISIBLE);
                p2.setVisibility(VISIBLE);
                p3.setVisibility(GONE);
            }
            attrbs.recycle();
        }
    }

    /**
     * Returns animation is enabled or not
     */
    public boolean isEnableAnimation() {
        return mEnableAnimation;
    }

    /**
     * Sets whether animation is enabled.
     *
     * @param mEnableAnimation Set true to enable it.
     */
    public void setEnableAnimation(boolean mEnableAnimation) {
        this.mEnableAnimation = mEnableAnimation;
    }

    /**
     * This function is used to set number of visible panels
     *
     * @param panelCount No. of visible panels
     */

    public void setnPbNoOfVisiblePanels(int panelCount) {
        noOfPanels = panelCount;
        if (noOfPanels < 2) {
            super.removeViewAt(2);
        }
    }

    /**
     * Returns the number of visible panels
     */
    public int getnPbNoOfVisiblePanels() {
        return noOfPanels;
    }

    /**
     * Scrolls to next level
     */
    public void scrollNextPage() {

        loadNextPanel();

    }

    /**
     * Scrolls to previous level
     */
    public void scrollPrevPage() {

        loadPrevPanel();
    }

    private boolean loadPrevPanel() {
        boolean retFromApp = true;
        Log.d("TPVision", "========= Load Penel===============");
        retFromApp = brzrListner.isFocussable(depth - 1);

        if (retFromApp) {
            if (noOfPanels == 1) {
                if (depth - 1 >= 0) {
                    depth--;
                    btDepth--;
                    mMoveToPreviousPage = false;
                    if (bkTrace.getChildCount() == 0) {
                        bkTrace.setVisibility(GONE);
                    }
                    if (!isEnableAnimation()) {
                        bkTrace.moveToPreviousDepth();
                        ((ViewGroup) super.getChildAt(2)).addView(brzrListner.getPanelView(depth));
                        if (((ViewGroup) super.getChildAt(1)).getChildCount() > 0) {
                            ((ViewGroup) super.getChildAt(1)).removeViewAt(0);
                        }
                        View v = super.getChildAt(2);
                        super.removeView(v);
                        super.addView(v, 1);
                        v.setVisibility(VISIBLE);
                        ((ViewGroup) super.getChildAt(2)).setVisibility(GONE);
                        v.requestFocus();
                        brzrListner.setDepth(depth);
                    } else {
                        Log.d(TAG, "loadPrevPanel(), depth" + depth);
                        if (mForwardAnimation != null && mForwardAnimation.isForwardAnimationRunning()) {
                            mForwardAnimation.animEnd();
                        }
                        mbackwardAnimation.animEnd();
                        mbackwardAnimation.aninmStart();

                    }
                }

            } else if (noOfPanels == 2) {
                if (restoreWithResult()) {

                    return true;
                }
                if (depth - 1 >= 0) {
                    depth--;
                    btDepth--;
                    bkTrace.moveToPreviousDepth();
                    if (bkTrace.getChildCount() == 0) {
                        bkTrace.setVisibility(GONE);
                    }
                    if (!isEnableAnimation()) {
                        ViewGroup v = (ViewGroup) super.getChildAt(2);
                        View removedView = v.getChildAt(0);
                        if (v.getChildCount() > 0) {
                            v.removeViewAt(0);
                        }
                        if (brzrListner instanceof NPanelBrowserExtnListener) {
                            Log.d("NPanel", "viewRemoved: " + removedView);
                            ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
                        }

                        v.setVisibility(GONE);

                        ViewGroup panel2 = (ViewGroup) super.getChildAt(3);
                        super.removeView(panel2);
                        panel2.addView(brzrListner.getPanelView(depth));
                        panel2.setVisibility(VISIBLE);
                        super.addView(panel2, 1);
                        panel2.requestFocus();

                        View v1 = super.getChildAt(1);
                        View v2 = super.getChildAt(2);
                        v1.setLayoutParams(lp1);
                        v2.setLayoutParams(lp2);

                        brzrListner.setDepth(depth);
                    } else {
                        if (mForwardAnimation != null && mForwardAnimation.isForwardAnimationRunning()) {
                            mForwardAnimation.animEnd();
                        }
                        mbackwardAnimation.animEnd();
                        mPanel1Width = (float) ((ViewGroup) super.getChildAt(1)).getWidth();
                        mbackwardAnimation.aninmStart();
                    }

                }
            }
            // to fix the controllability issues
            return true;
        } else {
            return true;
        }

    }

    /**
     * Returns view based on panel index
     *
     * @param index panel index
     */
    public View getPanel(int index) {
        if (index < (noOfPanels + 1)) {
            return super.getChildAt(index + 1);
        } else {
            return null;
        }
    }

    /**
     * Called to process key events. NPB overrides dispatchKeyEvent to handle dpad left key, dpad right key, dpad up key and dpad down key. NPB consumes long press DPAD right key and long press DPAD left key events
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        boolean ret = super.dispatchKeyEvent(event);

        Log.i(TAG, "in dispatchKeyEvent " + event.getAction() + " " + ret);

        int keyCode = event.getKeyCode();
        int keyAction = event.getAction();

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if (keyAction == KeyEvent.ACTION_DOWN && mCurrentViewState == VIEWSTATE_NORMAL && event.getRepeatCount() == 0) {
                    if (!ret) {
                        if (isEnableAnimation() && (mForwardAnimation.isForwardAnimationRunning() || mbackwardAnimation.isBackwardAnimationRunning())) {
                            return true;
                        }
                        return HandleRightKey();
                    } else {
                        return ret;
                    }
                } else if (keyAction == KeyEvent.ACTION_DOWN && event.getRepeatCount() > 0) {
                    return true;
                } else if (keyAction == KeyEvent.ACTION_UP) {
                    return ret;
                }
            }
            break;

            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if (keyAction == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    if (!ret) {
                        if (isEnableAnimation() && (mbackwardAnimation.isBackwardAnimationRunning() || mForwardAnimation.isForwardAnimationRunning())) {
                            return true;
                        }
                        return HandleLeftKey();
                    } else {
                        return ret;
                    }
                } else if (keyAction == KeyEvent.ACTION_UP) {
                    return ret;
                }
            }
            break;

            case KeyEvent.KEYCODE_DPAD_UP: {

                if (keyAction == KeyEvent.ACTION_DOWN && noOfPanels == 2) {
                    View nextView = focusSearch(View.FOCUS_FORWARD);
                    if (nextView != null
                            && getPanelIndex(nextView) != -1
                            && getPanelIndex(nextView) != getPanelIndex(getFocusedChild())) {
                        return true;
                    }
                }
            }
            break;

            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (keyAction == KeyEvent.ACTION_DOWN && noOfPanels == 2) {
                    View nextView = focusSearch(View.FOCUS_FORWARD);
                    if (nextView != null
                            && getPanelIndex(nextView) != -1
                            && getPanelIndex(nextView) != getPanelIndex(getFocusedChild())) {
                        return true;
                    }
                }
            }
            break;
        }
        return ret;
    }

    private boolean HandleRightKey() {
        return loadNextPanel();
    }

    private boolean loadNextPanel() {
        Log.d("TPVision", "========= Load next panel===============");
        boolean retFromApp = true;
        if (brzrListner != null) {
            retFromApp = brzrListner.isFocussable(depth + 1);
        }
        Log.v(TAG, "is focusable " + retFromApp);

        if (retFromApp) {
            if (noOfPanels == 1 && brzrListner != null) {
                if (brzrListner.MoveToNextPage()) {
                    View bv = brzrListner.getBacktraceView(++btDepth, bkTrace.getFreeView());
                    if (mIsWizard) {
                        bv.setTag(Integer.valueOf(btDepth - 1));
                    } else {
                        bv.setTag(Integer.valueOf(btDepth));
                    }

                    bkTrace.addView(bv);
                    depth++;
                    if (!isEnableAnimation()) {
                        ((ViewGroup) super.getChildAt(2)).addView(brzrListner.getPanelView(depth));
                        if (((ViewGroup) super.getChildAt(1)).getChildCount() > 0) {
                            ((ViewGroup) super.getChildAt(1)).removeViewAt(0);
                        }
                        View v = super.getChildAt(1);
                        super.removeView(v);
                        v.setVisibility(GONE);
                        super.addView(v, 2);
                        super.getChildAt(1).setVisibility(VISIBLE);
                        super.getChildAt(1).requestFocus();
                        brzrListner.setDepth(depth);
                    } else {
                        if (mbackwardAnimation != null && mbackwardAnimation.isBackwardAnimationRunning()) {
                            mbackwardAnimation.animEnd();
                        }
                        mForwardAnimation.animEnd();
                        mForwardAnimation.aninmStart();
                    }
                }

            } else if (noOfPanels == 2 && brzrListner != null) {

                boolean nextPage = brzrListner.MoveToNextPage();
                Log.v(TAG, "move to next page " + nextPage);

                if (nextPage) {

                    View bv = brzrListner.getBacktraceView(++btDepth, bkTrace.getFreeView());

                    bv.setTag(Integer.valueOf(btDepth));

                    bkTrace.addView(bv);
                    bkTrace.setVisibility(VISIBLE);
                    depth++;
                    if (!isEnableAnimation()) {
                        ViewGroup v = (ViewGroup) super.getChildAt(1);
                        View removedView = v.getChildAt(0);
                        if (v.getChildCount() > 0) {
                            v.removeViewAt(0);
                        }
                        if (brzrListner instanceof NPanelBrowserExtnListener) {
                            Log.d("NPanel", "viewRemoved: " + removedView);
                            ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
                        }

                        v.setVisibility(GONE);
                        super.removeViewAt(1);
                        super.addView(v, 3);
                        ViewGroup panel2 = (ViewGroup) super.getChildAt(2);
                        panel2.addView(brzrListner.getPanelView(depth + 1));
                        panel2.setVisibility(VISIBLE);

                        View v1 = super.getChildAt(1);
                        View v2 = super.getChildAt(2);
                        v1.setLayoutParams(lp1);
                        v2.setLayoutParams(lp2);
                        brzrListner.setDepth(depth);
                    } else {
                        if (mbackwardAnimation != null && mbackwardAnimation.isBackwardAnimationRunning()) {
                            mbackwardAnimation.animEnd();
                        }
                        mForwardAnimation.animEnd();
                        mForwardAnimation.aninmStart();
                    }

                    if (brzrListner instanceof NPanelBrowserExtnListener) {
                        return true;
                    }
                } else {

                    if (brzrListner instanceof NPanelBrowserExtnListener) {
                        ((NPanelBrowserExtnListener) brzrListner).setFocus(depth + 1);
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }

    }

    private boolean HandleLeftKey() {
        return loadPrevPanel();
    }

    /**
     * Register a callback to be invoked to load NPanelBrowser
     */
    public void setNPanelBrowserListner(NPanelBrowserListener listener) {

        brzrListner = listener;
        loadNPanelBrowser();
    }

    public void setNPanelBrowserListner(NPanelBrowserExtnListener listener) {
        brzrListner = listener;
        loadNPanelBrowser();
    }

    private void loadNPanelBrowser() {
        Log.d("TPVision", "========= Load NPanel Browser===============");
        setShowDividers(SHOW_DIVIDER_MIDDLE);

        if (mIsWizard) {
            setDividerDrawable(getContext().getResources().getDrawable(R.drawable.wizards_stack_divider_line));

        } else {
            setDividerDrawable(getContext().getResources().getDrawable(R.drawable.npanel_verticaldivider_line));
        }

        if (brzrListner != null) {
            depth = brzrListner.getDepth();

            if (depth > 0) {
                btDepth = depth - 1;
                for (int looper = 0; looper < depth; looper++) {
                    View v = brzrListner.getBacktraceView(looper, bkTrace.getFreeView());
                    v.setTag(looper);
                    bkTrace.addView(v);
                    bkTrace.setVisibility(VISIBLE);
                }
            } else {
                if (mIsWizard) {
                    View v = brzrListner.getBacktraceView(++btDepth, bkTrace.getFreeView());
                    v.setTag(Integer.valueOf(btDepth - 1));
                    bkTrace.addView(v);
                    bkTrace.setTag(Integer.valueOf(btDepth));
                    bkTrace.setVisibility(VISIBLE);
                } else if (mIsOptions) {
                    View v = brzrListner.getBacktraceView(btDepth, bkTrace.getFreeView());
                    v.setTag(btDepth);
                    bkTrace.addView(v);
                    bkTrace.setVisibility(VISIBLE);
                }
            }


            if (noOfPanels == 1) {
                p1.addView(brzrListner.getPanelView(depth));
            } else if (noOfPanels == 2) {
                p1.addView(brzrListner.getPanelView(depth));
                p2.addView(brzrListner.getPanelView(depth + 1));
            }
        }
    }

    /**
     * Checked the state to update second panel. If No of visible panels is two and panel index is one, removes all views from second panel and returns true.
     */
    public boolean isNextPanelUpdate(View v) {
        if (getPanelIndex(v) == 1 && getnPbNoOfVisiblePanels() == 2) {

            ((ViewGroup) super.getChildAt(2)).removeAllViews();
            return true;
        }
        return false;
    }

    /**
     * Update second panel.
     */
    public void updateNextPanel() {

        ((ViewGroup) super.getChildAt(2)).addView(brzrListner.getPanelView(depth + 1));
    }

    /**
     * Update panel 2 with view passed by application and application gets a callback with removedView as parameter
     */
    public void updatePanel2(View view) {
        Log.d("TPVision", "========= Update Panel2===============");
        Panel lPanel2 = (Panel) super.getChildAt(2);
        if (lPanel2 != null) {
            View removedView = lPanel2.getChildAt(0);
            lPanel2.removeAndDetachChildViews();
            if (view != null) {
                lPanel2.addView(view, 0);
            }
            if (brzrListner instanceof NPanelBrowserExtnListener) {
                Log.d("NPanel", "viewRemoved: " + removedView);
                ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
            }
        }
    }

    /**
     * Update panel 1 with view passed by application and application gets a callback with removedView as parameter
     */
    public void updatePanel1(View view) {
        Log.d("TPVision", "========= Update Panel 1===============");
        Panel lPanel1 = (Panel) super.getChildAt(1);
        if (lPanel1 != null) {
            View removedView = lPanel1.getChildAt(0);
            lPanel1.removeAndDetachChildViews();
            if (view != null) {
                lPanel1.addView(view, 0);
            }
            if (brzrListner instanceof NPanelBrowserExtnListener) {
                Log.d("NPanel", "viewRemoved: " + removedView);
                ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
            }
        }
    }

    /**
     * Collapse the NPanelBrowser.Applicable for 2 panel browser make first panel visibility GONE and sets NPanelBrowser width to WRAP_CONTENT.
     */

    public void collapse() {
        Log.d("TPVision", "========= Collapse===============");
        if (mCurrentViewState == VIEWSTATE_NORMAL && getnPbNoOfVisiblePanels() == 2) {

            // first get the backtrace view and add to it
            View bv = brzrListner.getBacktraceView(++btDepth, bkTrace.getFreeView());
            bv.setTag(Integer.valueOf(btDepth));
            bkTrace.addView(bv);

            // then make the backtrace has collapsed
            bkTrace.collapse(true);

            // Change the layout params
            lp = getLayoutParams();
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = getHeight();
            setLayoutParams(lp);

            super.getChildAt(1).setVisibility(GONE);
            mCurrentViewState = VIEWSTATE_COLLAPSED;

            if (mViewStateListner != null) {
                mViewStateListner.onViewStateChanged(VIEWSTATE_COLLAPSED);
            }
        }
    }

    /**
     * This function is the reverse of minimize. By default, the browser will
     * handle it internally to maximize when necessary but the application as
     * well can maximize it when necessary.
     */

    public void restore() {

        // TODO check for the need for Restoring
        restoreWithResult();
    }

    /**
     * @return true if restored else false
     */
    private boolean restoreWithResult() {
        Log.d("TPVision", "========= Restore with result===============");
        if (mCurrentViewState != VIEWSTATE_NORMAL) {

            // decrease the current depth
            btDepth--;

            // restore the Previous panel
            super.getChildAt(1).setVisibility(VISIBLE);
            super.getChildAt(1).requestFocus();

            if (mCurrentViewState == VIEWSTATE_COLLAPSED) {
                lp = getLayoutParams();

                lp.width = LayoutParams.WRAP_CONTENT;
                lp.height = getHeight();

                setLayoutParams(lp);

            }

            // restore the backtrace
            bkTrace.restore();

            mCurrentViewState = VIEWSTATE_NORMAL;
            if (mViewStateListner != null) {
                mViewStateListner.onViewStateChanged(VIEWSTATE_NORMAL);
            }
            return true;

        }
        return false;
    }

    /**
     * Resets the browser and brings back to the root
     */

    public void reset() {
        Log.d("TPVision", "======== RESET =========");
        if (mForwardAnimation != null && mForwardAnimation.isForwardAnimationRunning()) {
            mForwardAnimation.animEnd();
        }
        if (mbackwardAnimation != null && mbackwardAnimation.isBackwardAnimationRunning()) {
            mbackwardAnimation.animEnd();
        }
        mCurrentViewState = VIEWSTATE_NORMAL;
        bkTrace.reset();
        if (bkTrace.getChildCount() == 0) {
            bkTrace.setVisibility(GONE);
        }
        selectDepth(-2);
    }

    /**
     * Clears all the views.
     */
    public void clear() {
        bkTrace.clear();
        if (bkTrace.getChildCount() == 0) {
            bkTrace.setVisibility(GONE);
        }

        depth = 0;
        btDepth = -1;
        for (int i = 1; i < getChildCount(); i++) {
            Panel p = (Panel) getChildAt(i);

            p.removeAndDetachChildViews();
        }

        if (noOfPanels == 1) {
            p1 = (Panel) getChildAt(1);
            p2 = (Panel) getChildAt(2);
        } else {
            p1 = (Panel) getChildAt(1);
            p2 = (Panel) getChildAt(2);
            p3 = (Panel) getChildAt(3);

        }

    }

    /**
     * Expand the NPanelBrowser.Applicable for 2 panel browser make first panel visibility GONE and second panel takes full width.
     */
    public void maximize() {
        Log.d("TPVision", "========= Maximize===============");
        if (mCurrentViewState == VIEWSTATE_NORMAL && getnPbNoOfVisiblePanels() == 2) {

            View bv = brzrListner.getBacktraceView(++btDepth, bkTrace.getFreeView());
            bv.setTag(Integer.valueOf(btDepth));
            bkTrace.addView(bv);
            bkTrace.collapse(false);
            super.getChildAt(1).setVisibility(GONE);

            mCurrentViewState = VIEWSTATE_MAXIMIZED;

            if (mViewStateListner != null) {
                mViewStateListner.onViewStateChanged(VIEWSTATE_MAXIMIZED);
            }
        }
    }


    /**
     * Returns panel index
     *
     * @param v View added to panel
     */
    public int getPanelIndex(View v) {
        Log.d("TPVision", "========= getPanel Index===============");
        Rect viewRect = new Rect();
        v.getGlobalVisibleRect(viewRect);
        if (noOfPanels == 1) {
            Rect panel1Rect = new Rect();
            super.getChildAt(1).getGlobalVisibleRect(panel1Rect);
            if (panel1Rect.contains(viewRect)) {
                Log.i(TAG, "in getPanelIndex: PanelIndex: 0");
                return 0;
            }
        } else if (noOfPanels == 2) {

            Rect panel1Rect = new Rect();
            super.getChildAt(1).getGlobalVisibleRect(panel1Rect);
            Rect panel2Rect = new Rect();
            super.getChildAt(2).getGlobalVisibleRect(panel2Rect);
            if (panel1Rect.contains(viewRect)) {
                Log.i(TAG, "in getPanelIndex: PanelIndex: 1");
                return 1;
            } else if (panel2Rect.contains(viewRect)) {
                Log.i(TAG, "in getPanelIndex: PanelIndex: 2");
                return 2;
            }
        }
        return -1;
    }

    /**
     * Interface definition for a callback to be invoked for getting views from application to add to backTrace and panels.
     */
    public interface NPanelBrowserListener {
        /**
         * Called to get panel view
         *
         * @param index depth
         */
        View getPanelView(int index);

        /**
         * Called to get backtrace view
         */
        View getBacktraceView(int depth, View v);

        /**
         * Returns isFocussable is true or false.Checks for controllabilty
         */
        boolean isFocussable(int index);

        /**
         * Returns MoveToNextPage is true or false.Checks for leaf node
         */
        boolean MoveToNextPage();

        /**
         * Sets depth
         */
        void setDepth(int depth);

        /**
         * Gets depth
         */
        int getDepth();
    }

    public interface NPanelBrowserExtnListener extends NPanelBrowserListener {
        /**
         * Set focus to panel
         *
         * @param index Panel index
         */
        void setFocus(int index);

        /**
         * Called whenever view is removed
         *
         * @param view Removed view
         */
        void viewRemoved(View view);
    }

    public interface NPanelBrowserViewStateListener {
        void onViewStateChanged(int viewState);
    }

    public interface NPanelBrowserAnimationListener {
        void onAnimationEnd();

        void onAnimationStart();
    }

    public interface NPanelBrowserRootClickListener {
        void onRootExpand();

        void onRootCollapse(int depth);
    }

    public void setNPanelBrowserRootClickListener(NPanelBrowserRootClickListener listener) {
        mNPanelBrowserRootClickListener = listener;
    }

    public void setAnimationListener(NPanelBrowserAnimationListener listener) {
        mAnimationListener = listener;
    }

    // added for Eaccessibilty
    @Override
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        event.setClassName(getClass().getName());
        return super.requestSendAccessibilityEvent(child, event);
    }

    /**
     * Register a callback to be invoked whenever  NPB state changes to Normal, collapse, and maximize.
     */
    public void setViewStateChangeListener(NPanelBrowserViewStateListener lisner) {
        mViewStateListner = lisner;
    }

    /**
     * Class implements forward animation with animation listner
     */
    class ForwardAnimation implements AnimatorListener {
        private Animator mParentForwardSliding1, mParentForwardSliding2;
        private FPSUtil mFPSUtilSlide1, mFPSUtilSlide2;

        public ForwardAnimation() {

            mParentForwardSliding1 = (Animator) AnimatorInflater
                    .loadAnimator(mContext, R.anim.forward_animation_slide1);
            mParentForwardSliding2 = mParentForwardSliding1.clone();

            mParentForwardSliding2.addListener(this);
            mParentForwardSliding1.addListener(this);

            mFPSUtilSlide1 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "Forward Slide Animation 1");
            mFPSUtilSlide2 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "Forward Slide Animation 1");

        }

        public boolean isForwardAnimationRunning() {
            if (mParentForwardSliding1 != null && mParentForwardSliding2 != null) {
                return mParentForwardSliding1.isRunning() || mParentForwardSliding2.isRunning();
            }
            return false;
        }

        /**
         * method for stating the animation
         */
        void aninmStart() {
            Log.d("Npanel", "  aninmStart()");
            final ViewGroup v2 = (ViewGroup) mNPanelBrowser.getChildAt(2);
            v2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mParentForwardSliding1.setTarget(v2);
            Log.d("1DrawStat", "mParentForwardSliding1 start");
            mFPSUtilSlide1.setView(v2, mParentForwardSliding1);

            if (noOfPanels == 2) {
                final ViewGroup v3 = (ViewGroup) mNPanelBrowser.getChildAt(3);
                v3.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mParentForwardSliding2.setTarget(v3);

                Log.d("1DrawStat", "mParentForwardSliding2 start");
                mFPSUtilSlide2.setView(v3, mParentForwardSliding2);
            }
            setupViews();
        }

        /**
         * if animation is playing while starting the same, during fast
         * navigation
         */
        void animEnd() {


            if (mParentForwardSliding1 != null && mParentForwardSliding1.isRunning()) {
                mParentForwardSliding1.end();

            }
            if (mParentForwardSliding2 != null && mParentForwardSliding2.isRunning()) {
                mParentForwardSliding2.end();

            }
        }

        /**
         * changing the position of the views
         */
        void removeandAddView() {
            ViewGroup v = (ViewGroup) mNPanelBrowser.getChildAt(1);
            mNPanelBrowser.removeViewAt(1);
            if (noOfPanels == 1) {
                mNPanelBrowser.addView(v, 2);
                // mNPanelBrowser.getChildAt(1).requestFocus();// commented due
                // to focus related

            } else if (noOfPanels == 2) {
                mNPanelBrowser.addView(v, 3);
            }

        }

        /**
         * setting three panels before starting the animation
         */
        void setupViews() {
            Log.d("TPVision", "========= Setup View===============");
            View v1 = mNPanelBrowser.getChildAt(1);
            v1.setBackground(null);
            v1.setVisibility(GONE);

            if (noOfPanels == 1) {
                Panel p2 = (Panel) getChildAt(2);
                p2.setVisibility(VISIBLE);

                View addedView = brzrListner.getPanelView(depth);
                if (addedView != null && addedView.getParent() != null) {
                    ((ViewGroup) addedView.getParent()).removeView(addedView);
                }
                ((Panel) v1).removeAndDetachChildViews();
                if (addedView != null) {
                    p2.addView(addedView);
                }
                if (mIsWizard && !mIsCompactWizard) {
                    LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                            mPanelHeightWizard);
                    p2.setLayoutParams(layoutParam);
                } else {
                    p2.setLayoutParams(lp1);
                }
                p2.requestFocus();
                brzrListner.setDepth(depth);
                mParentForwardSliding1.start();
            } else if (noOfPanels == 2) {

                Panel v3 = (Panel) getChildAt(3);
                v3.setVisibility(VISIBLE);


                ViewGroup v = (ViewGroup) mNPanelBrowser.getChildAt(1);
                View removedView = v.getChildAt(0);
                if (v.getChildCount() > 0) {
                    v.removeViewAt(0);
                }
                if (brzrListner instanceof NPanelBrowserExtnListener) {
                    Log.d("NPanel", "viewRemoved: " + removedView);
                    ((NPanelBrowserExtnListener) brzrListner).viewRemoved(removedView);
                }

                View addedView = brzrListner.getPanelView(depth + 1);

                if (addedView != null && addedView.getParent() != null) {
                    ((ViewGroup) addedView.getParent()).removeView(addedView);
                }
                v3.addView(addedView);

                Panel v2 = (Panel) getChildAt(2);

                if (mIsWizard && !mIsCompactWizard) {
                    LayoutParams layoutParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                            mPanelHeightWizard);
                    v3.setLayoutParams(layoutParam);
                    v2.setLayoutParams(layoutParam);
                } else {
                    v3.setLayoutParams(lp2);
                    v2.setLayoutParams(lp1);
                }

                brzrListner.setDepth(depth);
                mParentForwardSliding1.start();
                mParentForwardSliding2.start();
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub
            if (mAnimationListener != null) {
                mAnimationListener.onAnimationStart();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (animation != null) {
                removeHardwareLayer(animation);
                Log.d("Npanel", "forward onAnimationEnd");
                if (animation.equals(mParentForwardSliding1) && noOfPanels == 1) {
                    removeandAddView();
                }
                if (animation.equals(mParentForwardSliding2)) {
                    removeandAddView();
                }
            }

            if (mAnimationListener != null) {
                mAnimationListener.onAnimationEnd();
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            Log.d("Npanel", "forward onAnimationCancel");

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * Implements backward animation
     */
    class BackwardAnimation implements AnimatorListener, AnimatorUpdateListener {
        private Animator mParentBackwardSliding, mAplphaanimFadeIn;
        private FPSUtil mFPSUtilSlide1, mFPSUtilSlide2;

        public BackwardAnimation() {
			/*Backward Sliding animation*/
            mParentBackwardSliding = (Animator) AnimatorInflater.loadAnimator(mContext,
                    R.anim.forward_animation_silde_out);
			/*Alpha animation*/
            if (noOfPanels == 2) {
                mAplphaanimFadeIn = (Animator) AnimatorInflater.loadAnimator(mContext,
                        R.anim.backward_fade_in);
            } else {
                mAplphaanimFadeIn = (Animator) AnimatorInflater.loadAnimator(mContext,
                        R.anim.backward_fade_in_single_panel);
            }
            mParentBackwardSliding.addListener(this);
            mFPSUtilSlide1 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "Backward Slide Animation 1");
            mFPSUtilSlide2 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "BAckward Slide Animation 1");

        }


        public boolean isBackwardAnimationRunning() {
            if (mParentBackwardSliding != null && mAplphaanimFadeIn != null) {
                return mParentBackwardSliding.isRunning() || mAplphaanimFadeIn.isRunning();
            }
            return false;
        }

        /**
         * call for starting the animation
         */
        void aninmStart() {
            ViewGroup v1 = (ViewGroup) mNPanelBrowser.getChildAt(1);
            ViewGroup v2 = (ViewGroup) mNPanelBrowser.getChildAt(2);
            View addedView = brzrListner.getPanelView(depth);
            Log.d(TAG, "setPannel1 listner depth" + brzrListner + "," + depth);
            // add view
            if (addedView != null && addedView.getParent() != null) {
                ((ViewGroup) addedView.getParent()).removeView(addedView);
            }
            if (noOfPanels == 2) {
                v2.setVisibility(GONE);
                Panel v3 = (Panel) mNPanelBrowser.getChildAt(3);
                mNPanelBrowser.removeView(v3);
                v3.setVisibility(VISIBLE);
                if (addedView != null) {
                    v3.addView(addedView);
                }
                mNPanelBrowser.addView(v3, 1);
                Log.d(TAG, "getChild Count" + mNPanelBrowser.getChildCount());
                ViewGroup v = (ViewGroup) getChildAt(3);
                View removedView = v.getChildAt(0);
                if (v.getChildCount() > 0) {
                    v.removeViewAt(0);
                }
                if (brzrListner instanceof NPanelBrowserExtnListener) {
                    Log.d("NPanel", "viewRemoved: " + removedView);
                    ((NPanelBrowserExtnListener) brzrListner)
                            .viewRemoved(removedView);
                }

                View vw1 = mNPanelBrowser.getChildAt(1);
                vw1.setLayoutParams(lp1);
                View vw2 = mNPanelBrowser.getChildAt(2);
                vw2.setLayoutParams(lp2);
                vw2.setVisibility(VISIBLE);

            } else if (noOfPanels == 1) {
                Panel p2 = (Panel) mNPanelBrowser.getChildAt(2);
                if (addedView != null) {
                    p2.addView(addedView);
                } else {
                    Log.d(TAG, "add View is null");
                }
                p2.setVisibility(VISIBLE);
                mNPanelBrowser.removeView(p2);
                mNPanelBrowser.addView(p2, 1);
                p2.requestFocus();
            }

            brzrListner.setDepth(depth);

            v1 = (ViewGroup) mNPanelBrowser.getChildAt(1);
            v2 = (ViewGroup) mNPanelBrowser.getChildAt(2);
            v1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            v2.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            Log.d("1DrawStat", "mAplphaanimFadeIn start");
            mFPSUtilSlide1.setView(v1, mAplphaanimFadeIn);

            mAplphaanimFadeIn.setTarget(v1);

            if (noOfPanels == 2) {

                mParentBackwardSliding.setTarget(v2);
                ((ObjectAnimator) mParentBackwardSliding).setFloatValues(-mPanel1Width, 0.0f);

            } else if (noOfPanels == 1) {
                v1.setVisibility(GONE);
                mParentBackwardSliding.setTarget(v2);
                ((ObjectAnimator) mParentBackwardSliding).setFloatValues(0.0f, (float) v2.getWidth());
                ((ValueAnimator) mParentBackwardSliding).addUpdateListener(this);


            }
            Log.d("1DrawStat", "mParentBackwardSliding start");
            mFPSUtilSlide2.setView(v2, mParentBackwardSliding);

            AnimatorSet lAnimatorSet = new AnimatorSet();
            if (mIsOptions) {
                lAnimatorSet.play(mAplphaanimFadeIn).after(mParentBackwardSliding);
            } else {
                lAnimatorSet.play(mParentBackwardSliding).with(mAplphaanimFadeIn);
            }

            lAnimatorSet.start();

        }


        /**
         * if animation is playing while starting the same, during fast
         * navigation
         */
        void animEnd() {
            if (mAplphaanimFadeIn != null && mAplphaanimFadeIn.isRunning()) {
                mAplphaanimFadeIn.end();
            }
            if (mParentBackwardSliding != null && mParentBackwardSliding.isRunning()) {
                mParentBackwardSliding.end();
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub
            if (mAnimationListener != null) {
                mAnimationListener.onAnimationStart();
            }

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (animation != null) {
                removeHardwareLayer(animation);
                if (animation.equals(mParentBackwardSliding)) {
                    Panel p;
                    if (noOfPanels == 2) {
                        p = (Panel) getChildAt(3);
                        p.removeAndDetachChildViews();
                    } else if (noOfPanels == 1) {
                        p = (Panel) getChildAt(2);
                        p.setVisibility(GONE);
                        p.setTranslationX(0.0f);
                        p.removeAndDetachChildViews();
                        final ViewGroup v1 = (ViewGroup) mNPanelBrowser.getChildAt(1);
                        v1.setVisibility(VISIBLE);
                        if (getFocusedChild() == null) {
                            v1.requestFocus();
                        }
                    }

                }
            }

            if (mAnimationListener != null) {
                mAnimationListener.onAnimationEnd();
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (!mMoveToPreviousPage) {
                mMoveToPreviousPage = true;
                bkTrace.moveToPreviousDepth();
            }
        }
    }

    class CollapseAnimation implements AnimatorListener {
        private Animator mParentSliding1, mParentSliding2;
        private FPSUtil mFPSUtilSlide1, mFPSUtilSlide2;

        public CollapseAnimation() {
            mParentSliding2 = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.forward_animation_silde_out);
            mParentSliding1 = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.forward_animation_silde_out);
            mParentSliding1.addListener(this);
            mParentSliding2.addListener(this);
            mFPSUtilSlide1 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "Collapse Animation 1");
            mFPSUtilSlide2 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "Collapse Animation 2");
        }

        void onAnimStart() {
            View v1 = mNPanelBrowser.getChildAt(1);
            View v2 = mNPanelBrowser.getChildAt(2);
            v1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            v2.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            mFPSUtilSlide1.setView(v1, mParentSliding1);
            mFPSUtilSlide2.setView(v2, mParentSliding2);

            mParentSliding1.setTarget(v1);
            mParentSliding2.setTarget(v2);
            mParentSliding1.start();
            mParentSliding2.start();
        }

        void removeView() {
            mNPanelBrowser.getChildAt(1).setVisibility(GONE);
            mNPanelBrowser.getChildAt(2).setVisibility(GONE);
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (animation != null && animation.equals(mParentSliding1)) {
                removeView();
                mNPanelBrowser.getChildAt(1).setTranslationX(0.0f);
                mNPanelBrowser.getChildAt(2).setTranslationX(0.0f);
            }
            if (animation != null) {
                removeHardwareLayer(animation);
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }

    }

    class RestoreAnimation implements AnimatorListener {
        private Animator mPannel1Sliding, mBackTraceCollapsing, mPannel2Sliding;
        private FPSUtil mFPSUtilSlide1;

        public RestoreAnimation() {
            mBackTraceCollapsing = (Animator) AnimatorInflater
                    .loadAnimator(mContext, R.anim.forward_animation_collapse);

            mPannel1Sliding = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.forward_animation_slide1);
            mPannel2Sliding = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.forward_animation_slide1);
            mPannel1Sliding.setStartDelay(100);
            mPannel2Sliding.setStartDelay(100);
            mBackTraceCollapsing.addListener(this);
            mPannel1Sliding.addListener(this);
            mPannel2Sliding.addListener(this);

            mFPSUtilSlide1 = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "RestoreAnimation 1");

        }

        void aninStart() {
            bkTrace.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mBackTraceCollapsing.setTarget(bkTrace);
            mBackTraceCollapsing.start();

            View v1 = mNPanelBrowser.getChildAt(1);
            v1.setVisibility(VISIBLE);
            v1.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            mPannel1Sliding.setTarget(v1);
            mFPSUtilSlide1.setView(v1, mPannel1Sliding);
            v1.requestFocus();

            Log.d(TAG, " RestoreAnimation aninStart");

            View v2 = mNPanelBrowser.getChildAt(2);
            v2.setVisibility(VISIBLE);
            v2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mPannel2Sliding.setTarget(v2);
            mFPSUtilSlide1.setView(v2, mPannel2Sliding);

            mPannel1Sliding.start();
            mPannel2Sliding.start();

        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (animation != null && animation.equals(mBackTraceCollapsing)) {
                bkTrace.setAlpha(1.0f);
                Panel p = (Panel) (mNPanelBrowser.getChildAt(1));
                Panel p1 = (Panel) (mNPanelBrowser.getChildAt(2));
                p.setVisibility(VISIBLE);
                p1.setVisibility(VISIBLE);
                removeHardwareLayer(animation);
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }
    }

    class NpannelCollapseAnimation implements AnimatorListener {
        private Animator mPannelCollapsing = null;
        private FPSUtil mFPSUtilPannelCollapse;

        public NpannelCollapseAnimation() {
            mPannelCollapsing = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.forward_animation_collapse);
            mPannelCollapsing.addListener(this);
            mFPSUtilPannelCollapse = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(),
                    "NpannelCollapseAnimation 1");
        }

        public void animStart() {
            View v = mNPanelBrowser.getChildAt(1);
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mPannelCollapsing.setTarget(v);
            mFPSUtilPannelCollapse.setView(v, mPannelCollapsing);
            mPannelCollapsing.start();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            // Change the layout params
            lp = getLayoutParams();
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = getHeight();
            setLayoutParams(lp);

            View v = mNPanelBrowser.getChildAt(1);
            v.setVisibility(GONE);
            v.setAlpha(1.0f);
            removeHardwareLayer(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }
    }

    class NpannelRestoreAnimation implements AnimatorListener {
        private Animator mPannelFadeIn = null;
        private FPSUtil mFPSUtilRestore;

        public NpannelRestoreAnimation() {
            mPannelFadeIn = (Animator) AnimatorInflater.loadAnimator(mContext, R.anim.backward_fade_in);
            mPannelFadeIn.addListener(this);
            mFPSUtilRestore = FPSUtil.getFpsUtil((Activity) mNPanelBrowser.getContext(), "NpannelRestoreAnimation 1");
        }

        public void animStart() {
            View v = mNPanelBrowser.getChildAt(1);
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mPannelFadeIn.setTarget(v);
            v.setVisibility(VISIBLE);
            v.setAlpha(0.0f);
            mFPSUtilRestore.setView(v, mPannelFadeIn);
            mPannelFadeIn.start();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            View v = mNPanelBrowser.getChildAt(1);
            v.requestFocus();
            removeHardwareLayer(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * Removs the hardware layer and clears animation
     */
    public void removeHardwareLayer(Animator animation) {
        // removing the hardware layer
        View view = (View) ((ObjectAnimator) animation).getTarget();
        view.setLayerType(View.LAYER_TYPE_NONE, null);
        view.clearAnimation();

    }

    /**
     * Refresh the views in backTrace
     */
    public void refreshBackTraceView() {
        bkTrace.refreshUI();
    }
}
