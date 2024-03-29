

public class Ball {
	private RectF mRect; 
	private float mXVelocity;
	private float mYVelocity;
	private float mBallWidth; 
	private float mBallHeight;
	
	public Ball(int screenX, int screenY){
		
		//Make the mBall size relative to the screen resolution
		mBallWidth = screenX / 100;
		mBallHeight = mBallWidth;

		/* 
		 * Start the ball travelling straight up 
		 * at a quarter of the screen height per second
		*/

		mYVelocity = screenY / 4;
		mXVelocity = mYVelocity;

		//initialize the rect that represents the mBall
		mRect = new RectF();

	}

	public RectF getRect(){
		return mRect;
	}
	
	// Change the position each frame
	public void update(long fps){
		mRect.left = mRect.left + (mXVelocity / fps);
		mRect.top = mRect.top + (mYVelocity / fps);
		mRect.right = mRect.left + mBallWidth;
		mRect.bottom = mRect.top - mBallHeight;
}

	// Reverse the vertical heading
	public void reverseYVelocity(){
		mYVelocity = -mYVelocity;
}
 
	// Reverse the horizontal heading
	public void reverseXVelocity(){
		mXVelocity = -mXVelocity;
}
 
public void setRandomXVelocity(){
     
		// Generate a random number either 0 or 1
		Random generator = new Random();
		int answer = generator.nextInt(2);
 
		if(answer == 0){
			reverseXVelocity();
    }
}
 
	// Speed up by 10%
	// A score of over 20 is quite difficult
	// Reduce or increase 10 to make this easier or harder
	public void increaseVelocity(){
		mXVelocity = mXVelocity + mXVelocity / 10;
		mYVelocity = mYVelocity + mYVelocity / 10;
}
}
