/**
 *
 */
package ui.media.ui;

import java.util.Formatter;
import java.util.Locale;

import ui.media.PlayBackControl2;
import ui.media.utility.PlaybackState;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * AbsPlayBackControlLayout class is used to handle the keyvents of Playback
 * control in generic.
 */
public abstract class AbsPlayBackControlLayout extends RelativeLayout {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    /**
     * Keyevent dispatcher to Playercontroller2 class
     */
    public interface IDelegateEventListener {
        boolean keyEventDispatcherPBC(KeyEvent event);

    }

    ;

    /**
     * @param context
     */
    public AbsPlayBackControlLayout(Context context) {
        super(context);
        initialize();
    }

    /**
     * @param context
     * @param attrs
     */
    public AbsPlayBackControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AbsPlayBackControlLayout(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);

        initialize();

    }

    private void initialize() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**
     * Convenience method to delegate the events to
     *
     * @param iIDelegateEventListener
     */
    public void setIDelegateEventListener(
            IDelegateEventListener iIDelegateEventListener) {

    }

    /**
     * Convenience method to set the seek position of progress bar.
     *
     * @param position
     */
    public abstract void seekTo(int position);

    /**
     * Convenience method to set Metadata info to playbackcontrol layout.
     *
     * @param values
     */
    public abstract void setMetadata(ContentValues values);

    /**
     * Convenience method to set the specific Metadata info to playbackcontrol
     * layout.
     *
     * @param key
     * @param values
     */
    public abstract void setMetadata(String key, String values);

    /**
     * Convenience method to enable/disable the fwd and rwd keys based on
     * application input.
     *
     * @param isEnable
     */
    public abstract void setEnableFwdRwdKeys(boolean isEnable);

    /**
     * Convenience method to enable/disable the next and prev keys based on
     * application input.
     *
     * @param isEnable
     */
    public abstract void setEnablePrevKeys(boolean isEnable);

    /**
     * Convenience method to enable/disable the next and prev keys based on
     * application input.
     *
     * @param isEnable
     */
    public abstract void setEnableNextKeys(boolean isEnable);

    /**
     * Convenience method to enable/disable the play/pause keys based on
     * playback state.
     *
     * @param isEnable
     */
    public abstract void setEnabledPausePlay(boolean isEnable);

    /**
     * Convenience method to enable/disable the TSB based on application input.
     *
     * @param isEnable
     */
    public abstract void setEnableProgressBar(boolean isEnable);

    /**
     * Convenience method to enable slow mode based on application input.
     */
    public void showSlowModePlayback() {
        // to show playback text for DLNA
        // by default no text will be shown
    }

    /**
     * Convenience method to display the options key default : false
     */
    public abstract void showOptionsKey(boolean status);

    /**
     * Convenience method to set the fwd speed control.
     *
     * @param speed
     */
    public void setForwardSpeed(int speed) {

    }

    /**
     * Convenience method to set the rwd speed control.
     *
     * @param speed
     */
    public void setRewSpeed(float speed) {

    }

    /**
     * Convenience method to enable secondary TSB updated based on application
     * input.
     */
    public void enableSecondaryUpdate() {

    }

    /**
     * Convenience method to disable secondary TSB updated based on application
     * input.
     */
    public void disableSecondaryUpdate() {

    }

    /**
     * Convenience method to remove secondary TSB updated based on application
     * input.
     */
    public void removeSecondaryProgress() {

    }

    /**
     * Convenience method to set the secondray progress value.
     *
     * @param progress
     */
    public void setSecondaryProgress(int progress) {

    }

    /**
     * Convenience method to set the TSB progress max limit.
     *
     * @param max
     */
    public void setMaxProgress(int max) {

    }

    /**
     * Convenience method to set the current TSB progress value.
     *
     * @param progress
     */
    public void setProgress(int progress) {
        // TODO Auto-generated method stub
    }

    /**
     * convenience method to set the view listeners
     *
     * @param prev
     * @param next
     */
    public void setPreNextListener(OnClickListener prev,
                                   OnClickListener next) {
        // TODO Auto-generated method stub
    }

    /**
     * convenience method to set the view listeners
     *
     * @param rwd
     * @param fwd
     */
    public void setRwdFwdListener(OnClickListener rwd,
                                  OnClickListener fwd) {
        // TODO Auto-generated method stub
    }

    /**
     * convenience method to set the view listeners
     *
     * @param pause
     */
    public void setPausePlayListener(OnClickListener pause) {
        // TODO Auto-generated method stub
    }

    /**
     * convenience method to set the view listeners
     *
     * @param seek
     */
    public void setProgressListener(OnSeekBarChangeListener seek) {
        // TODO Auto-generated method stub

    }

    /**
     * To set the filename
     *
     * @param name
     */
    public void setHeaderText(String name) {
        // TODO Auto-generated method stub
    }

    /**
     * To set the current playback time
     *
     * @param currTime
     */
    public void setCurrentTime(int currTime) {
        // TODO Auto-generated method stub
    }

    /**
     * To set the current playback time
     *
     * @param currTime
     */
    public void setSlideShowTime(String currTime) {
        // TODO Auto-generated method stub
    }

    /**
     * To set the end of playback time
     *
     * @param endTime
     */
    public void setEndTime(int endTime) {
        // TODO Auto-generated method stub
    }

    /**
     * To handle the left/right key events in seekbar of playing content only if
     * it is focused.
     *
     * @return
     */
    public boolean getSeekBarFocusability() {
        return false;
    }

    /**
     * Convenience method to refresh the background selector. Set it to default
     * selectors.
     *
     * @param isPlaying
     */
    public void refreshSelectors(boolean isPlaying) {
        // TODO Auto-generated method stub
    }

    /**
     * To reset the fwd and rwd selecotors.
     */
    public void refreshFwdRwdSelectors() {
        // TODO Auto-generated method stub
    }

    public void setPausePlayFocus() {
        // TODO Auto-generated method stub

    }

    /**
     * Convenience method to udpate the fwd,rwd selector based on current
     * playback speed.
     *
     * @param fwd
     * @param speed
     */
    public void updateFwdRwdStatus(boolean fwd, int speed) {
        // TODO Auto-generated method stub
    }

    /**
     * Longkey event listeners for Rwd and Fwd buttons
     *
     * @param rwd
     * @param fwd
     */
    public void setRwdFwdLongClickListener(OnLongClickListener rwd,
                                           OnLongClickListener fwd) {
        // TODO Auto-generated method stub

    }

    /**
     * Longkey event listeners for Rwd and Fwd buttons
     *
     * @param prev
     * @param next
     */
    public void setPrevNextLongClickListener(OnLongClickListener prev,
                                             OnLongClickListener next) {
        // TODO Auto-generated method stub

    }

    public void setRwdFwdFocus(boolean rwd) {
        // TODO Auto-generated method stub
    }

    public void setPrevNextFocus(boolean prev) {
        // TODO Auto-generated method stub
    }

    public void setPlayPauseFocus() {
        // TODO Auto-generated method stub
    }

    String stringForTime(int timeMs) {
        int totalSeconds = Math.round((float) timeMs / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * This call will refresh the Pause/Play button do avoid mediaplayer delay,other
     * buttons can be in any state(2x,4x..etc).
     */
    public void updatePausePlayButton(boolean isPlaying) {
    }

}
