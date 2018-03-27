package ui.media;

public abstract class AbsMediaPlayerControl {

	// Ref PR's issue AN-1787
	public abstract void seekTo(int pos);

	public abstract void play();

	public abstract void pause();

	public abstract int getTotalDuration();

	public abstract int getCurrentDuration();

	public abstract boolean isPlaying();

	/**
	 * @deprecated There is a problem with %, 1.99 = 1 hence we will miss 54s(1% =
	 *             54s) time duration change,to avoid this use bufferduration in
	 *             millisecond API : getBufferDurationMillis()
	 * @return buffer value
	 */
	public int getBufferPercentage() {
		return 0;
	}

	/**
	 * To get the buffer duration in milliseconds.
	 * 
	 * @return duration in ms
	 */
	public int getBufferDurationMillis() {
		return 0;
	}

	/**
	 * @deprecated use setSpeed
	 * 
	 * @return
	 */
	public  boolean onForwardKeyPressed(){
		return false;
		
	}

	/**
	 * @deprecated use setSpeed
	 * 
	 * @return
	 */
	public  boolean onRewindKeyPressed(){
		return false;
		
	}

	public abstract  void setSpeed(Boolean forward, int speed);

	public void stop() {

	}

	public boolean onPrevKeyPressed() {
		return false;
	}

	public boolean onNextKeyPressed() {
		return false;
	}

}
