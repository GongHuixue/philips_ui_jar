package ui.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Toast;

public class FPSUtil {

	private static FPSUtil FPSUTIL = null;
	private static final String TAG = FPSUtil.class.getSimpleName();
	private int mFPS;

	private int mFrameCount;
	private long mTimeStamp;
	private Activity mContext;
	private String mAnimationName;
	
	private OnPreDrawListener mPreListner = new OnPreDrawListener() {

		@Override
		public boolean onPreDraw() {
			mFrameCount++;
			return true;
		}
	};

	private FPSUtil(Activity context,String animationName) {

		mContext = context;
		mAnimationName= animationName;
		mFPS = 0;
	}

	static public FPSUtil getFpsUtil(Activity context, String animationName) {
	//	if (FPSUTIL == null) {
		//	FPSUTIL = new FPSUtil(context,animationName);
	//	}
		
		
		return new FPSUtil(context,animationName);
	}

	public void setView(View v) {

		setView(v, null);
	}

	
	
	public void setView(View v, Animator anim) {
		
		v.getViewTreeObserver().removeOnPreDrawListener(mPreListner);
		v.getViewTreeObserver().addOnPreDrawListener(mPreListner);

		if (anim != null) {
			anim.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					startMeasuring();
				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					endMeasuring();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					endMeasuring();
				}
			});
		}
		
	}

	public void startMeasuring() {
		mFrameCount = 0;
		mTimeStamp = SystemClock.elapsedRealtime();
	}

	public int endMeasuring() {

		String className=mContext.getComponentName().getClassName();
		long timestamp = SystemClock.elapsedRealtime();
		long elapsedTime =  (timestamp - mTimeStamp);
		if (elapsedTime > 0 && mTimeStamp > 0) {

			Log.i(TAG , "For Activity "+className+
					" ( "+ mAnimationName+ " ) :" +" Time " + elapsedTime + " Frames(FPS): "
					+ mFrameCount + " Frame rate: " + (mFrameCount * 1000)
					/ (float) elapsedTime);
			
			mFPS = (int) ((mFrameCount * 1000)	/ (float) elapsedTime); 

			mTimeStamp = 0;
			mFrameCount = 0;
		}

		return mFPS;
	}

	public int getMeasuredFps() {
		return mFPS;
	}
}
