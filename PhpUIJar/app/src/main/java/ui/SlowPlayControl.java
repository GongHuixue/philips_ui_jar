package ui;

import fany.phpuijar.R;
import ui.Slider.ISliderOnTouchListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import ui.utils.LogHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Linear Layout which has 3 TextViews to indicate slow speed trick modes.
 * TextViews are not focusable.
 * for rewind keys one instance of this class will be affected and for Forward keys other instance.
 * This is decided by xml attribute named 'inverted' in control.xml
 * 
 */
public class SlowPlayControl extends LinearLayout {

	private TextView[] textViews = null;
	
	boolean inverted = false;
	String[] labels = { "1/4", "1/2"};
	Drawable speedControlBackground = null;
	private static ISlowPlayControlCallback mControlCallback = null;
		
	public SlowPlayControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		textViews = new TextView[2];

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View playControl = (View) inflater.inflate(
				R.layout.slow_play_control_1, this);

		if (attrs != null) {
			TypedArray attributes = context.obtainStyledAttributes(attrs,
					R.styleable.slow_play_control);
			inverted = attributes.getBoolean(0, false);

		}

		speedControlBackground = context.getResources().getDrawable(
				R.drawable.playcontrols_dlna_highlighted_glow);

		textViews[0] = (TextView) playControl.findViewById(R.id.left);
		textViews[1] = (TextView) playControl.findViewById(R.id.right);
		
		if (inverted) {
			textViews[0].setText(labels[1]);
			textViews[1].setText(labels[0]);
		} else {
			textViews[0].setText(labels[0]);
			textViews[1].setText(labels[1]);
		}
	}
	
	public SlowPlayControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlowPlayControl(Context context) {
		this(context, null, 0);
	}
	
	/**
	 * Sets background for textview based on speed of playback
	 * @param playbackSpeed
	 */
	
	protected void setBackground(int playbackSpeed){
		int index = evaluateIndex(playbackSpeed);
		LogHelper.d("PlayBackControl"," SlowPlayControl => setBackground() - textViews[index] = t["+index +"]" );
		
		for(TextView t: textViews){
			t.setBackground(null);
		}		
		this.textViews[index].setBackground(speedControlBackground);
	}
	
	protected void clearAllBackgrounds(){
		for(TextView t: textViews){
			t.setBackground(null);
		}
	}
	
	/**
	 * Based on speed get index for TextView array to set Background to show it highlighted
	 * @param playbackSpeed
	 * @return int
	 */
	
	private int evaluateIndex(int playbackSpeed){
		int index = 0;
		
		if(inverted){
			switch(playbackSpeed){
				case PlaybackSpeed.SPEED_LEVEL1:
					index = 1;
					break;
				case PlaybackSpeed.SPEED_LEVEL2:
					index = 0;
					break;

				default:
					break;
			}			
		} else {
			switch(playbackSpeed){
				case PlaybackSpeed.SPEED_LEVEL1:
					index = 0;
					break;
				case PlaybackSpeed.SPEED_LEVEL2:
					index = 1;
					break;
					
				default:
					break;
			}
		}
		return index;
	}
	
	private static int getNextPlaybackSpeed(int currentSpeed) {
		int nextSpeed = currentSpeed;
		
		LogHelper.d("PlayBackControl","SlowPlayControl => getNextPlaybackSpeed - currentSpeed="+currentSpeed);
	
		switch (currentSpeed) {
			case PlaybackSpeed.DEFAULT:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL1;
				break;
				
			case PlaybackSpeed.SPEED_LEVEL1:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL2;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL2:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL3_NORMAL;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL3_NORMAL:
				nextSpeed = PlaybackSpeed.SPEED_LEVEL4;
				break;
	
			case PlaybackSpeed.SPEED_LEVEL4:
			case PlaybackSpeed.SPEED_LEVEL5:
			case PlaybackSpeed.SPEED_LEVEL6:
			case PlaybackSpeed.SPEED_LEVEL7:
			case PlaybackSpeed.SPEED_LEVEL8:
				LogHelper.d("PlayBackControl","SlowPlayControl =>Invalid state: Getting fast play previous speeds when play back mode was Slow");
				break;
	
			default:
				break;
		}
		return nextSpeed;
	}
	
	/**
	 * Handle forward event For SlowPlayControl based on playback state and playback speed
	 * @param currentState
	 * @param currentSpeed
	 * @return boolean - whether key is handled or not
	 */
	
	protected static boolean handleSlowSpeedForward(int currentState, int currentSpeed){
		boolean retVal = false;
		int nextPlaybackSpeed = currentSpeed;
		 LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedForward : - currentSpeed="+currentSpeed + " currentState="+currentState);

		switch(currentState) {
			case PlaybackState.PLAY:
			case PlaybackState.PAUSE:
			case PlaybackState.FORWARD:
				 LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedForward :PLAY/PAUSE/FORWARD - currentSpeed="+currentSpeed);
				 nextPlaybackSpeed = getNextPlaybackSpeed(currentSpeed);
				 retVal = true;
				 break;
				
			case PlaybackState.STOP: 
				LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedForward :STOP - Invalid state: I don't expect this use case");
				break;
				
			case PlaybackState.REWIND:			
				LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedForward :REWIND - currentSpeed="+currentSpeed);
				nextPlaybackSpeed = PlaybackSpeed.SPEED_LEVEL1;
				retVal = true;
				break;
				
			default:
				break;
		}
		
		if(retVal){
			mControlCallback.onForward(nextPlaybackSpeed);
		}
		return retVal;
	}
	
	/**
	 * Handle rewind event For SlowPlayControl based on playback state and playback speed
	 * @param currentState
	 * @param currentSpeed
	 * @return boolean - whether key is handled or not
	 */
	
	protected static boolean handleSlowSpeedRewind(int currentState, int currentSpeed){
		boolean retVal = false;
		int nextPlaybackSpeed = currentSpeed;
		
		 LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedRewind : - currentSpeed="+currentSpeed + " currentState="+currentState);

		switch(currentState) {
			case PlaybackState.PLAY:
			case PlaybackState.PAUSE:
			case PlaybackState.REWIND:
				 LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedRewind :PLAY/PAUSE/REWIND - currentSpeed="+currentSpeed);
				 nextPlaybackSpeed = getNextPlaybackSpeed(currentSpeed);
				 retVal = true;
				 break;
				
			case PlaybackState.STOP: 
				LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedRewind :STOP - Invalid state: I don't expect this use case");
				break;
				
			case PlaybackState.FORWARD:
				LogHelper.d("PlayBackControl","SlowPlayControl =>handleSlowSpeedRewind :FORWARD - currentSpeed="+currentSpeed);
				nextPlaybackSpeed = PlaybackSpeed.SPEED_LEVEL1;
				retVal = true;
				break;
				
			default:
				break;
		}
		
		if(retVal){
			mControlCallback.onRewind(nextPlaybackSpeed);
		}
		return retVal;
	}
	
	protected static void registerSlowPlayControlCallback(ISlowPlayControlCallback callback) {
		mControlCallback = callback;
	}
	
	protected interface ISlowPlayControlCallback {
		void onForward(int speed);

		void onRewind(int speed);
	}	
}