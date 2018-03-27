package ui.media;

import ui.PlayBackControl.KeyRegisterListener;
import ui.media.ui.AbsPlayBackControlLayout;
import ui.media.utility.PlaybackControlConstants;
import ui.media.utility.PlaybackSpeed;
import ui.media.utility.PlaybackState;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.os.Message;

import ui.utils.LogHelper;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 *  * <style>
 * table,th,td
 * {
 * border:1px solid black;
 * }
 * </style>
 * <h1 style="text-align: center;"><span style="font-family:arial,helvetica,sans-serif;">PlaybackControl Component Design</span></h1>

 * <p><strong>Purpose and Scope :</strong></p>
<p>Playback component gives user interface while media player plays any media file or images.</p>
<p>User Interactions includes following actions by remote keys.</p>


<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Playing /Pausing a file.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Fast Forwarding/ rewinding.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Playing Next/Previous file from list of selected files.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Dragging progress bar to desired position forward or backward.</p>


<p>Playback controls are used for situations where the progress of a running video, music, slide show is indicated continuously. It gives user a view of how longer the play back will take, what is speed for ffwd/rwd .</p>
<p>Playback controls are time line based. The bar is empty at the beginning of the activity showed in blue color and fills from left to right. The user can interact with the progress bar by using DPad keys to change the progress back or forward on the time line. It also shows buffered amount of content with gray color on progress bar in time shift feature, where channel can be paused and buffered and then played.</p>
<p>It also shows Name of file which is getting played and some more information like count of file in selected folder, name of album etc. at top window of play back component.</p>
<p>Playback controls is shown for 4sec after 4sec if there is no user interaction then it is hidden. Info key of remote can be used if user wants control to be displayed continuously without time out.</p>
<p>PlayBack component doesn&rsquo;t play media file.</p>


<p><strong>References :</strong></p>
<p>Component reference document link (share point link with the component name)</p>



<p><strong>List of applications using this component</strong></p>
<ol>
	<li>Content Explorer &ndash; While running video file, audio file and slide show of images playback component is shown to user which shows status of media file.</li>
	<li>PTA &ndash; Media file from other device like mobile or tablet is pushed on Tv set to view.</li>
	<li>PlayTv &ndash; User is playing already recorded content from storage media, Playback comp is used .</li>
	<li>Timeshift - When live channel needs to be recorded, stream is paused and content starts buffering. Playback component shows this buffered content with gray color.</li>
	<li>Nettv browser &ndash; Videos from network are viewed in full screen mode.</li>
</ol>
<p><strong>&nbsp;</strong></p>


<p><strong>Requirements :</strong></p>

<p>Handle following actions using keys and pointer</p>

<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Play /Pause a file.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Fast Forward/ rewind.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Playing Next/Previous file from list of selected files.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Changing progress bar to desired position forward or backward using DPad keys.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Show current time duration of media file at left side and total duration of media file at right side of bottom window above progress bar.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Show speed at the time user does forward and rewind, Name of file and other information about that file c/a metadata, Type of video played (Dolby digital etc.) at top window.</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Handle visibility by using &nbsp;media keys in remote</p>
<p>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Update UI when user uses media keys in remote.</p>

<p><strong><span style="text-decoration: underline;">Usage Guidelines for Apps:</span></strong></p>
<p><strong>Packages exposed for Apps:</strong></p>
	<ol>
	<li><strong>1.&nbsp;&nbsp;&nbsp;&nbsp; </strong><strong>org.droidtv.ui.comps.media</strong></li>
	<li><strong>2.&nbsp;&nbsp;&nbsp;&nbsp; </strong><strong>org.droidtv.ui.comps.media.utility</strong></li>
	</ol>

<pre><strong>Interfaces/Classes &nbsp;exposed to Client application:</strong></pre>
<pre>&nbsp;</pre>
<pre>1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Abstract class AbsMediaPlayerControl</pre>
<pre>&nbsp;</pre>
<pre>2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; IPbcDissmissListener //Playbackcontrol dismiss listener</pre>
<pre>and&nbsp; org.droidtv.ui.comps.media.utility constant files for specifying Speed and metadata contentvalues.</pre>


<pre><strong>Usage:</strong></pre>

<pre><strong>&nbsp;</strong></pre>
<pre>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  Activity using playback component should create an instance of it using following constructor.</pre>
<pre>1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; It accepts View as first argument which acts as anchor view on which top and bottom popup windows are rendered.</pre>
<pre>2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Second argument is boolean &ndash; which specifies whether it&rsquo;s a slide show of images or not.</pre>
<pre>3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Third argument is not used (printing the uri to identify the app).</pre>
<pre>&nbsp;</pre>
<pre><strong>public</strong> <a href="http://opengrok.tpvision.com:8080/source/s?refs=PlayBackControl&amp;project=device"><strong>PlayBackControl</strong></a>2(<strong>final</strong> <a href="http://opengrok.tpvision.com:8080/source/s?defs=View&amp;project=device">View</a> <a href="http://opengrok.tpvision.com:8080/source/s?refs=anchor&amp;project=device"><strong>anchor</strong></a>, <strong>boolean</strong> <a href="http://opengrok.tpvision.com:8080/source/s?refs=slideshow&amp;project=device"><strong>slideshow</strong></a>, <a href="http://opengrok.tpvision.com:8080/source/s?defs=String&amp;project=device">String</a> <a href="http://opengrok.tpvision.com:8080/source/s?refs=uri&amp;project=device"><strong>uri</strong></a>) {}</pre>
<pre>&nbsp;</pre>
<pre><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Following are the methods of playback control exposed to Activity and its purpose.</strong></pre>
<pre>&nbsp;</pre>
<pre>1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void show(int timeout) : &nbsp;Shows the PlayBackComponent on screen. It will go away automatically after 'timeout' milliseconds of inactivity. Pass 0 to show the controller&nbsp; until hide() is called.</pre>
<pre>2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void hide() :Remove the controller from the screen.</pre>
<pre>3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void setMediaPlayer(AbsMediaPlayerControlplayer) : Activity will pass object of type AbsMediaPlayerControlplayer&nbsp; to communicate between player and component.</pre>
<pre>4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void setEnabled(boolean enabled) : set the enability of &nbsp;all playback buttons.</pre>
<pre>Specific call back for this for playback control,</pre>
<pre>&nbsp;</pre>

<pre>&nbsp;&nbsp; setEnabledFfwd(boolean enabled)</pre>
<pre>&nbsp;&nbsp; setEnabledRew(boolean enabled)</pre>
<pre>&nbsp;&nbsp; setEnabledNext(boolean enabled)</pre>
<pre>&nbsp;&nbsp; setEnabledPrev(boolean enabled)</pre>
<pre>&nbsp;&nbsp; setEnabledPausePlay(boolean enabled)</pre>
<pre>&nbsp;&nbsp; setEnabledProgress(boolean enabled)</pre>
<pre>&nbsp;</pre>

<pre>5.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void setEnabledTrickAndJumpMode(boolean enabled)</pre>
<pre>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; To enable the trickmode option.</pre>
<pre>&nbsp;</pre>
<pre>6.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; setSlowPlayControlsEnabled(Boolean enable)</pre>
<pre>&nbsp;&nbsp; To enable DLNA trickplay along with trickmode. To be called with&nbsp; 5.</pre>
<pre>&nbsp;</pre>
<pre>7.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; setTrickModeRange(int fwdMax, int rwdMax)</pre>
<pre>&nbsp;&nbsp; Set the range for trickmode playback.</pre>
<pre>&nbsp;</pre>
<pre>8.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void enableSecondaryUpdate() : In timeshift , activity has to call this to notify component to start displaying secondary progress in progressbar.</pre>
<pre>9.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public void onSeekCompleted() : This is to notify component that activity has done the task.On this notification component will update UI and displays it.</pre>
<pre>10.&nbsp; void showOptionKey(boolean status )</pre>
<pre>&nbsp;&nbsp; To show the optionkey(2k15), playbackcontrol will give this callback to apps via unhandled keys.</pre>
<pre>11.&nbsp; Activity has to implement NewKeyRegisterListener and implement public boolean unHandledKeyEvents(KeyEvent event) to get the keys which are not handled by component as unhandled keys. Such keys are returned to activity by </pre>
<pre>mNewKeyRegisterListener.unHandledKeyEvents(event).</pre>
<pre>12.&nbsp; public void setMediaPlayer(AbsMediaPlayerControl player) : Activity will pass object of type AbsMediaPlayerControl to communicate between player and component</pre>
<pre>&nbsp;</pre>
<p><strong>PlaybackControl &ndash; Internal only: &nbsp;&nbsp;&nbsp;&nbsp; </strong></p>
<p><strong>Package: </strong>org.droidtv.ui.comps.media.ui</p>
<p><strong>Requirements Traceability - Design </strong></p>
<p><strong>&nbsp;Static Design : </strong>Playback Component includes following classes.<strong></strong></p>

<ul>
	<li>AbsPlayBackControlLayout extends RelativeLayout :
<ul>

	<li>AbsPlayBackControlLayout class is used to handle the keyvents of Playback control in generic.</li>
	</ul>
	</li>
	</ul>
<p>&nbsp;</p>
<ul>

<li>PlayBackControlLayout extends AbsPlayBackControlLayout
<ul>
	<li>PlayBackControlLayout class is used to define the Video(DLNA,CB)</li>
	<li>playbackcontrol components and its state.</li>
</ul>
</li>
</ul>
<p>&nbsp;</p>
<ul>
	<li>PhotoLayout extends AbsPlayBackControlLayout
<ul>
	<li>Photolayout class is used to define the slideshow playbackcontrol components and its state.</li>
</ul>
</li>
</ul>

<p>&nbsp;</p>
<p><strong>&nbsp;</strong></p>
<p><strong>Execution Design </strong></p>


<pre>Handler is used here to schedule messages to be executed at some point in the future. </pre>
<pre>&nbsp;</pre>
<p><strong>private</strong> Handler mHandler = <strong>new</strong> Handler() {</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; @Override</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>public</strong> <strong>void</strong> handleMessage(Message msg) {</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>int</strong> pos = 0;</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>switch</strong> (msg.what) {</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>case</strong> <em>FADE_OUT</em>:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; // Hide playbar</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>break</strong>;</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>case</strong> <em>SHOW_PROGRESS</em>:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //Set or update Progress and durations of progress bar</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>break</strong>;</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>case</strong> <em>SECONDARYPROGRESS</em>:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //setSecondaryProgress of progressbar received from activity&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>break</strong>;</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>case</strong> <em>LRKEYPRESSED</em>:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
<p>//incrementPlayBack by certain amount continuously for long key //pressed</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>break</strong>;</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</p>
<p>};</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><strong>Class Diagram</strong></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<img src="{@docRoot}/playback_design_asset/PlaybackControl_ClassDig.PNG" />
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><strong>Sequence Diagram</strong></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<img src="{@docRoot}/playback_design_asset/PlaybackControl_SeqDig.PNG" />
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
 *
 */
public class PlayBackControl2 implements
		AbsPlayBackControlLayout.IDelegateEventListener {

	protected static final String TAG = PlayBackControl2.class.getSimpleName();

	private AbsMediaPlayerControl mPlayer = null;
	private int mState = PlaybackUtils.NOLONGLRKEYPRSD;

	private final PlaybackViewCreator mViewHandler;
	private PlayerHandler mHandler;

	private boolean slideshow = false;

	private IPbcDissmissListener mIPbcDissmissListener;
	private int currDurAnim = 0;

	private int mPlaybackState = PlaybackState.PLAY;
	private int mPlaybackSpeed = PlaybackSpeed.DEFAULT;
	private int mTimeoutFromApplication = 0;
	private boolean isSecondaryProgressEnabled = false;
	private int mSsecondaryprogress = 0;

	private int mTrickModeFwdMax = PlaybackSpeed.SPEED_LEVEL8;
	private int mTrickModeRwdMax = PlaybackSpeed.SPEED_LEVEL8;
	/**
	 * To support thr trick mode functionality. It can be any xX.
	 */
	private boolean isTrickModeEnabled = false;
	/**
	 * In TrickMode slow play enabled or not 1/4X,1/2X support
	 */
	private boolean isSlowPlayEnabled = false;
	/**
	 * To set normal speed to default/play - for slowplay. Used for UI and media
	 * pause/play keys.
	 */
	private boolean isDLNAEnabled = false;

	private boolean enableWrapAround = true;
	private boolean enableNext = true;
	private boolean enablePrev = true;
	private boolean enablePausePlay = true;
	private boolean mEnableGetDuration = true;

	/**
	 * Interface for handling the Key Events - init by application
	 */
	private NewKeyRegisterListener mNewKeyRegisterListener = null;

	// Used for photoplayback longkeyevnt animator
	private ObjectAnimator mSlideShowAnimator;
	/**
	 * To Keep ref for application timout CR :332645
	 */
	private int mAppTimout = PlaybackUtils.DEFAULT_FADEOUT_TIME;
	
	/**
	 * To enable the progressbar trick jump even trickplay is disabled.(Special case MRC playback)  PR  :343154
	 */
	private boolean isProgJumpEnabled = false;
	
	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public PlayBackControl2(final View anchor, boolean slideshow, String uri) {

		LogHelper.e(TAG, "PlayBackControl()" + anchor + "uri" + uri);
		this.slideshow = slideshow;
		mViewHandler = new PlaybackViewCreator(anchor,slideshow);
		if (slideshow) {
			// No seekbar,rwd,fwd buttons removed form this xml. block the keys
			// for fwd,rwd.
			isTrickModeEnabled = false;
			mSlideShowAnimator = new ObjectAnimator();
			mSlideShowAnimator.setTarget(this);
			mSlideShowAnimator.setInterpolator(new LinearInterpolator());
			mSlideShowAnimator.setDuration(250);
		}
		getPlayBackLayout().setIDelegateEventListener(this);
		mViewHandler.getPupupWindow().setOnDismissListener(onDismissListener);
		getPlayBackLayout().refreshSelectors(true);
		initControllerView();

	}

	/**
	 * Anything you want to have happen when created. Probably should create a
	 * view and setup the event listeners on child views.
	 */

	private void initControllerView() {
		mHandler = new PlayerHandler(this);
		getPlayBackLayout().setPreNextListener(mPrevListener, mNextListener);
		getPlayBackLayout().setPausePlayListener(mPauseListener);
		if (slideshow) {
			getPlayBackLayout().setPrevNextLongClickListener(
					mPrevLongClickListener, mNextLongClickListener);
		} else {
			getPlayBackLayout().setRwdFwdListener(mRewListener, mFfwdListener);
			getPlayBackLayout().setRwdFwdLongClickListener(mRwdLongClickListener,
					mFwdLongClickListener);
//			getPlayBackLayout().setProgressListener(mSeekListener);
		}

	}

	
	void updateHandlerFadout() {
		if (mTimeoutFromApplication != 0){
			doHide();
		}
	}

	void upateHandlerSetProg() {
		LogHelper.d(TAG, "upateHandlerSetProg  : SHOW_PROGRESS");
		updatePlaybackState();//update play/pause button during seekcomplete and trick mode 
		setProgress();
		if (mViewHandler.getPupupWindow().isShowing()) {
			mHandler.sendEmptyMessageDelayed(PlaybackUtils.SHOW_PROGRESS,
					PlaybackUtils.UPDATE_INTERVAL);
		}
	}

	void updateHandlerSecondrayProg() {
		float lTotDur = mPlayer.getTotalDuration();
		int lSecBuff = mPlayer.getBufferDurationMillis();
		mSsecondaryprogress = lSecBuff;
		LogHelper.i(TAG, " duration tot : " + lTotDur + " lsecbuff :"
				+ lSecBuff+"  mSsecondaryprogress % "+mSsecondaryprogress);
		
		getPlayBackLayout().setSecondaryProgress(mSsecondaryprogress);
		Message msg = mHandler.obtainMessage(PlaybackUtils.SECONDARYPROGRESS);
		mHandler.sendMessageDelayed(msg, 1000);
		if (mSsecondaryprogress == lTotDur) {
			mHandler.removeMessages(PlaybackUtils.SECONDARYPROGRESS);
		}
	}

	void updateHandlerLongKeyPressed() {
		mEnableGetDuration = false;
		if (mState == PlaybackUtils.LONGKEYPRSDRWD) {
			incrementPlayBack(-9000);
		}
		if (mState == PlaybackUtils.LONGKEYPRSDFWD) {
			incrementPlayBack(9000);
		}
		if (mState != PlaybackUtils.NOLONGLRKEYPRSD
				&& mPlayer.getCurrentDuration() > 0
				&& (mPlayer.getTotalDuration()) > mPlayer.getCurrentDuration()) {
			mHandler.sendEmptyMessageDelayed(PlaybackUtils.LRKEYPRESSED, 50);
		}
	}
	 int setProgress() {
		LogHelper.i(TAG, "The progress will be shown" + currDurAnim+ " mEnableGetDuration : "+mEnableGetDuration);
		if (mPlayer == null) {
			return 0;
		}
	

		if (!slideshow) {
			
			if (mState !=PlaybackUtils.NOLONGLRKEYPRSD) {
				return 0;
			}
			updateProgMax();// In CB audio playback total duration is not
							// available at show(0), hence updating every
							// progress change. 
			
			int position = -1;
			int duration = mPlayer.getTotalDuration();
			if (mEnableGetDuration) {
				position = mPlayer.getCurrentDuration();
			} else {
				if (currDurAnim < 0) {
					currDurAnim = 0;
				} else if (currDurAnim > duration) {
					currDurAnim = duration;
				} else {
					position = currDurAnim;
				}
			}
			LogHelper.d(TAG, "position = " + position);
			if (duration > 0) {
				getPlayBackLayout().setProgress(position);
			} else {
				getPlayBackLayout().setProgress(0);
			}
		}
		int currentTime =mPlayer.getCurrentDuration();
		int totTime=mPlayer.getTotalDuration();
		if (slideshow) {
			// set slide text currtime/endtime 5/15.
			getPlayBackLayout().setSlideShowTime(currentTime+ "/" + totTime);
			LogHelper.e(TAG, " setSlideShowTime "+currentTime+ "/" + totTime);
		} else {
			getPlayBackLayout().setCurrentTime(currentTime);
			if (isSecondaryProgressEnabled) {//RMCR-2918
				totTime = mPlayer.getBufferDurationMillis();
				LogHelper.i(TAG, "  TSB secondary time : totTime = " + totTime);
			}
			getPlayBackLayout().setEndTime(totTime);
		}
		LogHelper.i(TAG, " The progress will be shown end ");
		return 0;
	}

	private void incrementPlayBack(int dur) {
		int duration = dur;
		currDurAnim = currDurAnim + duration;
		duration = mPlayer.getTotalDuration();
		LogHelper.d(TAG, "incrementPlayBack step 1 " + currDurAnim);
		if (!slideshow) { 
			// No SeekBar for slide show.This condition has to be checked if we need the seekTo in special
			// media with no totalduration
			if (duration > 0) {
				if (currDurAnim < 0) {
					currDurAnim = 0;
				} else if (currDurAnim > duration) {
					currDurAnim = duration;
				}
				// To check secondary proglimit and go to buffered duration seek..
				// at key- up :  tune to curranim which is already handled as part of handleevent().
				if (mState == PlaybackUtils.LONGKEYPRSDFWD) {
					checkSecondaryProgLimit();
				}
				LogHelper.d(TAG, "incrementPlayBack step 2 " + currDurAnim);
				getPlayBackLayout().setProgress(currDurAnim);
			}
		}
		doShow(mAppTimout);
	}

	/**
	 * Show the controller on screen. It will go away automatically after 4
	 * seconds of inactivity.
	 */
	public void show() {
		updateProgMax();
		LogHelper.i(TAG, "show() is called from application with default timeout ");
		doShow(PlaybackUtils.DEFAULT_FADEOUT_TIME);
		getPlayBackLayout().setPlayPauseFocus(); //set the focus to play/pause icon
		mAppTimout = PlaybackUtils.DEFAULT_FADEOUT_TIME;
	}

	/**
	 * Show the PlayBackComponent on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		updateProgMax();
		
		LogHelper.i(TAG, "Show() is called from application with timeout = "+ timeout);
		mTimeoutFromApplication = timeout;
		doShow(timeout);
		getPlayBackLayout().setPlayPauseFocus(); //set the focus to play/pause icon
		mAppTimout = timeout;
	
	}

	private void updateProgMax() {
		if (!slideshow) {
			int max = mPlayer.getTotalDuration();
			LogHelper.d(TAG, " set  : max prog: " + max);
			getPlayBackLayout().setMaxProgress(max);
		}
	}
	private void doShow(int timeout) {
		
		
		updatePlaybackState();//To avoid mediaplayer state change delay issue.
		LogHelper.d(TAG,"show timeout = " + timeout
						+ mHandler.hasMessages(PlaybackUtils.LRKEYPRESSED)
						+ " secondary progress will be shown ="	+ isSecondaryProgressEnabled+" mAppTimout : "+mAppTimout);
		mHandler.removeMessages(PlaybackUtils.FADE_OUT);
		mHandler.removeMessages(PlaybackUtils.SECONDARYPROGRESS);
		mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
		if ((isSecondaryProgressEnabled) && mPlayer!=null && (mSsecondaryprogress < mPlayer.getTotalDuration())) {
			mHandler.sendEmptyMessage(PlaybackUtils.SECONDARYPROGRESS);
		}
		mHandler.sendEmptyMessage(PlaybackUtils.SHOW_PROGRESS);
		mTimeoutFromApplication = timeout;
		LogHelper.d(TAG, "the media player is " + mPlayer.isPlaying() + mState);
		// Attach the popup window to anchor view
		mViewHandler.show();
		mHandler.sendEmptyMessageDelayed(PlaybackUtils.FADE_OUT,
				mTimeoutFromApplication);
	}

	/**
	 * Dismiss the popupwindow
	 * @deprecated similar to show()
	 */
	public void dismiss() {
		doHide();
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		LogHelper.i(TAG, " hide () is called from application ");
		doHide();
	}

	private void doHide() {
		if (getPlayBackLayout() == null) {
			return;
		}
		if (mViewHandler.getPupupWindow().isShowing()) {
			try {
				LogHelper.i(TAG, "the hide request is done");
				mViewHandler.getPupupWindow().dismiss();
				mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
				mHandler.removeMessages(PlaybackUtils.FADE_OUT);
				mHandler.removeMessages(PlaybackUtils.SECONDARYPROGRESS);
				mHandler.removeMessages(PlaybackUtils.LRKEYPRESSED);
			} catch (IllegalArgumentException ex) {
				LogHelper.w("MediaController", "already removed");
			}
		}

	}

	/**
	 * Convenience method to set Metadata info to playbackcontrol layout. Use
	 * {@link PlaybackControlConstants} key values to set the corresponding
	 * info.
	 * 
	 * @param values
	 */
	public void setMetadata(ContentValues values) {
		getPlayBackLayout().setMetadata(values);
	}

	/**
	 * Convenience method to set the specific Metadata info to playbackcontrol
	 * layout. Use {@link PlaybackControlConstants} key values to set the
	 * corresponding info.
	 * 
	 * @param key
	 * @param values
	 */
	public void setMetadata(String key, String values) {
		getPlayBackLayout().setMetadata(key, values);
	}

	/**
	 * Convenience method to set the mediaplayer instance to controller. This
	 * sets the progress bar to the max 1000 for audio and video and for images
	 * the total number of images
	 * */
	public void setMediaPlayer(AbsMediaPlayerControl player) {
		mPlayer = player;
		updateProgMax();
	}

	/**
	 * Enable or disable Fwd button Issue number 1771
	 * 
	 * @param enabled
	 */
	public void setEnabledFfwd(boolean enabled) {
		if (getPlayBackLayout() != null) {
			getPlayBackLayout().setEnableFwdRwdKeys(enabled);
		}
	}

	/**
	 * Enable or disable Rew button Issue number 1771
	 * 
	 * @param enabled
	 */
	public void setEnabledRew(boolean enabled) {
		if (getPlayBackLayout() != null) {
			getPlayBackLayout().setEnableFwdRwdKeys(enabled);
		}
	}

	/**
	 * Enable or disable Next button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */

	public void setEnabledNext(boolean enabled) {
		if (getPlayBackLayout() != null) {
			enableNext = enabled;
			getPlayBackLayout().setEnableNextKeys(enabled);
		}
	}

	/**
	 * Enable or disable Prev button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */
	public void setEnabledPrev(boolean enabled) {
		if (getPlayBackLayout() != null) {
			enablePrev = enabled;
			getPlayBackLayout().setEnablePrevKeys(enabled);
		}
	}

	/**
	 * Enable or disable Play/Pause button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */
	public void setEnabledPausePlay(boolean enabled) {
		if (getPlayBackLayout() != null) {
			enablePausePlay = enabled;
			getPlayBackLayout().setEnabledPausePlay(enabled);
		}
	}

	/**
	 * Enable or disable Progress bar button Issue number 1771
	 * 
	 * @param enabled
	 */

	public void setEnabledProgress(boolean enabled) {
		if (getPlayBackLayout() != null) {
			getPlayBackLayout().setEnableProgressBar(enabled);
		}
	}

	/**
	 * set the enabling of buttons. Will enable all the
	 * buttons(Play,prev,next,fwd,rwd). All are enabled by default.
	 * 
	 * @param enabled
	 *            if true buttons are enabled false button is not enabled
	 */
	public void setEnabled(boolean enabled) {
		if (getPlayBackLayout() != null) {
			getPlayBackLayout().setEnabledPausePlay(enabled);
			getPlayBackLayout().setEnableFwdRwdKeys(enabled);
			getPlayBackLayout().setEnableNextKeys(enabled);
			getPlayBackLayout().setEnablePrevKeys(enabled);
			getPlayBackLayout().setEnableProgressBar(enabled);
		}
	}

	/**
	 * Update the secondary buffer limit in progress bar
	 */
	public void enableSecondaryUpdate() {
		// start running a handler and update the secondary value
		isSecondaryProgressEnabled = true;
		Message msg = Message.obtain();
		msg.what = PlaybackUtils.SECONDARYPROGRESS;
		mHandler.sendMessage(msg);
	}

	/**
	 * To disable the secondary progress update
	 * 
	 * TODO : currently not used by any app, just for future purpose
	 */
	public void disableSecondaryUpdate() {
		isSecondaryProgressEnabled = false;
		removeSecondaryProgress();
		mHandler.removeMessages(PlaybackUtils.SECONDARYPROGRESS);
	}

	/**
	 * Convenience method to remove the secondary progress
	 */
	public void removeSecondaryProgress() {
		getPlayBackLayout().removeSecondaryProgress();
	}

	public interface NewKeyRegisterListener {
		/**
		 * Application should return false if it is not handled. (eg: keys
		 * up/down/left/right app should return false)
		 * 
		 * @param keyEvent
		 * @return
		 */
		public boolean unHandledKeyEvents(KeyEvent keyEvent);
	}

	/**
	 * The param has to be set by the class who has implemented the
	 * KeyRegisterListener to get the unhandled keys.
	 * 
	 * @param keyRegisterListener
	 *            AN-766
	 */
	// The New Public API for observer.
	public void registerNewKeyListenerUnhandledKeys(
			NewKeyRegisterListener keyRegisterListener) {
		mNewKeyRegisterListener = keyRegisterListener;
	}

	/**
	 * Notified by application which is using this component
	 */
	public void onSeekCompleted() {
		LogHelper.i(TAG, "onSeekCompleted is called");
		mState = PlaybackUtils.NOLONGLRKEYPRSD;
		currDurAnim = 0;
		mEnableGetDuration = true;
		mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
		mHandler.sendEmptyMessage(PlaybackUtils.SHOW_PROGRESS);
	}

	/**
	 * Playbackcontroller dismiss listener
	 * 
	 */
	public interface IPbcDissmissListener {
		public void onPbcDismissed();
	}

	public void setBackKeyListener(IPbcDissmissListener argListener) {
		mIPbcDissmissListener = argListener;
	}

	/**
	 * To be called by Application use case : after image gets uploaded(by
	 * PicturePlayerActivity)
	 * 
	 */
	public void setCurrentDuration() {
		LogHelper.i(TAG, "Show () is called from Application");
		mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
		mHandler.sendEmptyMessage(PlaybackUtils.SHOW_PROGRESS);
	}

	/**
	 * This sets SlowPlayTrickMode From Application use case : slowPlayEnabled
	 * is false for AudioPlayer
	 * 
	 * @param enabled
	 *            (Value=true, available)( Value=false, not available)
	 */
	public void setSlowPlayTrickMode(boolean enabled) {
		isSlowPlayEnabled = enabled;
	}

	/**
	 * Controls visibility of slow play controls on play bar - Forward and
	 * Rewind
	 * 
	 * This is to reset the slowplaycontrol.
	 * 
	 * 
	 * @param enable
	 *            (Value=Boolean.TRUE, not used)( Value=Boolean.FALSE, reset to
	 *            normal)
	 */
	public void setSlowPlayControlsEnabled(Boolean enable) {
		LogHelper.i(TAG, "setSlowPlayControlsEnabled is called from Application");
		isDLNAEnabled = false;
		resetPlayControls();
	}

	/**
	 * This method will be called by application for resetting Playback
	 * component before starting Next playback/any other keyevent like rwd to
	 * playkey.
	 * 
	 * 
	 */
	public void resetPlayControls() {
		LogHelper.i(TAG, " resetPlayControls called ");
		// PR : 332310 - dragging progress bar upto end of playback app won't give
		// the onseekcomplete call back.Hence resetting progress animation.
		mEnableGetDuration = true;
		currDurAnim = 0;
		mState = PlaybackUtils.NOLONGLRKEYPRSD;
		if (getPlayBackLayout() != null && mPlayer!=null) {
			boolean lState = mPlayer.isPlaying();
			getPlayBackLayout().refreshSelectors(lState);
			setPlaybackSpeed(PlaybackSpeed.SPEED_LEVEL3_NORMAL);// reset.
			if (lState){
				setPlaybackState(KeyEvent.KEYCODE_MEDIA_PLAY);
			}else{
				setPlaybackState(KeyEvent.KEYCODE_MEDIA_PAUSE);}
		}
		mTimeoutFromApplication = mAppTimout;
	}

	/**
	 * API to update play pause state of playback from Application.
	 * This call will refresh the Pause/Play button do avoid mediaplayer delay,other
	 * buttons can be in any state(2x,4x..etc). 
	 * Important : If you want full reset from any state, use resetPlayControls() API. 
	 */
	public void updatePlaybackState() {
		LogHelper.i(TAG, " updatePlaybackState called ");
		if (getPlayBackLayout() != null) {
			boolean lState = mPlayer.isPlaying();
			getPlayBackLayout().updatePausePlayButton(lState);

		}
	}

	
	/**
	 * API to be called to enable or disable trickplay and seek actions. should
	 * be called with setEnabledProgress()
	 * 
	 * @param enabled
	 *            true = enabled and false = disabled
	 */
	public void setEnabledTrickAndJumpMode(boolean enabled) {
		isTrickModeEnabled = enabled;
	}

	/**
	 * API to set wrap around of images during slideshow
	 * Note : 2k15 No seekbar for Phot playback control(Input from UXD), hence
	 * will give onNextkeypressed callback only, app has to maintain this.
	 * 
	 * @param enabled
	 *            = true when wrapAround is allowed.
	 */
	public void setEnabledWrapAround(boolean enabled) {
		enableWrapAround = enabled;
		LogHelper.i(TAG, "enableWrapAround " + enableWrapAround);
	}

	/**
	 * Sets the play back speed as per defined constants in @link
	 * PlaybackSpeed.java
	 * @param speed
	 *            constant value passed for key event
	 */
	private void setPlaybackSpeed(int speed) {
		LogHelper.d(TAG, "setPlaybackSpeed to " + speed);
		mPlaybackSpeed = speed;
	}

	/**
	 * Gets the current play back speed
	 * 
	 * @return int Constant for play back speed defined in @link
	 *         PlaybackSpeed.java
	 */
	private int getPlaybackSpeed() {
		LogHelper.d(TAG, " getPlaybackSpeed = " + mPlaybackSpeed);
		return mPlaybackSpeed;
	}

	/**
	 * Sets the play back state for Trick play keys only.
	 * 
	 * @param keycode
	 *            constant value passed for key event
	 */
	private void setPlaybackState(int keycode) {

		LogHelper.d(TAG, "setPlaybackState - keycode=" + keycode);
		mPlaybackState = PlaybackUtils.getPlaybackState(keycode);
	}

	/**
	 * Gets the current play back state
	 * 
	 * @return int Constant for play back state defined in @link
	 *         PlaybackState.java
	 */
	private int getPlaybackState() {
		return mPlaybackState;
	}

	/**
	 * Application Sets the current play back speed and playbackState Used for
	 * DMR trick play ( app : VideoPlayerActivity)
	 * 
	 * @param speed
	 *            = playbackspeed playbackState=state
	 * @deprecated Not used by any application.
	 */
	public void setPlaybackSpeedAndState(int speed, int state) {

	}

	private View.OnLongClickListener mFwdLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mFwdLongClickListener");
			sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
			return true;
		}
	};

	private View.OnLongClickListener mRwdLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mRwdLongClickListener");
			sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			return true;
		}
	};

	private View.OnLongClickListener mNextLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			startPhotoAnimator("picturePlayerFwd");
			//Ref : setPicturePlayerFwd(int value)
			return true;
		}
	};
	private View.OnLongClickListener mPrevLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			startPhotoAnimator("picturePlayerRwd");
			//Ref : setPicturePlayerRwd(int value)
			return true;
		}
	};
	
	private void startPhotoAnimator(String property){
		cancelPhotoAninmator();
		mSlideShowAnimator.setPropertyName(property);
		mSlideShowAnimator.setIntValues(0, mPlayer.getTotalDuration());
		mSlideShowAnimator.start();
	}
	
	private void cancelPhotoAninmator() {
		if (mSlideShowAnimator.isStarted()){
			mSlideShowAnimator.cancel();}
	}

	/**
	 * Animator for photoplayback continuous next key press
	 */
	private void setPicturePlayerFwd(int value) {
		LogHelper.i(TAG, "Mediakeys : setPicturePlayerFwd animator is called value : "+value);
		sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
	}

	/**
	 * Animator for photoplayback continuous next key press
	 */
	private void setPicturePlayerRwd(int value) {
		LogHelper.i(TAG, "Mediakeys : setPicturePlayerRwd animator is called value : "+value);
		sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);

	}

	private View.OnClickListener mNextListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mNextListener");
			sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
		}
	};

	private View.OnClickListener mPrevListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mPrevListener");
			sendPrevNextKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);

		}
	};

	private void sendPrevNextKeyEvent(int key) {
		LogHelper.i(TAG, " sendPrevNextKeyEvent to app "+key);
		if (mPlayer != null) {
			if (key == KeyEvent.KEYCODE_MEDIA_NEXT) {
				mPlayer.onNextKeyPressed();
				getPlayBackLayout().setPrevNextFocus(false);
			} else {
				mPlayer.onPrevKeyPressed();
				getPlayBackLayout().setPrevNextFocus(true);
			}
		}
		resetPlayControls();
		showHandler();
	}

	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mfwdspeed count");
			showHandler();
			if (mPlayer != null && isTrickModeEnabled) {
				onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_MEDIA_FAST_FORWARD));
			}
		}
	};
	private View.OnClickListener mRewListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mRewListener");
			showHandler();
			if (mPlayer != null && isTrickModeEnabled) {
				onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_MEDIA_REWIND));
			}
		}
	};

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.i(TAG, "Mediakeys : mPauseListener");
			if (mPlayer.isPlaying()) {
				isDLNAEnabled = true;
				onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_MEDIA_PAUSE));
			} else {
				isDLNAEnabled = false;
				onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_MEDIA_PLAY));
			}
		}
	};

	/**
	 * Convenience metho to set the playback pause/play mode
	 */
	private void doPauseResume() {
		LogHelper.e(TAG,"doPauseResume is called - mPlayer.isPlaying() ="
						+ mPlayer.isPlaying());
		mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);

		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			setPlaybackState(KeyEvent.KEYCODE_MEDIA_PAUSE);
		} else {
			mHandler.sendEmptyMessage(PlaybackUtils.SHOW_PROGRESS);
			mPlayer.play();
			setPlaybackState(KeyEvent.KEYCODE_MEDIA_PLAY);
		}
		resetPlayControls();
	}

	private OnDismissListener onDismissListener = new OnDismissListener() {

		public void onDismiss() {
			mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
			if (null != mViewHandler){
				mViewHandler.getPupupWindow().dismiss();}
			
			if (null != mIPbcDissmissListener){
				mIPbcDissmissListener.onPbcDismissed();}
		}
	};

	
	private AbsPlayBackControlLayout getPlayBackLayout(){
		return (AbsPlayBackControlLayout) mViewHandler.getPupupWindow().getContentView();
	}
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
		}
		public void onProgressChanged(SeekBar bar, int progress,boolean fromuser) {
			if (!fromuser){
				// We're not interested in programmatically generated changes to the progress bar's position.
				return;
			}
			long lNewposition = (((long)mPlayer.getTotalDuration()) * progress) / 1000L;
			if (mState == PlaybackUtils.NOLONGLRKEYPRSD) {
				LogHelper.d(TAG,"Called the seek To in onProgressChanged at position = "+ lNewposition);
				((ProgressBar) bar).setProgress(progress);
				mPlayer.seekTo((int) progress);
			}
		}
		public void onStopTrackingTouch(SeekBar bar) {
			LogHelper.d(TAG, "onProgressChnaged:onStopTrackingTouch");
		}
	};

	/**
	 * To set the trickmode forward and rewind speed range.
	 *
	 * 
	 * @param fwdMax
	 * @param rwdMax
	 */
	public void setTrickModeRange(int fwdMax, int rwdMax) {
		mTrickModeFwdMax = fwdMax;
		mTrickModeRwdMax = rwdMax;
	}

	/**
	 * To display the option key in the view. Default : invisible.
	 * 
	 * @param status
	 */
	public void showOptionKey(boolean status) {
		if (getPlayBackLayout() != null){
			getPlayBackLayout().showOptionsKey(status);}
	}

	public boolean keyEventDispatcherPBC(KeyEvent event) {
		return handleEvent(event);
	}

	public boolean handleEvent(KeyEvent event) {
		LogHelper.e(TAG, " handleEvent  called :  " + event);
		int lKeyCode = event.getKeyCode();
		if (slideshow) {
			cancelPhotoAninmator();
		}

		// Check for enabledTrickAndJumpMode is added to check whether
		// action on seekbar is allowed or not as In Long key press of Dpad
		// seekbar should not jump.
		int lTempKeySTate = checkLongKeyEvent(event);

		if ((lTempKeySTate != PlaybackUtils.NOLONGLRKEYPRSD)
				&& mState != PlaybackUtils.NOLONGLRKEYPRSD) {
			// Handle will tc of continuous navigation.
			return true;
		}
		if (event.getAction() == KeyEvent.ACTION_UP
				&& mState != PlaybackUtils.NOLONGLRKEYPRSD) {

			LogHelper.i(TAG, "seeking the player after long key pressed"
					+ currDurAnim);

			
			if(isSecondaryReached){
				currDurAnim += 10000;//To reach live point( there is an gap b/w live vs secondaryprogress)
				LogHelper.i(TAG, "long event : To reach live point adding +10sec"
						+ currDurAnim);
				isSecondaryReached = false;
			}
			
			if (!slideshow) {
				int totalDuration = mPlayer.getTotalDuration();
				if (totalDuration > 0) {
					if (currDurAnim == totalDuration){
						mPlayer.seekTo(currDurAnim - 10);
					}else{
						mPlayer.seekTo(currDurAnim);}
				}
			}
			mHandler.removeMessages(PlaybackUtils.LRKEYPRESSED);
			if (mTimeoutFromApplication != 0) {
				mHandler.removeMessages(PlaybackUtils.FADE_OUT);
			}
			mHandler.sendEmptyMessageDelayed(PlaybackUtils.FADE_OUT, 4000);
			mState = PlaybackUtils.NOLONGLRKEYPRSD;
			return true;
		}
		if (lTempKeySTate != PlaybackUtils.NOLONGLRKEYPRSD) {

			mEnableGetDuration = false;
			LogHelper.d(TAG, "running the message again 500");
			mHandler.removeMessages(PlaybackUtils.FADE_OUT);
			currDurAnim = mPlayer.getCurrentDuration();
			mHandler.sendEmptyMessageDelayed(PlaybackUtils.LRKEYPRESSED, 500);
			mState = lTempKeySTate;
		}

		if (mState != PlaybackUtils.NOLONGLRKEYPRSD) {
			return true;

		} else if ((mState == PlaybackUtils.NOLONGLRKEYPRSD)
				&& event.getAction() == KeyEvent.ACTION_UP
				&& ((lKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (lKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
			mHandler.removeMessages(PlaybackUtils.LRKEYPRESSED);
			mHandler.sendEmptyMessageDelayed(PlaybackUtils.FADE_OUT, 4000);
		}

		LogHelper.e(TAG, "Calling onKeyEvent  " + event);
		if (checkMediaKeyStatus(event)) {
			// Consume the Meida keys and no process, based on availability constraints.
			return true;
		}
		return onKeyEvent(event);

	}

	private int checkLongKeyEvent(KeyEvent event) {
		if (getPlayBackLayout().getSeekBarFocusability()) {

			if ((event.getRepeatCount() >= 4) && isTrickModeEnabled
					&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
					return PlaybackUtils.LONGKEYPRSDRWD;
				} else if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)) {
					return PlaybackUtils.LONGKEYPRSDFWD;
				}
			}
		}
		return PlaybackUtils.NOLONGLRKEYPRSD;
	}

	
	private void handlePausePlay(int keyCode) {
		LogHelper.e(TAG, "handlePausePlay ");

		if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
			if (!mPlayer.isPlaying()) {
				LogHelper.i(TAG, "handlePausePlay trigger play event");
				isDLNAEnabled = false;
				doPauseResume();
			} else {
				isDLNAEnabled = true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
			if (mPlayer.isPlaying()) {
				LogHelper.i(TAG, "handlePausePlay trigger pause event");
				doPauseResume();
			} else {
				// special case : To pause the trick mode, app need pause key
				// even in pause state .
				LogHelper.e(TAG, " Trick mode handlePausePlay ");
				mPlayer.pause();
				setPlaybackState(KeyEvent.KEYCODE_MEDIA_PAUSE);
			}
			isDLNAEnabled = true;
		} else {
			LogHelper.i(TAG, "handlePausePlay trigger play-pause toggle event : keyCode :"+keyCode);
			doPauseResume();
		}
		showHandler();
		getPlayBackLayout().setPlayPauseFocus();

	}
	private boolean onKeyEvent(KeyEvent event) {
		boolean retValue = false;
		int lKeyCode = event.getKeyCode();
		final boolean lUniqueDown = event.getRepeatCount() == 0
				&& event.getAction() == KeyEvent.ACTION_DOWN;

		if (checkPlaybarKeys(lKeyCode)) {
			showHandler();
		}
		LogHelper.i(TAG, " onKeyEvent lUniqueDown  " + lUniqueDown);
		if (lUniqueDown) {
			if (mHandler.hasMessages(PlaybackUtils.LRKEYPRESSED)) {
				mState=PlaybackUtils.NOLONGLRKEYPRSD;
				mHandler.removeMessages(PlaybackUtils.LRKEYPRESSED);
			}
		}

		switch (lKeyCode) {
		case KeyEvent.KEYCODE_MEDIA_PLAY:
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			if (lUniqueDown) {
				handlePausePlay(lKeyCode);
			}
			retValue = true;
			break;

		case KeyEvent.KEYCODE_MEDIA_STOP:
			if (lUniqueDown) {
				if (mPlayer != null) {
					LogHelper.e(TAG, " STOP callback to application ");
					resetPlayControls();
					mPlayer.stop();
				}
			}
			retValue = true;
			break;

		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			if (lUniqueDown && !slideshow) {
				processFFKey();
				getPlayBackLayout().setRwdFwdFocus(false);
				mTimeoutFromApplication = 0;//CR :332645
				retValue = true;
			}
			break;

		case KeyEvent.KEYCODE_MEDIA_REWIND:
			if (lUniqueDown && !slideshow) {
				processRWKey();
				getPlayBackLayout().setRwdFwdFocus(true);
				mTimeoutFromApplication = 0;//CR :332645
				retValue = true;
			}
			break;

		case KeyEvent.KEYCODE_MEDIA_NEXT:
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			if (lUniqueDown) {
				sendPrevNextKeyEvent(lKeyCode);
			}
			retValue = true;
			break;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (lUniqueDown) {
				retValue = handleSeekLeftRight(lKeyCode);
			} 
			LogHelper.i(TAG, "dpad left/right : retValue :" + retValue + "	 event  :"+event);
			return retValue;//No further action required

		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			LogHelper.i(TAG, "dpad up/down/center : retValue : false	 event  :"+event);
			return false;
		case KeyEvent.KEYCODE_BACK:
			doHide();
			retValue = false;
			break;
		default:
			retValue = false;
			break;
		}

		LogHelper.i(TAG, "onKeyEvent : retValue :" + retValue + "	 event  :"+event);
		if (retValue) {
			return retValue;
		}

		// The new API for call for unhandled keys
		if (mNewKeyRegisterListener != null) {
			retValue = mNewKeyRegisterListener.unHandledKeyEvents(event);
			LogHelper.e(TAG, "The rest unhandled key" + event
					+ " retvlaue from app : " + retValue);
		}
		mState = PlaybackUtils.NOLONGLRKEYPRSD;
		return retValue;
	}
	
	
	private boolean checkPlaybarKeys(int lKeyCode) {
		boolean lStatus = false;

		switch (lKeyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			LogHelper.i(TAG, " received DPAD keys  " + lKeyCode);
			lStatus = true;
			break;

		case KeyEvent.KEYCODE_MEDIA_PLAY:
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
		case KeyEvent.KEYCODE_MEDIA_REWIND:
		case KeyEvent.KEYCODE_MEDIA_NEXT:
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

			LogHelper.i(TAG, " received Media keys " + lKeyCode);
			lStatus = true;
			break;
		default:
			lStatus = false;
			break;
		}

		return lStatus;
	}

	private void processRWKey() {
		LogHelper.i(TAG, " processRWKey ");

		if (getPlaybackState() != PlaybackState.REWIND) {
			resetPlayControls();
			setPlaybackSpeed(PlaybackSpeed.DEFAULT);
			if (isDLNAEnabled) {
				setPlaybackSpeed(PlaybackSpeed.DEFAULT);
			} else {
				setPlaybackSpeed(PlaybackSpeed.SPEED_LEVEL3_NORMAL);
			}
		}
		setPlaybackState(KeyEvent.KEYCODE_MEDIA_REWIND);
		int nextRwdSpeed = getNextPlaybackSpeed(getPlaybackSpeed());
		mPlayer.setSpeed(false, nextRwdSpeed);
		getPlayBackLayout().updateFwdRwdStatus(false, nextRwdSpeed);

	}

	private void processFFKey() {
		LogHelper.i(TAG, " processFFKey ");
		
		if (getPlaybackState() != PlaybackState.FORWARD) {
			resetPlayControls();
			if (isDLNAEnabled) {
				setPlaybackSpeed(PlaybackSpeed.DEFAULT);
			} else {
				setPlaybackSpeed(PlaybackSpeed.SPEED_LEVEL3_NORMAL);
			}
		}
		setPlaybackState(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD);
		int nextFwdSpeed = getNextPlaybackSpeed(getPlaybackSpeed());
		mPlayer.setSpeed(true, nextFwdSpeed);
		getPlayBackLayout().updateFwdRwdStatus(true, nextFwdSpeed);

	}

	private boolean checkMediaKeyStatus(KeyEvent event) {
		int lKeyCode = event.getKeyCode();
		if (!isTrickModeEnabled && !slideshow) {
			LogHelper.e(TAG, " TrickPlayAndJumpMode is enabled ?  = "
					+ isTrickModeEnabled + " isProgJumpEnabled : "+isProgJumpEnabled);
			switch (lKeyCode) {
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				LogHelper.e(TAG,
						"Absorb this key as TrickPlayAndJumpMode is disabled "
								+ event);
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (getPlayBackLayout().getSeekBarFocusability() && !isProgJumpEnabled) {
					LogHelper.e(TAG,
							"Absorb this key as TrickPlayAndJumpMode is disabled "
									+ event);
					return true;
				} else {
					return false;
				}
			}
		}
		if (!enableNext && lKeyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
			LogHelper.e(TAG, " Next is enabled ?  = " + enableNext
					+ " Absorb this key as Next is disabled");
			return true;
		}
		if (!enablePrev && lKeyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			LogHelper.e(TAG, " Prev is enabled ?  = " + enableNext
					+ " Absorb this key as Prev is disabled");
			return true;
		}
		if (!enablePausePlay) {
			LogHelper.d(TAG, " PausePlay is enabled ?  = " + enablePausePlay);
			switch (lKeyCode) {
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
			case KeyEvent.KEYCODE_MEDIA_PLAY:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				LogHelper.e(TAG, "Absorb this key as PausePlay is disabled " + event);
				return true;
			}
		}
		return false;
	}

	private void showHandler() {
		if (mTimeoutFromApplication != 0){
			doShow(PlaybackUtils.DEFAULT_FADEOUT_TIME);
		}else{
			doShow(0);
			}
		}	

	/**
	 * To get the next playback speed .
	 * 
	 * @param currentSpeed
	 * @return
	 */
	private int getNextPlaybackSpeed(int currentSpeed) {
		int lNextSpeed = currentSpeed;
		LogHelper.i("PlayBackControl",
				"SlowPlayControl => getNextPlaybackSpeed - currentSpeed="
						+ currentSpeed);

		if (isSlowPlayEnabled
				&& currentSpeed < PlaybackSpeed.SPEED_LEVEL3_NORMAL) {
			// Slow play jump
			lNextSpeed = PlaybackUtils.getNextSlowPlaySpeed(currentSpeed);
			if (lNextSpeed == PlaybackSpeed.SPEED_LEVEL3_NORMAL) {
				isDLNAEnabled = false;
			}
		} else {
			isDLNAEnabled = false;
			lNextSpeed = PlaybackUtils.getNextNormalPlaySpeed(currentSpeed);
			if (lNextSpeed == PlaybackSpeed.SPEED_LEVEL3_NORMAL) {
				isDLNAEnabled = false;
			}
		}
		if (getPlaybackState() == PlaybackState.FORWARD
				&& lNextSpeed > mTrickModeFwdMax) {
			lNextSpeed = PlaybackSpeed.DEFAULT;
		} else {
			if (lNextSpeed > mTrickModeRwdMax) {
				lNextSpeed = PlaybackSpeed.DEFAULT;
			}
		}
		setPlaybackSpeed(lNextSpeed);
		return lNextSpeed;
	}

	private boolean handleSeekLeftRight(int keyCode) {
		LogHelper.d(TAG, "  handleSeekLeftRight  keyCode:  "+keyCode);
		if (!getPlayBackLayout().getSeekBarFocusability()) {
			return false;
		}
		LogHelper.i(TAG, "  getSeekBarFocusability true, handling seekbar navigation");
	
		mHandler.removeMessages(PlaybackUtils.SHOW_PROGRESS);
		mEnableGetDuration = false;
		if (currDurAnim == 0) {
			currDurAnim = mPlayer.getCurrentDuration();
		}
			if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode){
				currDurAnim -= 10000;
			}else{
				currDurAnim += 10000;
			}
			if (currDurAnim <= 0) {
				currDurAnim = 0;}
		LogHelper.d(TAG, "currDurAnim" + currDurAnim);
		mHandler.removeMessages(PlaybackUtils.FADE_OUT);
		mHandler.sendEmptyMessageDelayed(PlaybackUtils.FADE_OUT, 4000); 
		// To check secondary proglimit and go to buffered duration seek..
		if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
			checkSecondaryProgLimit();
		}
		
		if(isSecondaryReached){
			currDurAnim += 10000;//To reach live point( there is an gap b/w live vs secondaryprogress)
			isSecondaryReached = false;
			LogHelper.i(TAG, " handleright : to reach live point adding +10sec"
					+ currDurAnim);
		}
		mPlayer.seekTo(currDurAnim);

		return true;
	}

	private boolean isSecondaryReached =false;
	/**
	 * Convenience method to ensure that progress bar jump is not exceeding the
	 * secondary progress limit if it is enabled.
	 * 
	 */
	private void checkSecondaryProgLimit() {
		LogHelper.i(TAG, " checkSecondaryProgLimit isSecondaryProgressEnabled "
				+ isSecondaryProgressEnabled);
		if (isSecondaryProgressEnabled) {
			int lTotDur = mPlayer.getTotalDuration();

			int lSecDuration = mPlayer.getBufferDurationMillis();
			LogHelper.d(TAG, " duration tot : " + lTotDur + " lsecbuff :"
					+ lSecDuration);
			if (currDurAnim >= lSecDuration) {
				currDurAnim = lSecDuration;
				LogHelper.i(TAG,
						" exceeding the total duration, resetting to lSecDuration duration"
								+ currDurAnim + "  lSecDuration : "
								+ lSecDuration);
				isSecondaryReached = true;
			} else {
				
				LogHelper.i(TAG,
						" current duration withing secondary limit  currDurAnim "
								+ currDurAnim + "  lSecDuration : "
								+ lSecDuration);
			}
		}
	}
	
	/**
	 * Returns status of window whether it is shown or not
	 * 
	 * @return true or false
	 */

	public boolean isShowing() {
		if (mViewHandler != null) {
			return mViewHandler.getPupupWindow().isShowing();
		}
		return false;
	}

	/**
	 * Convenience method to enable the progress jump incase of trickmode is
	 * disabled/fwd-rwd controls are disabled. If trickmode is enabled then no
	 * need to set this API, by default trick mode will allow the progress jump.
	 * This is the special case Ref : PR - 343154
	 * 
	 * @param isProgJumpEnabled
	 *            the isProgJumpEnabled to set
	 */
	public void setProgJumpEnabled(boolean isProgJumpEnabled) {
		this.isProgJumpEnabled = isProgJumpEnabled;
		LogHelper.d(TAG, " setProgJumpEnabled :  " + isProgJumpEnabled);
	}

}
