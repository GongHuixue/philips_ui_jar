/**
 *
 */
package ui.media.ui;

import ui.media.utility.PlaybackControlConstants;
import ui.media.utility.PlaybackSpeed;
import fany.phpuijar.R;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;

import ui.utils.LogHelper;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


/**
 * PlayBackControlLayout class is used to define the Video(DLNA,CB)
 * playbackcontrol components and its state.
 */
public class PlayBackControlLayout extends AbsPlayBackControlLayout {

    private IDelegateEventListener mIDelegateEventListener = null;
    private static final String TAG = PlayBackControlLayout.class.getSimpleName();

    private ImageButton mPrev, mNext, mPausePlay, mOptKey;

    private ImageButtonRwd mRwd;
    private ImageButtonFwd mFwd;

    private SeekBar mProg;

    private TextView mHeader, mMetaInfo, mCurrTime, mEndTime;

    private String mFileName = " ";
    private String mMetaDataPath = " ";
    private String mDolbyText = null;

    /**
     * @param context
     */
    public PlayBackControlLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    public PlayBackControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PlayBackControlLayout(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Will be available all the time, find..wont fail.
        mHeader = (TextView) findViewById(R.id.TxtFileNm);
        mMetaInfo = (TextView) findViewById(R.id.metaInfo);

        mPausePlay = (ImageButton) findViewById(R.id.pause);
        mPrev = (ImageButton) findViewById(R.id.prevbutton);
        mNext = (ImageButton) findViewById(R.id.nextButton);

        mCurrTime = (TextView) findViewById(R.id.current_time);
        mOptKey = (ImageButton) findViewById(R.id.optionskey);

        mProg = (SeekBar) findViewById(R.id.mediacontroller_progress);

        mEndTime = (TextView) findViewById(R.id.endtime);
        mFwd = (ImageButtonFwd) findViewById(R.id.ffwd);

        mRwd = (ImageButtonRwd) findViewById(R.id.rew);

    }

    @Override
    public void setMetadata(ContentValues values) {

        if (values != null && values.size() > 0) {

            mDolbyText = values
                    .getAsString(PlaybackControlConstants.PLAY_DOLBY_DTS_TEXT);
            mMetaDataPath = values
                    .getAsString(PlaybackControlConstants.PLAY_METADATA_TEXT);
            mFileName = values
                    .getAsString(PlaybackControlConstants.PLAY_SPEED_TEXT);
        }
        refreshMetaInfo();

    }

    @Override
    public void setMetadata(String key, String values) {
        parseMetaInfo(key, values);
        refreshMetaInfo();
    }

    private void parseMetaInfo(String key, String value) {
        // No placeholder for Dolby DTS by UXD, it should be appended along with
        // metadata - confirmed by Rajesh Ghodke and Rama
        if (key.equals(PlaybackControlConstants.PLAY_DOLBY_DTS_TEXT)) {
            mDolbyText = value;
        } else if (key.equals(PlaybackControlConstants.PLAY_METADATA_TEXT)) {
            mMetaDataPath = value;
        } else if (key.equals(PlaybackControlConstants.PLAY_SPEED_TEXT)) {
            mFileName = value;
        } else {
            LogHelper.i(TAG, "parseMetaInfo  invalid Key :" + key);
        }
        LogHelper.i(TAG, "parseMetaInfo  mFileName : " + mFileName + "  mDolbyText :"
                + mDolbyText + " mMetaDataPath " + mDolbyText);
        refreshMetaInfo();

    }

    private void refreshMetaInfo() {

        if (mHeader != null) {
            mHeader.setText(mFileName);
        }
        if (mMetaInfo != null) {
            mMetaInfo.setText(mMetaDataPath);
        }

    }

    @Override
    public void showOptionsKey(boolean status) {

        if (status) {
            mOptKey.setVisibility(VISIBLE);
        } else {
            mOptKey.setVisibility(INVISIBLE);
        }
        mOptKey.setFocusable(false);
    }

    @Override
    public void seekTo(int position) {
        mProg.setProgress(position);

    }

    private int evaluateVisibility(boolean isEnable) {
        LogHelper.i(TAG, "evaluateVisibility :" + isEnable);
        return (isEnable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEnableFwdRwdKeys(boolean isEnable) {
        LogHelper.i(TAG, "setEnableFwdRwdKeys :" + isEnable);
        mRwd.setEnabled(isEnable);
        mRwd.setFocusable(isEnable);
        mFwd.setEnabled(isEnable);
        mFwd.setFocusable(isEnable);

    }

    @Override
    public void setEnableNextKeys(boolean isEnable) {
        mNext.setEnabled(isEnable);
        mNext.setFocusable(isEnable);
        mNext.setVisibility(evaluateVisibility(isEnable));
    }

    @Override
    public void setEnablePrevKeys(boolean isEnable) {
        mPrev.setEnabled(isEnable);
        mPrev.setFocusable(isEnable);
        mPrev.setVisibility(evaluateVisibility(isEnable));
    }

    @Override
    public void setEnabledPausePlay(boolean isEnable) {
        mPausePlay.setEnabled(isEnable);
        mPausePlay.setFocusable(isEnable);
    }

    @Override
    public void setEnableProgressBar(boolean isEnable) {
        mProg.setEnabled(isEnable);
        mProg.setFocusable(isEnable);

    }

    @Override
    public void setRwdFwdListener(OnClickListener rwd, OnClickListener fwd) {
        mRwd.setOnClickListener(rwd);
        mFwd.setOnClickListener(fwd);
    }

    @Override
    public void setRwdFwdLongClickListener(OnLongClickListener rwd,
                                           OnLongClickListener fwd) {
        mRwd.setOnLongClickListener(rwd);
        mFwd.setOnLongClickListener(fwd);
    }

    @Override
    public void setPausePlayListener(OnClickListener pause) {
        mPausePlay.setOnClickListener(pause);
    }

    @Override
    public void setPreNextListener(OnClickListener prev, OnClickListener next) {
        mPrev.setOnClickListener(prev);
        mNext.setOnClickListener(next);
    }

    @Override
    public void setProgressListener(OnSeekBarChangeListener seek) {
        mProg.setOnSeekBarChangeListener(seek);

    }

    @Override
    public void setHeaderText(String txt) {
        if (mHeader != null) {
            mHeader.setText(txt);
        }
    }

    @Override
    public void setCurrentTime(int currTime) {
        String currTimeStr = stringForTime(currTime);
        LogHelper.i(TAG, "setCurrentTime   :" + currTimeStr);
        mCurrTime.setText(currTimeStr);

    }

    @Override
    public void setEndTime(int endTime) {
        String endTimeStr = stringForTime(endTime);
        LogHelper.i(TAG, "setEndTime   :" + endTimeStr);
        mEndTime.setText(endTimeStr);
    }

    @Override
    public boolean getSeekBarFocusability() {

        return mProg.hasFocus();

    }

    @Override
    public void refreshSelectors(boolean isPlaying) {

        // if both will be avail together , we can relay on
        // single.
        updatePausePlayButton(isPlaying);
        refreshFwdRwdSelectors();
    }

    @Override
    public void updatePausePlayButton(boolean isPlaying) {
        LogHelper.i(TAG, " updatePausePlayBackground  : isPlaying : " + isPlaying);
        if (isPlaying) {
            mPausePlay.setBackgroundResource(R.drawable.play_pause_selector);
        } else {
            mPausePlay.setBackgroundResource(R.drawable.play_playicon_selector);
        }
    }


    @Override
    public void refreshFwdRwdSelectors() {
        // if both will be avail together , we can relay on single.
        mFwd.setCurrntSpeedState(PlaybackSpeed.SPEED_LEVEL3_NORMAL);
        mRwd.setCurrntSpeedState(PlaybackSpeed.SPEED_LEVEL3_NORMAL);
    }

    @Override
    public void setPausePlayFocus() {
        mPausePlay.requestFocus();
    }

    @Override
    public void updateFwdRwdStatus(boolean fwd, int speed) {

        if (fwd) {
            mFwd.setCurrntSpeedState(speed);
        } else {
            mRwd.setCurrntSpeedState(speed);
        }
    }

    @Override
    public void setRwdFwdFocus(boolean rwd) {

        if (rwd) {
            mRwd.requestFocus();
        } else {
            mFwd.requestFocus();
        }
    }

    @Override
    public void setPrevNextFocus(boolean prev) {

        if (prev) {
            mPrev.requestFocus();
        } else {
            mNext.requestFocus();
        }
    }

    @Override
    public void setPlayPauseFocus() {

        mPausePlay.requestFocus();
    }

    @Override
    public void setMaxProgress(int max) {
        mProg.setMax(max);

    }

    @Override
    public void setProgress(int progress) {
        mProg.setProgress(progress);
    }

    @Override
    public void setSecondaryProgress(int progress) {
        mProg.setSecondaryProgress(progress);
    }

    @Override
    public void removeSecondaryProgress() {
        mProg.setSecondaryProgress(0);
    }

    @Override
    public void setIDelegateEventListener(
            IDelegateEventListener iIDelegateEventListener) {
        mIDelegateEventListener = iIDelegateEventListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.i(TAG, "dispatchKeyEvent " + event.toString() + " " + toString());
        boolean ret = false;
        if (mIDelegateEventListener != null) {
            ret = mIDelegateEventListener.keyEventDispatcherPBC(event);
        }
        if (!ret) {
            return super.dispatchKeyEvent(event);
        } else {
            return ret;
        }

    }
}
