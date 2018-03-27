package ui;

import java.util.Formatter;
import java.util.Locale;

import fany.phpuijar.R;
import ui.SlowPlayControl.ISlowPlayControlCallback;
import ui.SlowPlayControl;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import ui.utils.LogHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
/**
* @deprecated use new play component which is part of media package
*/				
public class PlayBackControl implements PlayerBottomLayout.IDelegateEventListener, ISlowPlayControlCallback {
	protected final View anchor;
	private final PopupWindow windowtop;
	private final PopupWindow windowbottom;
	private View root,top;
	private Drawable background = null;
	private final WindowManager windowManager;
	private ImageButton mPauseButton;
	private ImageButton mFfwdButton;
	private ImageButton mRewButton;
	private ImageButton mNextButton;
	private ImageButton mPrevButton;
	private ObjectAnimator mLinearObjectAnimator;
	private int mDurationAnimation = 0;
		
	private KeyRegisterListener mKeyRegisterListener=null;
	// Added new Interface for handling the Key Events
	private NewKeyRegisterListener mNewKeyRegisterListener = null;
	private static String LOG_TAG = PlayBackControl.class.getSimpleName();
	private MediaPlayerControl mPlayer;
	//private boolean mDragging;
	private ProgressBar mProgress;
	private TextView mTime, mDuration;
	private TextView mFileSpeed,dolbyDtsText;
	private TextView mFileMetaData;
	private boolean longkeypressedleft = false;
	private boolean longkeypressedright = false;	
	StringBuilder mFormatBuilder;
	Formatter mFormatter;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int SECONDARYPROGRESS = 3;
	private static final int LRKEYPRESSED = 4;
	private static final int NOLONGLRKEYPRSD=0;
	private int state = NOLONGLRKEYPRSD;
	private static final int sDefaultTimeout = 4000;
	private static final int LONGKEYPRSDFWD = 2;
	private static final int LONGKEYPRSDBCK = 1;
	private int currDurAnim = 0; 
	private boolean mInfinte = false;
	private boolean mEnableGetDuration = true;	
	private boolean slideshow=false;
	private String mFileName = " ";
	private String mMetaDataPath = " ";
	public static int image_position = 0;
	private View viewToFocus = null;	
	private IPbcDissmissListener mIPbcDissmissListener;
	private MediaPlayerControlNew mMediaPlayerControlNew;
	private int mProgressBarHeight;
	private int mSlideShowTotalDuration = 0;
	private int interval = 1000;
	private String mDolbyText = null;
	
	private SlowPlayControl rewindSlowPlayControl = null;
	private SlowPlayControl forwardSlowPlayControl = null;
	private int playbackState = PlaybackState.PLAY;
	private int playbackSpeed = PlaybackSpeed.DEFAULT;
	private boolean slowPlayEnabled = false;
	private int timeoutFromApplication = 0;
	private boolean secondaryProgressEnabled = false;
	private int secoondaryprogress = 0;
	private boolean enabledTrickAndJumpMode = true;
	private boolean enableWrapAround = true;
	private boolean enableNext = true;
	private boolean enablePrev = true;
	private boolean enablePausePlay = true;
	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public PlayBackControl(final View anchor, boolean slideshow, String uri) {
		LogHelper.d(LOG_TAG, "PlayBackControl()" + anchor + "uri" + uri);
		this.anchor = anchor;
		mLinearObjectAnimator=new ObjectAnimator();
		mLinearObjectAnimator.setDuration(mDurationAnimation);
		mLinearObjectAnimator.setInterpolator(new LinearInterpolator());
		this.slideshow = slideshow;
		this.windowtop = new PopupWindow(anchor.getContext()); 
		windowbottom = new PopupWindow(anchor.getContext());
		windowbottom.setTouchable(true);
		windowbottom.setFocusable(true);
		windowbottom.setOutsideTouchable(false);
		this.windowManager = (WindowManager) this.anchor.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowbottom.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		windowbottom.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		windowbottom.setOutsideTouchable(false);
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(
				R.layout.control, null);
		PlayerBottomLayout playerBottomLayout = (PlayerBottomLayout) root;
		playerBottomLayout.setIDelegateEventListener(this);
		windowbottom.setContentView(root);
		root.setOnKeyListener(keylistener);	
		windowbottom.setOnDismissListener(onDismissListener);
		ViewGroup top = (ViewGroup)inflater.inflate(R.layout.controltop, null);
		root.setOnHoverListener(mRootHoverListener);
		windowtop.setContentView(top);
		windowbottom.setContentView(root);
		root.setFocusable(true);
		root.setFocusableInTouchMode(true);
		root.requestFocus();
		rewindSlowPlayControl=(SlowPlayControl)root.findViewById(R.id.slowplaycontrolone);
		forwardSlowPlayControl=(SlowPlayControl)root.findViewById(R.id.slowplaycontroltwo);
		SlowPlayControl.registerSlowPlayControlCallback(this);
		
		mFileSpeed = (TextView) top.findViewById(R.id.TxtFileNm);
		mFileMetaData = (TextView) top.findViewById(R.id.TxtMetaDataId);
		dolbyDtsText=(TextView) top.findViewById(R.id.TxtMetaDataLogo);
		initControllerView(this.root);
		if(mProgress != null){
			mLinearObjectAnimator.setTarget(this);
		}

		//mSmallThumbDrawable = anchor.getResources().getDrawable(R.drawable.circle_shape);
		//mBigThumbDrawable = anchor.getResources().getDrawable(R.drawable.circle_shape_big);
		mProgressBarHeight = anchor.getResources().getDimensionPixelSize(R.dimen.playbackcontrol_progressbar_height);
	}
	/**
	 * The param has to be set by the class who has implemented the KeyRegisterListener to get the unhandled keys.
	 * @param keyRegisterListener
	 * AN-766
	 */
	public void registerKeyListenerUnhandledKeys(KeyRegisterListener keyRegisterListener){
		mKeyRegisterListener = keyRegisterListener;
	}
	// The New Public API for observer.
	public void registerNewKeyListenerUnhandledKeys(NewKeyRegisterListener keyRegisterListener){
                mNewKeyRegisterListener = keyRegisterListener;
        }
	
	OnDismissListener onDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			mHandler.removeMessages(SHOW_PROGRESS);
			if(null != windowtop)
			windowtop.dismiss();
			if(null != windowbottom)
			windowbottom.dismiss();
			if(null != mIPbcDissmissListener)
			mIPbcDissmissListener.onPbcDismissed();
		}
	};
/**
 * To set Name of a file to be played at top of window
 * @param FileName
 */
	public void setTopWindowString(String FileName){
		mFileSpeed.setText(FileName);
		mFileName = FileName;
		LogHelper.d(LOG_TAG,"mFileName"+mFileName);
	}
	
	/**To be called from application to set Text for Dolby/DTS type of video.
	 * 
	 * @param text = DolbyDtsText
	 */
	public void setDolbyDtsText(String text){
		if(text != null){
			mDolbyText = text;
			LogHelper.d(LOG_TAG," setDolbyDtsText = "+mDolbyText +" dolbyDtsText = "+dolbyDtsText);
			dolbyDtsText.setVisibility(View.VISIBLE);
			dolbyDtsText.setText(mDolbyText);
		}
		else{
			mDolbyText = null;
			dolbyDtsText.setVisibility(View.GONE);
		}
	}
	
	/**
	 * To set Other information about file to be played at top of window below Name of file
	 * @param FileName
	 */
	public void setTopWindowMetaDataString(String MetaData){
		LogHelper.d(LOG_TAG,"Setting the meta data for the filename");
		mFileMetaData.setText(MetaData);
		mMetaDataPath = MetaData;
	}
	
	
	private void preShow() {
		if (this.root == null) {
			throw new IllegalStateException(
					"setContentView was not called with a view to display.");
		}
		if (this.background == null) {
			//this.windowbottom.setBackgroundDrawable(new BitmapDrawable());
			this.windowbottom.setBackgroundDrawable(null);
			this.windowtop.setBackgroundDrawable(new BitmapDrawable());
		} else {
			//this.windowbottom.setBackgroundDrawable(this.background);
			this.windowbottom.setBackgroundDrawable(null);
			this.windowtop.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of
		// the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable
		this.windowbottom.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		this.windowbottom.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.windowtop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		this.windowtop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		
	}

	/**
	 * Sets the background drawable for window
	 * 
	 * @param background
	 */
	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}
	
	/**
	 * The windowBottom -> root when it gets the focus we are not able to navigate using cursor keys
	 * on the buttons of play ffwd and rew and progress bar.
	 * when we use windowbottom -> root to focusable false we are able to navigate but we dont get key event so 
	 * we used keyListner on all the views.
	 */
	
	OnKeyListener keylistener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
				
			return onKeyEvent(event);
		}
		
	};
	private boolean onKeyEvent(KeyEvent event){
		
			int keyCode = event.getKeyCode();
			{
				//Check for enabledTrickAndJumpMode is added to check whether action on seekbar is allowed or not as In Long key press of Dpad seekbar should not jump.
				longkeypressedleft = (event.getRepeatCount() >= 4 ) && enabledTrickAndJumpMode && (event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
				longkeypressedright = (event.getRepeatCount() >= 4) && enabledTrickAndJumpMode && (event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT);
	        	if((longkeypressedleft || longkeypressedright) && state !=NOLONGLRKEYPRSD){
	        		return true;
	       		} 
	        	if((event.getAction() == KeyEvent.ACTION_UP) && ((!longkeypressedleft)&&((!longkeypressedright)))&&(state != NOLONGLRKEYPRSD)){
				interval = 1000;
	        		longkeypressedleft = false;
	        		longkeypressedright = false;
	        		LogHelper.d(LOG_TAG,"seeking the player after long key pressed"+currDurAnim);	
					if(!slideshow){
						int totalDuration =  mPlayer.getTotalDuration();
						if(totalDuration > 0){
							if(currDurAnim == totalDuration){
								
	        						mPlayer.seekTo(currDurAnim-10);
							}else{
								mPlayer.seekTo(currDurAnim);
							}
							
						}
						
					}else{
					
						mPlayer.seekTo(currDurAnim);
					}
	        		mHandler.removeMessages(LRKEYPRESSED);
				if(mHandler.hasMessages(FADE_OUT) && !mInfinte){
					mHandler.removeMessages(FADE_OUT);
				}	
			        mHandler.sendEmptyMessageDelayed(FADE_OUT, 4000);
				state = NOLONGLRKEYPRSD;
	        		return true;
	        	}
	        	if(longkeypressedleft){
	        		if(state == NOLONGLRKEYPRSD && (event.getRepeatCount() >= 4)){
					if(slideshow){
						interval = 300;
					}
					mEnableGetDuration = false;
	        			LogHelper.d(LOG_TAG, "running the message again 500");
					mHandler.removeMessages(FADE_OUT);
					{
	        				currDurAnim = mPlayer.getCurrentDuration();
					}
	        			mHandler.sendEmptyMessageDelayed(LRKEYPRESSED,500);
	        			state = LONGKEYPRSDBCK;
	        		}
	        	}
	        	if(longkeypressedright){
	        		
	        		if(state == NOLONGLRKEYPRSD && (event.getRepeatCount() >= 4)){
					if(slideshow){
						interval = 300;
					}
					mEnableGetDuration = false;
	        			LogHelper.d(LOG_TAG, "running the message again 500");
					mHandler.removeMessages(FADE_OUT);
					{
						currDurAnim = mPlayer.getCurrentDuration();
					}
		        		mHandler.sendEmptyMessageDelayed(LRKEYPRESSED,500);
		        		state = LONGKEYPRSDFWD;
	        		}
	        	}

	        	if(longkeypressedleft || longkeypressedright){
	        		return true;
	        	
	        	}else if((state ==NOLONGLRKEYPRSD)&& event.getAction() == KeyEvent.ACTION_UP && ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)||(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))){
	        		mHandler.removeMessages(LRKEYPRESSED);
	        		mHandler.sendEmptyMessageDelayed(FADE_OUT,4000);
	        	}
			}
		LogHelper.d(LOG_TAG,"Calling HandleEvent "+event);	
	    	return handleEvent(event);
	}
	public void onSeekCompleted(){
		LogHelper.d(LOG_TAG,"onSeekCompleted is called");
		state = NOLONGLRKEYPRSD;
		currDurAnim = 0;
		mEnableGetDuration = true;
	        mHandler.sendEmptyMessage(SHOW_PROGRESS);
	}
	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		this.windowbottom.setOnDismissListener(listener);
		this.windowtop.setOnDismissListener(listener);
	}

	/**
	 * Displays like a popdown menu from the anchor view
	 */
	//public void showLikePopDownMenu() {
	//	this.showLikePopDownMenu(0, 0);
	//}

	/**
	 * Displays like a popdown menu from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in X direction
	 * @param yOffset
	 *            offset in Y direction
	 */
	public void showLikePopDownMenu(int xOffset, int yOffset) {
		this.preShow();
		this.windowbottom.showAsDropDown(this.anchor, xOffset, yOffset);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		this.showLikeQuickAction(0, 0);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public void showLikeQuickAction(int xOffset, int yOffset) {
		this.preShow();

		// this.window.setAnimationStyle(R.style.Animations_GrowFromBottom);
		// why is this needed when we are already sending the x,y cordinate?
		int[] location = new int[2];
		this.anchor.getLocationOnScreen(location);
		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();
				// fix for unknown time playback
		if (mPlayer.getTotalDuration() > 0 && slideshow ) {
			this.windowbottom.showAtLocation(this.anchor, Gravity.BOTTOM, 0, 0);
		// need to change
			this.windowbottom.update(0, 0, screenWidth, anchor.getResources().getDimensionPixelSize(R.dimen.layoutheight));
			this.windowbottom.setOutsideTouchable(false);
			this.windowbottom.getContentView().setOnKeyListener(keylistener);
		}else if(!slideshow){
			this.windowbottom.showAtLocation(this.anchor, Gravity.BOTTOM, 0, 0);
			// need to change
			this.windowbottom.update(0, 0, screenWidth, anchor.getResources().getDimensionPixelSize(R.dimen.layoutheight));
			this.windowbottom.setOutsideTouchable(false);
			this.windowbottom.getContentView().setOnKeyListener(keylistener);
		}
		this.windowtop.showAtLocation(this.anchor, Gravity.TOP, 0, 0);
		this.windowtop.update(0, 0, screenWidth, WindowManager.LayoutParams.WRAP_CONTENT);
		updatePausePlay();
		if(!mHandler.hasMessages(SHOW_PROGRESS)){
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
		else{
			if(mHandler.hasMessages(FADE_OUT) && !mInfinte){
				mHandler.removeMessages(FADE_OUT);
				mHandler.sendEmptyMessageDelayed(FADE_OUT,4000);
			}
		}
	}

	/**
	 * Dismiss the popupwindow
	 */

	public void dismiss() {
		LogHelper.d(LOG_TAG," Dismiss is called from application ");
		doDismiss();
	}
	
	private void doDismiss(){
		doHide();
		/*mHandler.removeMessages(SHOW_PROGRESS);
		mHandler.removeMessages(FADE_OUT);
		mHandler.removeMessages(LRKEYPRESSED);
		state = NOLONGLRKEYPRSD;
		viewToFocus = null;
		mHandler.removeMessages(SECONDARYPROGRESS);
		this.windowbottom.dismiss();
		windowtop.dismiss();*/
	}
	
	/**
	 * Anything you want to have happen when created. Probably should create a
	 * view and setup the event listeners on child views.
	 */

	private void initControllerView(View v) {
		mPauseButton = (ImageButton) v.findViewById(R.id.pause);
		if (mPauseButton != null) {
			mPauseButton.setOnClickListener(mPauseListener);
		}
		mFfwdButton = (ImageButton) v.findViewById(R.id.ffwd);
		if (mFfwdButton != null) {
			mFfwdButton.setOnClickListener(mFfwdListener);
			
		}

		mRewButton = (ImageButton) v.findViewById(R.id.rew);
		if (mRewButton != null) {
			mRewButton.setOnClickListener(mRewListener);
			
		}

		
		
		mNextButton = (ImageButton) v.findViewById(R.id.nextButton);
		if (mNextButton != null) {
			mNextButton.setOnClickListener(mNextListener);
			
		}

		mPrevButton = (ImageButton) v.findViewById(R.id.prevbutton);
		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(mPrevListener);
			
		}
		mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
		
		if (mProgress != null) {
		LayoutParams lp = (LayoutParams) mProgress.getLayoutParams();
		lp.height = 13;
		mProgress.setLayoutParams(lp);
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
			}
			mProgress.setOnTouchListener(touchListener);
			mProgress.setOnHoverListener(mSeekHoverListener);

		}

		mTime = (TextView) v.findViewById(R.id.time_current);
		mDuration = (TextView) v.findViewById(R.id.time);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}
	/**
	 * To fix the issue number AN-471
	 */
	
	OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				//showHandler();
				mHandler.post(new Runnable() {
					public void run() {
						changeLayoutParam(false);
					}
				});
				break;
			case MotionEvent.ACTION_UP:
				int location[]=new int[2];
				v.getLocationOnScreen(location);
				Rect rect = new Rect();
				v.getDrawingRect(rect);
				boolean test = false;
				if(rect.contains((int)event.getX(), (int)event.getY())){
					//doShow(sDefaultTimeout);
	            	 mHandler.post(new Runnable() {
						 public void run() {
							changeLayoutParam(false);
						 }
					});
				}else{
					//doShow(sDefaultTimeout);
	            	 mHandler.post(new Runnable() {
						 public void run() {
							changeLayoutParam(true);
							 mProgress.requestLayout();
							 mProgress.invalidate();
						}
					});
				}
			default:
				
				break;
			}
			return false;
		}
	};

	private View.OnClickListener mNextListener = new View.OnClickListener() {
		public void onClick(View v) {
			showHandler();
			if(mMediaPlayerControlNew != null){
				mMediaPlayerControlNew.onNextKeyPressed();
			
			}
		}
	};
	private View.OnClickListener mPrevListener = new View.OnClickListener() {
		public void onClick(View v) {
			showHandler();
			if(mMediaPlayerControlNew != null){
				mMediaPlayerControlNew.onPrevKeyPressed();
			}
		}
	};
	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.d(LOG_TAG,"mfwdspeed count");
			longkeypressedleft = false;
			longkeypressedright = false;
			mHandler.removeMessages(FADE_OUT);
			showHandler();
			if(mPlayer != null){
				if(mPlayer instanceof MediaPlayerWithTrickModes){
					handleEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_FAST_FORWARD));
				}else{			
					if(mPlayer.onForwardKeyPressed()){
						updatePausePlay();
					}
				}
			}
		}
	};
	private View.OnClickListener mRewListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.d(LOG_TAG, "mRewListener" + slideshow);
			showHandler();
			
			if(mPlayer != null){
				if(mPlayer instanceof MediaPlayerWithTrickModes){
					handleEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_REWIND));
				}else{	
					if(mPlayer.onRewindKeyPressed()){
						
						updatePausePlay();		
					}
				}
			}
		}
	};
	
	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			LogHelper.d(LOG_TAG, "mPauseListener");
			if(mPlayer instanceof MediaPlayerWithTrickModes && slowPlayEnabled){
				resetSlowPlayControlsInternal();
			}
			
			doPauseResume();
			showHandler();
			mFileSpeed.setText(mFileName);
			mFileMetaData.setText(mMetaDataPath);
		}
	};
	
	public void setSpeedText(String speedText){
		LogHelper.d(LOG_TAG,"mFileSpeed.setText"+speedText);	
		mFileName = speedText;
			mFileSpeed.setText(speedText);	
	}
	// Fix for flickering issue 23429,22184.
	public void setMetaDataText(String mMetaDataText){
		mMetaDataPath = mMetaDataText;
		mFileMetaData.setText(mMetaDataText);	
	}
	private void doPauseResume() {
		LogHelper.d(LOG_TAG,"doPauseResume is called - mPlayer.isPlaying() ="+mPlayer.isPlaying());
		
		if(mHandler.hasMessages(SHOW_PROGRESS)){
			mHandler.removeMessages(SHOW_PROGRESS);
		}
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
			mPlayer.play();
		}

		updatePausePlay();
		
	}
	
	private void setSecondaryProgress(int percentage){
		long secondaryupdate = (mProgress.getMax()*(long)percentage)/100;
		LogHelper.d(LOG_TAG," setSecondaryProgress to "+secondaryupdate);
		mProgress.setSecondaryProgress((int)secondaryupdate);
		if(percentage ==100){
			mHandler.removeMessages(SECONDARYPROGRESS);
		}
	}
	
	public void enableSecondaryUpdate(){
		// start running a handler and update the secondary value
		secondaryProgressEnabled = true;
		Message msg = Message.obtain();
		msg.what = SECONDARYPROGRESS;
		mHandler.sendMessage(msg);
	}
	
	public void disableSecondaryUpdate(){
		secondaryProgressEnabled = false ;
		if(mHandler.hasMessages(SECONDARYPROGRESS)){
			mHandler.removeMessages(SECONDARYPROGRESS);
		}
	}
	
	public void removeSecondaryProgress(){
		mProgress.setSecondaryProgress(0);
	}
	
	private int setProgress() {
		LogHelper.d(LOG_TAG,"The progress will be shown"+currDurAnim);
		if (mPlayer == null )  {
			return 0;
		}
		/*if(!slideshow){
			if(longkeypressedleft || longkeypressedright ){
				return 0;
			}
		}*/
		int position = -1;
		int duration = mPlayer.getTotalDuration();
		if(!slideshow){
			
			if( mEnableGetDuration ){
				position = mPlayer.getCurrentDuration();
			}
			else{
				if(currDurAnim < 0){
					currDurAnim = 0;
				}else if(currDurAnim > duration){
					currDurAnim = duration;
				}else{ 	
					position = currDurAnim;
				}
			}
		
		}else if(slideshow){
			if(duration != mSlideShowTotalDuration){
				mSlideShowTotalDuration = duration;
				mProgress.setMax(mSlideShowTotalDuration);
			}
			if( mEnableGetDuration ){
				position = mPlayer.getCurrentDuration();
				LogHelper.d(LOG_TAG, " getCurrentDuration = "+position);
			}else{
				LogHelper.d(LOG_TAG, "in Else part currDurAnim = " +currDurAnim);
				if(currDurAnim < 0){
					currDurAnim = duration;
				}else if(currDurAnim > duration){
					currDurAnim = 0;
				}else{
					position = currDurAnim;
				}
			}
			
		}
	
		LogHelper.d(LOG_TAG,"position = "+position);
		if (mProgress != null && !slideshow) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
				
			}else{
				mProgress.setProgress(0);
			}

			
		}else if(mProgress != null && slideshow){
			
			mProgress.setProgress(position);
		}

		if (mTime != null) {
			if (slideshow) {
				mTime.setText(String.valueOf(position));
				mDuration.setText(String.valueOf(duration));
			} else {
				mTime.setText(stringForTime(position));
				mDuration.setText(stringForTime(duration));
			}

		}
		
		LogHelper.d(LOG_TAG,"The progress will be shown end");

		return position;
	}
	/***
	 * Added to let the playback be visible on the screen.
	 */
	private OnHoverListener mRootHoverListener = new OnHoverListener() {
		
		@Override
		public boolean onHover(View v, MotionEvent event) {
			if(windowbottom.isShowing()){
				if(!mInfinte){
					mHandler.removeMessages(FADE_OUT);
					//mHandler.sendEmptyMessage(SHOW_PROGRESS);
					mHandler.sendEmptyMessageDelayed(FADE_OUT,4000);
				}
			}
			return false;
		}
	};

	private OnHoverListener mSeekHoverListener = new OnHoverListener() {

		@Override
		public boolean onHover(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
					changeLayoutParam(false);

			} else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
					changeLayoutParam(true);
			}
			return false;
		}
	};

	private void changeLayoutParam(boolean isNormal) {
		if (mProgress == null || !(mProgress instanceof SeekBar))
			return;

		if (isNormal) {
			LayoutParams params = mProgress.getLayoutParams();
			if (params.height != mProgressBarHeight) {
				params.height = mProgressBarHeight;
				mProgress.setLayoutParams(params);
			}
			// seekBar.setMinimumHeight(dpToPx(20));
			// seekBar.setThumbOffset(dpToPx(5));
		} else {
			LayoutParams params = mProgress.getLayoutParams();
			if (params.height != LayoutParams.WRAP_CONTENT) {
				params.height = LayoutParams.WRAP_CONTENT;
				mProgress.setLayoutParams(params);
			}
			// seekBar.setMinimumHeight(dpToPx(20));
			// seekBar.setThumbOffset(dpToPx(10));
			// seekBar.setThumb(getResources().getDrawable(R.drawable.circle_shape));
		}
	}

	// There are two scenarios that can trigger the seekbar listener to trigger:
	//
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {

			//mDragging = true;
			
			
			// By removing these pending progress messages we make sure
			// that a) we won't update the progress while the user adjusts
			// the seekbar and b) once the user is done dragging the thumb
			// we will post one of these messages to the queue again and
			// this ensures that there will be exactly one message queued up.
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}
			 
			long duration = mPlayer.getTotalDuration();
			long newposition = (duration * progress) / 1000L;
			if(slideshow){
				if(duration == 1){
					((ProgressBar)bar).setProgress(progress);
					mPlayer.seekTo(1);
				}else{
					((ProgressBar)bar).setProgress(progress);
					LogHelper.d(LOG_TAG, " in onProgressChanged position is "+progress);
					if(progress == 0){
						progress = 1;
					}
					mPlayer.seekTo(progress);
				}
			}else{
				if(state == NOLONGLRKEYPRSD){
					LogHelper.d(LOG_TAG,"Called the seek To in onProgressChanged at position = "+newposition);
					((ProgressBar)bar).setProgress(progress);
					mPlayer.seekTo((int) newposition);
				}
			}
			
			
			if (mTime != null) {

				if (slideshow) {
					image_position = (int) progress;
					mTime.setText(String.valueOf(progress));
					mDuration.setText(String.valueOf(duration));
				} else {
					mTime.setText(stringForTime((int) newposition));
					mDuration.setText(stringForTime((int) duration));
				}
			}
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendEmptyMessageDelayed(FADE_OUT, 4000);
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			LogHelper.d(LOG_TAG, "onProgressChnaged:onStopTrackingTouch");
			resetSlowPlayControlsInternal(); // After Manual Seek,Play bar should reset Speed etc.
		//	mDragging = false;
			//setProgress();
			//mHandler.sendEmptyMessage(SHOW_PROGRESS);
			mHandler.removeMessages(FADE_OUT);
			// if(!mInfinte)
				// mHandler.sendEmptyMessageDelayed(FADE_OUT, 4000);
			

			// Ensure that progress is properly updated in the future,
			// the call to show() does not guarantee this because it is a
			// no-op if we are already showing.

		}
	};

	/**
	 * Returns status of window whether it is shown or not
	 * 
	 * @return true or false
	 */

	public boolean isShowing() {
		
		return this.windowbottom.isShowing();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos = 0;
			switch (msg.what) {
			case FADE_OUT:
				LogHelper.d(LOG_TAG,"hide is called here");
				if(!mInfinte)
				doHide();
				break;
			case SHOW_PROGRESS:
				LogHelper.d(LOG_TAG,"SHOW_PROGRESS");
				updatePausePlay();
				setProgress();
				if ( windowbottom.isShowing()) {
					//msg = obtainMessage(SHOW_PROGRESS);
					
					//sendMessageDelayed(msg, 1000 - (pos % 1000));
					sendEmptyMessageDelayed(SHOW_PROGRESS, interval);
					
				}
				break;
			case SECONDARYPROGRESS:
				
				secoondaryprogress = mPlayer.getBufferPercentage();
				setSecondaryProgress(secoondaryprogress);
				msg = obtainMessage(SECONDARYPROGRESS);
				sendMessageDelayed(msg, 1000);
				if(secoondaryprogress==100){
					removeMessages(SECONDARYPROGRESS);
				}
				break;
			case LRKEYPRESSED:
				LogHelper.d(LOG_TAG,"LONGKEYPRESSED" + state + "drt =" + System.currentTimeMillis());
				/*if(!slideshow)*/{
				mEnableGetDuration = false;
				if(state == LONGKEYPRSDBCK){
					if(!slideshow){
						incrementPlayBack(-9000);
					}
					else{
						incrementPlayBack(-1);
					}
				}
				if(state == LONGKEYPRSDFWD){
					if(!slideshow)
						incrementPlayBack(9000);
					else{
						incrementPlayBack(1);
					}
				}
				if((state == LONGKEYPRSDBCK || state == LONGKEYPRSDFWD) && mPlayer.getCurrentDuration() > 0 && ( mPlayer.getTotalDuration()) > mPlayer.getCurrentDuration()){
					if(!slideshow){
						sendEmptyMessageDelayed(LRKEYPRESSED, 50);
					}else{
						sendEmptyMessageDelayed(LRKEYPRESSED, 300);
					}
				}/*else{
					if(currDurAnim <= mPlayer.getTotalDuration() && currDurAnim >=0){
						mPlayer.seekTo(currDurAnim);
					}else{
						if(mPlayer.getTotalDuration() > currDurAnim){
							mPlayer.seekTo(mPlayer.getTotalDuration());
						}else if(currDurAnim < 0){
							mPlayer.seekTo(0);
						}
					}
					removeMessages(LRKEYPRESSED);
					state = NOLONGLRKEYPRSD;
				}*/
				}
				break;
			}
		}
	};
	/**
	 *  Issue number 1771
	 * @param enabled
	 */
	public void setEnabledFfwd(boolean enabled) {
        if (mFfwdButton != null) {
                mFfwdButton.setEnabled(enabled);
                mFfwdButton.setFocusable(enabled);
        }
	}
	/**
	 *  Issue number 1771
	 * @param enabled
	 */
	public void setEnabledRew(boolean enabled) {
        if (mRewButton != null) {
                mRewButton.setEnabled(enabled);
                mRewButton.setFocusable(enabled);
        }
	}
	/**
	 * Enable or disable Next button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */
	
	public void setEnabledNext(boolean enabled) {
        if (mNextButton != null) {
        	enableNext = enabled;
        	mNextButton.setEnabled(enabled);
        	mNextButton.setFocusable(enabled);
        }
	}
	/**
	 * Enable or disable Prev button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */
	public void setEnabledPrev(boolean enabled) {
        if (mPrevButton != null) {
        	enablePrev = enabled;
        	mPrevButton.setEnabled(enabled);
        	mPrevButton.setFocusable(enabled);
        }
	}
	
	/**
	 * Enable or disable Play/Pause button
	 * 
	 * @param enabled
	 *            if true button is enabled false button is not enabled
	 */
	public void setEnabledPausePlay(boolean enabled) {
        if (mPauseButton != null) {
        	enablePausePlay = enabled;
        	mPauseButton.setEnabled(enabled);
        	mPauseButton.setFocusable(enabled);
        }
	}
	
	/**
	 *  Issue number 1771
	 * @param enabled
	 */
	
	public void setEnabledProgress(boolean enabled) {
        if (mProgress != null) {
                mProgress.setEnabled(enabled);
                mProgress.setFocusable(enabled);
        }
	}


	/**
	 * set the enability of buttons
	 * 
	 * @param enabled
	 *            if true buttons are enabled false button is not enabled
	 */
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null) {
			mPauseButton.setEnabled(enabled);
			mPauseButton.setFocusable(enabled);
		}
		if (mFfwdButton != null) {
			mFfwdButton.setEnabled(enabled);
			mFfwdButton.setFocusable(enabled);
		}
		if (mRewButton != null) {
			mRewButton.setEnabled(enabled);
			mRewButton.setFocusable(enabled);
		}

		if (mProgress != null) {
			mProgress.setEnabled(enabled);
			mProgress.setFocusable(enabled);
			
		}
	}
	/**
	 * Interface to be implemented as a callback
	 * 
	 * @author nischitha.d
	 * @modified by arun.gupta
	 * The canPause is removed from here.
	 * The stop() is added for syopping the mediaPlayer.
	 * This will be a interface to call all the method related to the mediaPlayer
	 * this will be informing the playback control on the MediaPlayer state.
	 */
	public interface KeyRegisterListener {
		public void unHandledKeys(KeyEvent keyEvent);

	}
	public interface NewKeyRegisterListener extends KeyRegisterListener{
		public boolean unHandledKeyEvents(KeyEvent keyEvent);
	}
	public interface MediaPlayerControl {
		
		void seekTo(int pos);

        void play();

        void pause();

        int getTotalDuration();

        int getCurrentDuration();

        boolean isPlaying();

        int getBufferPercentage();

        boolean onForwardKeyPressed();

        boolean onRewindKeyPressed();
        // fix for the build break 
        //void stop();
	}
	/**
	 * issue AN-1787
	 * @author arun.gupta
	 *
	 */
	public interface MediaPlayerControlExtn extends MediaPlayerControl{
		void stop();
	}
	
	public interface MediaPlayerControlNew extends MediaPlayerControlExtn{
		 boolean onPrevKeyPressed();

	    boolean onNextKeyPressed();
	}
	
	public interface MediaPlayerWithTrickModes extends MediaPlayerControlNew{
		void setSpeed(Boolean forward, int speed);
	}
	
	public interface IPbcDissmissListener
	{
		public void onPbcDismissed();
	}

	public void setBackKeyListener(IPbcDissmissListener argListener) {
		mIPbcDissmissListener = argListener;
	}
	
	public void setMediaPlayerControlNew(MediaPlayerControlNew argListener) {
		mMediaPlayerControlNew = argListener;
		mPrevButton.setVisibility(View.VISIBLE);
		mNextButton.setVisibility(View.VISIBLE);
		if(slideshow){
			mSlideShowTotalDuration = mPlayer.getTotalDuration();
			if((mPlayer.getTotalDuration())<=1){
				mProgress.setEnabled(false);
				mProgress.setFocusable(false);
				setEnabled(false);
			}
			else
			{
				mProgress.setEnabled(true);
				mProgress.setFocusable(true);
				setEnabled(true);
			}
			mProgress.setMax(mPlayer.getTotalDuration());
			LogHelper.d(LOG_TAG," Seek bars Total Duration" + mPlayer.getTotalDuration());
		}else{
			//iseekBar.setMax(mPlayer.getTotalDuration());
			mProgress.setMax(1000);
		}
	}

public void setMediaPlayerWithTrickModes (MediaPlayerWithTrickModes player) {
		mPlayer = player;
		
		if(slideshow){
			mSlideShowTotalDuration = mPlayer.getTotalDuration();
			if((mPlayer.getTotalDuration())<=1){
				mProgress.setEnabled(false);
				mProgress.setFocusable(false);
				setEnabled(false);
			}
			else
			{
				mProgress.setEnabled(true);
				mProgress.setFocusable(true);
				setEnabled(true);
			}
			mProgress.setMax(mPlayer.getTotalDuration());
			LogHelper.d(LOG_TAG," Seek bars Total Duration" + mPlayer.getTotalDuration());
		}else{
			//iseekBar.setMax(mPlayer.getTotalDuration());
			mProgress.setMax(1000);
		}
	}

	private void incrementPlayBack(int duration){
		
		/*if(!slideshow)*/{
			
			//long position = mPlayer.getCurrentDuration() + duration;
			currDurAnim =currDurAnim + duration;
			SeekBar seekbar = (SeekBar) mProgress;
			//seekbar.setProgress(currDurAnim);
			duration = mPlayer.getTotalDuration();
			LogHelper.d(LOG_TAG,"incrementPlayBack step 1 "+ currDurAnim);
			if(!slideshow){
			// This condition has to be checked if we need the seekTo in special media with no totalduration
			if(duration > 0){
				if(currDurAnim < 0 ){
					currDurAnim = 0;
				}
				if(currDurAnim>duration)
				{
					currDurAnim = duration;
				}
				if(duration> 0){
					long pos = 1000L * currDurAnim / duration;
					LogHelper.d(LOG_TAG,"incrementPlayBack step 2 "+ pos);
					seekbar.setProgress((int)pos);
				}
			}
			}else if(slideshow){
			if(duration > 1){
				if(currDurAnim < 1 ){
					if(enableWrapAround){
						currDurAnim = duration;
					}else{
						//When no wrap around -> setting seekbar to first position and giving callback to application.
						LogHelper.d(LOG_TAG,"no wrap around currDurAnim=" + currDurAnim);
						currDurAnim = 1;
						mPlayer.seekTo(-1);
					}
				}
				if(currDurAnim>duration){
					if(enableWrapAround){
						currDurAnim = 1;
					}else{
						//When no wrap around -> setting seekbar to last position and giving callback to application.
						LogHelper.d(LOG_TAG,"no wrap around currDurAnim=" + currDurAnim);
						currDurAnim = duration;
						mPlayer.seekTo(-1);
					}
				}
				//long pos = 1000L * currDurAnim / duration;
				LogHelper.d(LOG_TAG,"incrementPlayBack step 2 "+ currDurAnim);
				seekbar.setProgress(currDurAnim);
			}
			}
			doShow(sDefaultTimeout);
		
		}
	}
	
	/** key handling for the rc control.
	 * 
	 * @param event = KeyEvent
	 * @return boolean true = event is handled false = return Event as unhandled key.
	 */
	
	
	public boolean handleEvent(KeyEvent event){
		boolean retValue = false;
		int keyCode = event.getKeyCode();
		final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
	
		if(keyCode != KeyEvent.KEYCODE_MEDIA_STOP && keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT && keyCode != KeyEvent.KEYCODE_DPAD_LEFT) {
        	showHandler();
        } 
		
		if (uniqueDown) {

			if (mHandler.hasMessages(LRKEYPRESSED)) {

				longkeypressedleft = false;
				longkeypressedright = false;
				mHandler.removeMessages(LRKEYPRESSED);
			}
			// state = 0;
		}

		LogHelper.d(LOG_TAG, "handleevent longkeypressed = " + longkeypressedleft+ " playbackState = " +getPlaybackState()+"  "+ event );
		
		if(!enabledTrickAndJumpMode){
			LogHelper.d(LOG_TAG," TrickPlayAndJumpMode is enabled ?  = "+ enabledTrickAndJumpMode);
			switch (keyCode){
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:case KeyEvent.KEYCODE_MEDIA_REWIND:case KeyEvent.KEYCODE_DPAD_LEFT:case KeyEvent.KEYCODE_DPAD_RIGHT:
				LogHelper.d(LOG_TAG,"Absorb this key as TrickPlayAndJumpMode is disabled "+event);
		        return true;//mNewKeyRegisterListener.unHandledKeyEvents(event);	
			}
		}
		if(!enableNext && keyCode == KeyEvent.KEYCODE_MEDIA_NEXT){
			LogHelper.d(LOG_TAG," Next is enabled ?  = "+ enableNext + " Absorb this key as Next is disabled");
			return true;
		}
		if(!enablePrev && keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS){
			LogHelper.d(LOG_TAG," Prev is enabled ?  = "+ enablePrev + " Absorb this key as Prev is disabled");
			return true;
		}
		if(!enablePausePlay){
			LogHelper.d(LOG_TAG," PausePlay is enabled ?  = "+ enablePausePlay);
			switch (keyCode){
			case KeyEvent.KEYCODE_MEDIA_PAUSE:case KeyEvent.KEYCODE_MEDIA_PLAY:case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				LogHelper.d(LOG_TAG,"Absorb this key as PausePlay is disabled "+event);
		        return true;
		    }
		}
		switch (keyCode) {
			
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			
				if (uniqueDown) {
					doPauseResume();
				}
				if (mPlayer instanceof MediaPlayerWithTrickModes) {
					setPlaybackSpeed(PlaybackSpeed.DEFAULT);
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				if(uniqueDown){
					clearSlowPlayControlBackgrounds();

				// slowPlayEnabled is false for AudioPlayer etc.
					if (mPlayer.isPlaying() && mPlayer instanceof MediaPlayerWithTrickModes && slowPlayEnabled) {
						LogHelper.d(LOG_TAG,"SLOW play control mode is ON inside KeyEvent.KEYCODE_MEDIA_PLAY");
						if(!isSlowPlayControlsVisible()) {
							if(getPlaybackState() == PlaybackState.PLAY ){
								setSlowPlayControlsEnabledInternal(Boolean.TRUE);
							}else{
								setSlowPlayControlsEnabledInternal(Boolean.FALSE);
							}
						}
						else if(isSlowPlayControlsVisible()){
							if(getPlaybackState() == PlaybackState.PLAY ){
								setSlowPlayControlsEnabledInternal(Boolean.FALSE);
							}else{
								setSlowPlayControlsEnabledInternal(Boolean.TRUE);
							}
						}
						else{
							setSlowPlayControlsEnabledInternal(Boolean.FALSE);
						}
										
					} else {
						/*if (getPlaybackState() == PlaybackState.PAUSE && isSlowPlayControlsVisible()){
							clearSlowPlayControlBackgrounds();
							setSlowPlayControlsEnabledInternal(Boolean.TRUE);
						}*/
					}
					if (!mPlayer.isPlaying()){
						mHandler.sendEmptyMessage(SHOW_PROGRESS);
						mPlayer.play();
					}
					setPlaybackSpeed(PlaybackSpeed.DEFAULT);
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				if(uniqueDown){
					if(mHandler.hasMessages(SHOW_PROGRESS)){
						mHandler.removeMessages(SHOW_PROGRESS);
					}
					mPlayer.pause();
				
					clearSlowPlayControlBackgrounds();
					setPlaybackSpeed(PlaybackSpeed.DEFAULT);
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_STOP:
				if(uniqueDown){
					if (mPlayer != null) {
						if (mPlayer instanceof MediaPlayerControlExtn) {
							LogHelper.d(LOG_TAG," STOP callback to application ");
							((MediaPlayerControlExtn) mPlayer).stop();
						}
					}
				if(mPlayer instanceof MediaPlayerWithTrickModes && slowPlayEnabled){
					clearSlowPlayControlBackgrounds();
					setSlowPlayControlsEnabledInternal(Boolean.FALSE);
					setPlaybackSpeed(PlaybackSpeed.DEFAULT);
					}
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
				if(uniqueDown){
					
					if(mPlayer instanceof MediaPlayerWithTrickModes && slowPlayEnabled && getPlaybackState()==PlaybackState.PAUSE){
						LogHelper.d(LOG_TAG,"********************Enabling Slow Play controls");
						setSlowPlayControlsEnabledInternal(Boolean.TRUE);
					}
				
					handleForward();
					
					if(mPlayer instanceof MediaPlayerWithTrickModes){
						LogHelper.d(LOG_TAG, "Setting speed to "+getPlaybackSpeed());
						((MediaPlayerWithTrickModes)mPlayer).setSpeed(Boolean.TRUE,getPlaybackSpeed());
					}else{
						mPlayer.onForwardKeyPressed();
					}
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				if(uniqueDown){
				
					if(mPlayer instanceof MediaPlayerWithTrickModes && slowPlayEnabled && getPlaybackState()==PlaybackState.PAUSE){
						LogHelper.d(LOG_TAG,"********************Enabling Slow Play controls");
						setSlowPlayControlsEnabledInternal(Boolean.TRUE);
					}
				
					handleRewind();
				
					if(mPlayer instanceof MediaPlayerWithTrickModes){
						LogHelper.d(LOG_TAG, "Setting speed to "+getPlaybackSpeed());
						((MediaPlayerWithTrickModes)mPlayer).setSpeed(Boolean.FALSE,getPlaybackSpeed());
					}else{
						mPlayer.onRewindKeyPressed();
					}
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				if(uniqueDown){
					if (mNextButton != null) {
						if (mMediaPlayerControlNew != null) {
							LogHelper.d(LOG_TAG, "handleevent getting into next key");
							mMediaPlayerControlNew.onNextKeyPressed();
						}
					}
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				if(uniqueDown){
					if (mPrevButton != null) {
						if (mMediaPlayerControlNew != null) {
							LogHelper.d(LOG_TAG, "handleevent getting into prev key");
							mMediaPlayerControlNew.onPrevKeyPressed();
						}
					}
				}
				retValue = true;
				break;
			
			case KeyEvent.KEYCODE_DPAD_LEFT:
				resetSlowPlayControlsInternal();
				
				if(!mInfinte)
	           		doShow(timeoutFromApplication);
	           	else
	           		doShow(0);
				
				if (uniqueDown) {
					if(mHandler.hasMessages(SHOW_PROGRESS)){
						mHandler.removeMessages(SHOW_PROGRESS);
					}
					mEnableGetDuration = false;
					if (currDurAnim == 0) {
						currDurAnim = mPlayer.getCurrentDuration();
					}
					int duration = mPlayer.getTotalDuration();
					if (!slideshow) {
						currDurAnim -= 10000;
						if (currDurAnim <= 0) {
							currDurAnim = 0;
						}
					} else {
						currDurAnim--;
						if (currDurAnim < 1) {
							if (enableWrapAround) {
								currDurAnim = duration;
							} else {
								currDurAnim = 1;
								LogHelper.d(LOG_TAG," No Wrap Around currDurAnim="+currDurAnim);
								mPlayer.seekTo(-1);
								retValue = true;
								break;
							}
						}
					}
					LogHelper.d(LOG_TAG, "currDurAnim" + currDurAnim);
					SeekBar seekbar = (SeekBar) mProgress;
					if (duration > 0 && !slideshow) {
						long pos = 1000L * currDurAnim / duration;
						LogHelper.d(LOG_TAG, "incrementPlayBack step 2 " + pos);
						seekbar.setProgress((int) pos);
					} else {
	
						seekbar.setProgress((int) currDurAnim);
					}
					LogHelper.d(LOG_TAG, "currDurAnim" + currDurAnim);
					mPlayer.seekTo(currDurAnim);
				}
				retValue = true;
				break;
	
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				resetSlowPlayControlsInternal();
				
				if(!mInfinte)
					doShow(timeoutFromApplication);
	            else
	            	doShow(0);
				
				if (uniqueDown) {
					if(mHandler.hasMessages(SHOW_PROGRESS)){
						mHandler.removeMessages(SHOW_PROGRESS);
					}
					mEnableGetDuration = false;
					if (currDurAnim == 0) {
						currDurAnim = mPlayer.getCurrentDuration();
					}
					int duration = mPlayer.getTotalDuration();
					if (!slideshow) {
						currDurAnim += 10000;
						if (currDurAnim >= duration) {
							currDurAnim = duration;
						}
					} else { 
						currDurAnim++;
						if (currDurAnim > duration) {
							if (enableWrapAround) {
								currDurAnim = 1;
							} else {
								currDurAnim = duration;
								LogHelper.d(LOG_TAG," No Wrap Around currDurAnim="+currDurAnim);
								mPlayer.seekTo(-1);
								retValue = true;
								break;
							}
						}
					}
					SeekBar seekbar = (SeekBar) mProgress;
					if (duration > 0 && !slideshow) {
						long pos = 1000L * currDurAnim / duration;
						LogHelper.d(LOG_TAG, "incrementPlayBack step 2 " + pos);
						seekbar.setProgress((int) pos);
					} else {
	
						seekbar.setProgress((int) currDurAnim);
					}
					mPlayer.seekTo(currDurAnim);
				}
				retValue = true;
				break;
			
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_UP:
				retValue = false;
				break;
			case KeyEvent.KEYCODE_BACK:
				if(uniqueDown){
					doDismiss();
				}	
				retValue = false;
		break;
		default:
		retValue = false;
		break;
	}

        
        	// This should must have been called after the player was initialised 
        	// this will still show the control even when the player has not started running. 
        LogHelper.d(LOG_TAG,"calling the dispatch keyevent on the root"+retValue);
		if(mPlayer instanceof MediaPlayerWithTrickModes){
			setPlaybackState(keyCode);
		}
        if(retValue){
		return retValue;
	}
	// commented since we need only one call to the Application 
        if(mKeyRegisterListener!=null){
		LogHelper.d(LOG_TAG,"The rest unhandled key"+event);
            	//mKeyRegisterListener.unHandledKeys(event);
	}
	// The new API for call
	 if(mNewKeyRegisterListener!=null){
                LogHelper.d(LOG_TAG,"The rest unhandled key"+event);
                return mNewKeyRegisterListener.unHandledKeyEvents(event);
        }

	state = NOLONGLRKEYPRSD;
        return false;
	}

	private void showHandler(){
        	if(!mInfinte)
        	 		  doShow(sDefaultTimeout);
           		  else
           			  doShow(0);
           		
        	 	  updatePausePlay();
	}
	// FIX FOR issUE ON bLUR IMAGE 22419
	private void updatePausePlay() {
		if (this.root == null || mPauseButton == null)
			return;
		LogHelper.d(LOG_TAG," Update Pause Play icon - mPlayer.isPlaying"+mPlayer.isPlaying());
		if (mPlayer.isPlaying()) {
			mPauseButton.setImageResource(R.drawable.pause_n_ico_40x30_120);
			if(mFileName == null)
				LogHelper.e(LOG_TAG,"Please set the name of the File as well");
			mFileSpeed.setText(mFileName);
			mFileMetaData.setText(mMetaDataPath);
		} else {
			mPauseButton.setImageResource(R.drawable.play_n_ico_40x30_115);
		}
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = Math.round((float)timeMs/1000);

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
 * This sets the progress bar to the max 1000 for audio and video and for images the total number of images 
 * */
		public void setMediaPlayerExtension(MediaPlayerControlExtn player) {
		mPlayer = player;
	
		if(slideshow){
			mSlideShowTotalDuration = mPlayer.getTotalDuration();
			if((mPlayer.getTotalDuration())<=1){
				mProgress.setEnabled(false);
				mProgress.setFocusable(false);
				setEnabled(false);
			}
			else
			{
				mProgress.setEnabled(true);
				mProgress.setFocusable(true);
				setEnabled(true);			
			}
			mProgress.setMax(mPlayer.getTotalDuration());
		}else{
			mProgress.setMax(1000);
		}
		
	}
		/**
		 * This sets the progress bar to the max 1000 for audio and video and for images the total number of images 
		 * */		
	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		if(slideshow){
			mSlideShowTotalDuration = mPlayer.getTotalDuration();
			if((mPlayer.getTotalDuration())<=1){
				mProgress.setEnabled(false);
				mProgress.setFocusable(false);
				setEnabled(false);
			}
			else
			{
				mProgress.setEnabled(true);
				mProgress.setFocusable(true);
				setEnabled(true);
			}
			mProgress.setMax(mPlayer.getTotalDuration());
			
		}else{
			//iseekBar.setMax(mPlayer.getTotalDuration());
			mProgress.setMax(1000);
		}
		
	}

	/**
	 * Show the PlayBackComponent on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		LogHelper.d(LOG_TAG,"Show() is called from application with timeout = "+timeout);
		timeoutFromApplication = timeout;
		doShow(timeout);
	}
	
	private void doShow(int timeout){
		
		LogHelper.d(LOG_TAG, "show timeout = " +timeout+ mHandler.hasMessages(LRKEYPRESSED) + " secondary progress will be shown =" + secondaryProgressEnabled);
		if(this.root !=null){
			this.root.requestFocus();
		}
		if(mHandler.hasMessages(FADE_OUT)){	
			mHandler.removeMessages(FADE_OUT);
		}
		if(mHandler.hasMessages(SECONDARYPROGRESS)){
			 mHandler.removeMessages(SECONDARYPROGRESS);
		 }
		if(secondaryProgressEnabled == true && secoondaryprogress < 100){
			mHandler.sendEmptyMessage(SECONDARYPROGRESS);
		}
		if(!mHandler.hasMessages(SHOW_PROGRESS)){
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
		
		if(timeout==0){
			mInfinte = true;
		}else{
			mInfinte = false;
		}
		LogHelper.d(LOG_TAG,"the media player is "+mPlayer.isPlaying()+state);
		updatePausePlay();
		if (!this.windowbottom.isShowing() && root != null) {
			showLikeQuickAction(0, 0);
		}

		// cause the progress bar to be updated even if mShowing
		// was already true. This happens, for example, if we're
		// paused with the progress bar showing the user hits play.
		//mHandler.sendEmptyMessage(SHOW_PROGRESS);
	
		if(!mHandler.hasMessages(LRKEYPRESSED)){
			Message msg = mHandler.obtainMessage(FADE_OUT);
			if (timeout != 0 && !mInfinte) {
				mHandler.removeMessages(FADE_OUT);
				LogHelper.d(LOG_TAG,"The hide out is initiliased here"+timeout+"  time"+System.currentTimeMillis());
				mHandler.sendMessageDelayed(msg, timeout);
			}
		}
	}

	/**
	 * Show the controller on screen. It will go away automatically after 4
	 * seconds of inactivity.
	 */
	public void show() {
		LogHelper.d(LOG_TAG, "show() is called from application with default timeout ");
		doShow(sDefaultTimeout);
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		LogHelper.d(LOG_TAG," hide () is called from application ");
		doHide();
	}
	
	private void doHide(){
		if (root == null)
			return;

		if (this.windowbottom.isShowing()) {
			try {
				LogHelper.d(LOG_TAG,"the hide request is done");
				//if(mHandler.hasMessages(SHOW_PROGRESS))
				{
					PlayBackControl.this.windowbottom.dismiss();
					windowtop.dismiss();
					mHandler.removeMessages(SHOW_PROGRESS);
				 	viewToFocus = null;	
				}
				mHandler.removeMessages(FADE_OUT);
				mHandler.removeMessages(SECONDARYPROGRESS);
				mHandler.removeMessages(LRKEYPRESSED);

			} catch (IllegalArgumentException ex) {
				LogHelper.w("MediaController", "already removed");
			}
		}
	}
	
	@Override
	public boolean keyEventDispatcherPBC(KeyEvent event){
		return onKeyEvent(event);
	}
	@Override
        public boolean touchEventDispatcherPBC(MotionEvent event){
		doDismiss();
		return true;
	}
	/** To be called by Application use case : after image gets uploaded
	 * 
	 */
	public void setCurrentDuration(){
		LogHelper.d(LOG_TAG,"Show () is called from Application");
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
	}

	
	/**
	 * Method to handle FORWARD key event. Change the play back speed based on current play back state 
	 * and current play back speed. Event to be handled for slow speeds first by delegating to SlowPlayControl.
	 * Disable Slow play bar for fast speeds if its visible.  
	 * */	
	private void handleForward(){
		int currentState = getPlaybackState();
		int currentSpeed = getPlaybackSpeed();
		boolean retVal = false;
		
		LogHelper.d(LOG_TAG," handleForward() -->  currentState="+currentState + " currentSpeed="+currentSpeed);
		
		if(isSlowPlayControlsVisible()){
			retVal = SlowPlayControl.handleSlowSpeedForward(currentState, currentSpeed);
		}
			
		if(!retVal) {
			
			if(isSlowPlayControlsVisible()){
				setSlowPlayControlsEnabledInternal(Boolean.FALSE);
			}
		
			switch(currentState){
				case PlaybackState.PLAY:
				case PlaybackState.PAUSE:
				case PlaybackState.FORWARD:
					LogHelper.d(LOG_TAG,"handleForward :PLAY - currentSpeed="+currentSpeed);
					setPlaybackSpeed(getNextPlaybackSpeed(currentSpeed));
					break;
					
				case PlaybackState.STOP: 
					LogHelper.d(LOG_TAG,"handleForward :STOP - Invalid state: I don't expect this use case");
					break;					
				
				case PlaybackState.REWIND:
					LogHelper.d(LOG_TAG,"handleForward :FORWARD - currentSpeed="+currentSpeed);
					setPlaybackSpeed(PlaybackSpeed.SPEED_LEVEL4);
					break;
					
				default:
					break;		
			}
		}
	}
	
	/**
	 * Method to handle REWIND key event. Change the play back speed based on current play back state 
	 * and current play back speed. Event to be handled for slow speeds first by delegating to SlowPlayControl.
	 * Disable Slow play bar for fast speeds if its visible.  
	 * */
	private void handleRewind(){
		int currentState = getPlaybackState();
		int currentSpeed = getPlaybackSpeed();
		boolean retVal = false;
		
		LogHelper.d(LOG_TAG," handleRewind() -->  currentState="+currentState + " currentSpeed="+currentSpeed);
		
		if(isSlowPlayControlsVisible()){
			retVal = SlowPlayControl.handleSlowSpeedRewind(currentState, currentSpeed);
		}
			
		if(!retVal) {
			
			if(isSlowPlayControlsVisible()){
				setSlowPlayControlsEnabledInternal(Boolean.FALSE);
			}
		
			switch(currentState){
				case PlaybackState.PLAY:
				case PlaybackState.PAUSE:
				case PlaybackState.REWIND:
					LogHelper.d(LOG_TAG,"handleRewind :PLAY - currentSpeed="+currentSpeed);
					setPlaybackSpeed(getNextPlaybackSpeed(currentSpeed));
					break;
					
				case PlaybackState.STOP: 
					LogHelper.d(LOG_TAG,"handleRewind :STOP - Invalid state: I don't expect this use case");
					break;
					
				case PlaybackState.FORWARD:
					LogHelper.d(LOG_TAG,"handleRewind :FORWARD - currentSpeed="+currentSpeed);
					setPlaybackSpeed(PlaybackSpeed.SPEED_LEVEL4);
					break;
					
				default:
					break;		
			}
		}
	}
	
	
	/**
	 * Sets the play back state for Trick play keys only.
	 * 
	 * @param keycode constant value passed for key event
	 */	
	private void setPlaybackState(int keycode){
		LogHelper.d(LOG_TAG,"setPlaybackState - keycode="+keycode);

		switch(keycode){
			case KeyEvent.KEYCODE_MEDIA_PLAY:
			case KeyEvent.KEYCODE_MEDIA_STOP:
				playbackState = PlaybackState.PLAY;
				break;
			
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				playbackState = PlaybackState.PAUSE;
				break;
				
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
				playbackState = PlaybackState.FORWARD;
				if(getPlaybackSpeed() == PlaybackSpeed.DEFAULT)
					playbackState = PlaybackState.PLAY;
				break;
				
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				playbackState = PlaybackState.REWIND;
				if(getPlaybackSpeed() == PlaybackSpeed.DEFAULT)
					playbackState = PlaybackState.PLAY;
				break;
				
			default:
				break;
		}
	}
	
	/**
	 * Gets the current play back state
	 * 
	 * @return int Constant for play back state defined in @link PlaybackState.java
	 */	
	private int getPlaybackState(){
		return playbackState;
	}
	
	/**
	 * Sets the play back speed as per defined constants in @link PlaybackSpeed.java
	 * 
	 * @param keycode constant value passed for key event
	 */	
	private void setPlaybackSpeed(int speed){
		LogHelper.d(LOG_TAG, "setPlaybackSpeed to " + speed);
		playbackSpeed = speed;
	}
	
	/**
	 * Application Sets the current play back speed and playbackState
	 * Used for DMR trick play 
	 * 	 @param speed = playbackspeed playbackState=state 
	 */	
	public void setPlaybackSpeedAndState(int speed,int state){
		LogHelper.d(LOG_TAG," setPlaybackSpeedAndState() Called From Application with speed = "+speed + "state = "+state);
		setPlaybackSpeed(speed);
		
		switch(state){
			case PlaybackState.FORWARD:
				playbackState = KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
				break;
			case PlaybackState.REWIND:
				playbackState = KeyEvent.KEYCODE_MEDIA_REWIND;
				break;
			case PlaybackState.PLAY:
				playbackState = KeyEvent.KEYCODE_MEDIA_PLAY;
				break;
			case PlaybackState.PAUSE:
				playbackState = KeyEvent.KEYCODE_MEDIA_PAUSE;
				break;
			case PlaybackState.STOP:
				playbackState = KeyEvent.KEYCODE_MEDIA_STOP;
				break;	
			default : 
				playbackState = KeyEvent.KEYCODE_MEDIA_PLAY;
				setPlaybackSpeed(PlaybackSpeed.DEFAULT);
				break;
		}
		setPlaybackState(playbackState);
		if(slowPlayEnabled){
			clearSlowPlayControlBackgrounds();
			if(playbackSpeed == PlaybackSpeed.SPEED_LEVEL1 || playbackSpeed == PlaybackSpeed.SPEED_LEVEL2){
				if(playbackState == PlaybackState.FORWARD)
					forwardSlowPlayControl.setBackground(getPlaybackSpeed());
				else if(playbackState == PlaybackState.REWIND)
					rewindSlowPlayControl.setBackground(getPlaybackSpeed());
			}
		}
	}
	
	
	/**
	 * Gets the current play back speed
	 * 
	 * @return int Constant for play back speed defined in @link PlaybackSpeed.java
	 */	
	private int getPlaybackSpeed(){
		LogHelper.d(LOG_TAG, " getPlaybackSpeed = "+playbackSpeed);
		return playbackSpeed;
	}
		
	/**
	 * Gets the next play back speed
	 * 
	 * @return int Constant for play back speed defined in @link PlaybackSpeed.java
	 */	
	private static int getNextPlaybackSpeed(int currentSpeed) {
		int nextSpeed = currentSpeed;
		LogHelper.d(LOG_TAG,"getNextPlaybackSpeed - currentSpeed="+currentSpeed);

		switch (currentSpeed) {
			case PlaybackSpeed.DEFAULT:
			case PlaybackSpeed.SPEED_LEVEL1:
			case PlaybackSpeed.SPEED_LEVEL2:
			case PlaybackSpeed.SPEED_LEVEL3_NORMAL:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL4;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL4:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL5;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL5:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL6;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL6:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL7;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL7:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL8;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL8:
				nextSpeed = PlaybackSpeed.DEFAULT;
				break;
	
			default:
				break;
		}
		return nextSpeed;
	}
	
	/**
	 * Callback method defined in SlowPlayControl.ISlowPlayControlCallback
	 * 
	 * - Called after FORWARD key event is processed by SlowPlayControl
	 * - Updates Play back speed, Slow play control visibility & highlight on slow play controls
	 * 
	 * @param speed Constant for play back speed defined in @link PlaybackSpeed.java
	 */	
	@Override
	public void onForward(int speed) {
		setPlaybackSpeed(speed);
		
		if(speed == PlaybackSpeed.SPEED_LEVEL3_NORMAL){
			if(isSlowPlayControlsVisible()){
				clearSlowPlayControlBackgrounds();
				setSlowPlayControlsEnabledInternal(Boolean.FALSE);
			}			
		} else {
			if(!isSlowPlayControlsVisible()){
				setSlowPlayControlsEnabledInternal(Boolean.TRUE);
			}
			
			clearSlowPlayControlBackgrounds();
			forwardSlowPlayControl.setBackground(getPlaybackSpeed());
			
		}
	}
	
	/**
	 * Callback method defined in SlowPlayControl.ISlowPlayControlCallback
	 * 
	 * - Called after REWIND key event is processed by SlowPlayControl
	 * - Updates Play back speed, Slow play control visibility & highlight on slow play controls
	 * 
	 * @param speed Constant for play back speed defined in @link PlaybackSpeed.java
	 */		
	@Override
	public void onRewind(int speed) {
		setPlaybackSpeed(speed);
		
		if(speed == PlaybackSpeed.SPEED_LEVEL3_NORMAL){
			if(isSlowPlayControlsVisible()){
				clearSlowPlayControlBackgrounds();
				setSlowPlayControlsEnabledInternal(Boolean.FALSE);
			}			
		} else {
			if(!isSlowPlayControlsVisible()){
				setSlowPlayControlsEnabledInternal(Boolean.TRUE);
			}
			
			clearSlowPlayControlBackgrounds();
            rewindSlowPlayControl.setBackground(getPlaybackSpeed());
		}	
	}
	
	/**
	 * Removes highlights from both Forward and Rewind Slow play controls
	 */
	private void clearSlowPlayControlBackgrounds(){
			LogHelper.d(LOG_TAG, "clearSlowPlayControlBackgrounds");
			forwardSlowPlayControl.clearAllBackgrounds();
			rewindSlowPlayControl.clearAllBackgrounds();
	}
	
	/**
	 * Checks if Slow Play Controls are there in visible mode or not
	 * 
	 * @return boolean Returns true if slow play control mode is visible, otherwise false
	 */
	private boolean isSlowPlayControlsVisible(){
		boolean isVisible = false;
		
		if ((rewindSlowPlayControl != null) && (forwardSlowPlayControl != null)) {
			
			if ((rewindSlowPlayControl.getVisibility() == View.VISIBLE)	&& (rewindSlowPlayControl.getVisibility() == View.VISIBLE)) {
				isVisible = true;
			}
		} else {
			LogHelper.d(LOG_TAG,"Invalid state: showSpeedControls: rewindSlowPlayControl &  forwardSlowPlayControl is null");
		}
		return isVisible;
	}	
	
	/**
	 * Controls visibility of slow play controls on play bar - Forward and Rewind
	 *  
	 * @param enable (Value=Boolean.TRUE, visible)( Value=Boolean.FALSE, not visible)
	 */	
	public void setSlowPlayControlsEnabled(Boolean enable) {
		LogHelper.d(LOG_TAG,"setSlowPlayControlsEnabled is called from Application");
		setSlowPlayControlsEnabledInternal(enable);

	}
	private void setSlowPlayControlsEnabledInternal(Boolean enable) {
		LogHelper.d(LOG_TAG,"setSlowPlayControlsEnabledInternal ");
		
		if(enable){  
			mNextButton.setVisibility(View.GONE);
			mPrevButton.setVisibility(View.GONE);

			rewindSlowPlayControl.setVisibility(View.VISIBLE); 
			forwardSlowPlayControl.setVisibility(View.VISIBLE);
		}else{
			mNextButton.setVisibility(View.VISIBLE);
			mPrevButton.setVisibility(View.VISIBLE);
			
			rewindSlowPlayControl.setVisibility(View.GONE);
			forwardSlowPlayControl.setVisibility(View.GONE);
		}
		}
		
	/** This method will be called by application for resetting Playback component before starting Next playback in Playall condition.
	 * 
	 * 
	 */
	public void resetSlowPlayControls(){
		LogHelper.d(LOG_TAG, "resetSlowPlayControls() is Called from Application");
		resetSlowPlayControlsInternal();
	}
	private void resetSlowPlayControlsInternal(){
		LogHelper.d(LOG_TAG, "resetSlowPlayControlsInternal is Called");
		clearSlowPlayControlBackgrounds();
		setPlaybackSpeed(PlaybackSpeed.DEFAULT);
		setPlaybackState(KeyEvent.KEYCODE_MEDIA_PLAY);
	}
	
	/** This sets SlowPlayTrickMode From Application
	 *  use case : slowPlayEnabled is false for AudioPlayer
	 * @param enabled (Value=true, available)( Value=false, not available)
	 */
	public void setSlowPlayTrickMode(boolean enabled){
		slowPlayEnabled = enabled;
	}
	/** API to update play pause state of playback from Application
	 * 
	 */
	public void updatePlaybackState(){
		updatePausePlay();
	}
	
	/**API to be called to enable or disable trickplay and seek actions.
	 * should be called with setEnabledProgress() 
	 * 
	 * @param enabled true = enabled and false = disabled
	 */
	public void setEnabledTrickAndJumpMode(boolean enabled){
		enabledTrickAndJumpMode = enabled;
	}
	
	/**
	 * API to set wrap around of images during slideshow
	 * @param enabled = true when wrapAround is allowed.
	 */
	public void setEnabledWrapAround(boolean enabled) {
		enableWrapAround = enabled;
	}
	
}
