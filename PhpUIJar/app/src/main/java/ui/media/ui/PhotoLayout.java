/**
 *
 */
package ui.media.ui;

import ui.media.utility.PlaybackControlConstants;

import fany.phpuijar.R;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;

import ui.utils.LogHelper;

import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * Photolayout class is used to define the slideshow playbackcontrol components
 * and its state.
 */
public class PhotoLayout extends AbsPlayBackControlLayout {

    private IDelegateEventListener mIDelegateEventListener = null;
    private static final String TAG = PlayBackControlLayout.class.getSimpleName();

    private ImageButton mPrev, mNext, mPausePlay, mOptKey;

    private TextView mHeader, mMetaInfo, mCurrTime;

    private String mFileName = " ";
    private String mMetaDataPath = " ";
    private String mDolbyText = null;

    /**
     * @param context
     */
    public PhotoLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    public PhotoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PhotoLayout(Context context, AttributeSet attrs, int defStyle) {
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
        if (key.equals(PlaybackControlConstants.PLAY_DOLBY_DTS_TEXT)) {

            mDolbyText = value;
        }
        if (key.equals(PlaybackControlConstants.PLAY_METADATA_TEXT)) {

            mMetaDataPath = value;

        } else {
            mFileName = value;

        }
        LogHelper.i(TAG, "parseMetaInfo  mFileName : " + mFileName + "  mDolbyText :" + mDolbyText + " mMetaDataPath " + mDolbyText);
        refreshMetaInfo();
    }

    private void refreshMetaInfo() {

        mHeader.setText(mFileName);
        // TODO : Muthu :append dolby text - check UXD
        mMetaInfo.setText(mMetaDataPath);
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
    public void setHeaderText(String txt) {
        mHeader.setText(txt);
    }

    @Override
    public void setSlideShowTime(String currTime) {
        LogHelper.d(TAG, " slideshow curr/tot : " + currTime);
        mCurrTime.setText(currTime);
    }

    @Override
    public void refreshSelectors(boolean isPlaying) {

        updatePausePlayBackground(isPlaying);

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
    public void setEnableNextKeys(boolean isEnable) {
        mNext.setEnabled(isEnable);
        mNext.setFocusable(isEnable);

    }

    @Override
    public void setEnablePrevKeys(boolean isEnable) {
        mPrev.setEnabled(isEnable);
        mPrev.setFocusable(isEnable);
    }

    @Override
    public void setEnabledPausePlay(boolean isEnable) {
        mPausePlay.setEnabled(isEnable);
        mPausePlay.setFocusable(isEnable);

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
    public void setPrevNextLongClickListener(OnLongClickListener prev,
                                             OnLongClickListener next) {
        mPrev.setOnLongClickListener(prev);
        mNext.setOnLongClickListener(next);
    }

    @Override
    public void seekTo(int position) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEnableFwdRwdKeys(boolean isEnable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEnableProgressBar(boolean isEnable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIDelegateEventListener(
            IDelegateEventListener iIDelegateEventListener) {
        mIDelegateEventListener = iIDelegateEventListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.e(TAG, "dispatchKeyEvent " + event.toString() + " " + toString());
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

    @Override
    public void updatePausePlayButton(boolean isPlaying) {
        // Slide show both funtionality is same.refreshSelectors(isPlaying).This
        // my differ in future if fwd/rwd key needed, hence using diff code.
        updatePausePlayBackground(isPlaying);
    }

    private void updatePausePlayBackground(boolean isPlaying) {
        LogHelper.d(TAG, " updatePausePlayBackground  : isPlaying : " + isPlaying);
        if (isPlaying) {
            mPausePlay.setBackgroundResource(R.drawable.play_pause_selector);
        } else {
            mPausePlay.setBackgroundResource(R.drawable.play_playicon_selector);
        }

    }
}
