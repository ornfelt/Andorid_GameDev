class PongView extends SurfaceView implements Runnable {
 
    // This is our thread
    Thread mGameThread = null;
 
    // We need a SurfaceHolder object
    // We will see it in action in the draw method soon.
    SurfaceHolder mOurHolder;
 
    // A boolean which we will set and unset
    // when the game is running- or not
    // It is volatile because it is accessed from inside and outside the thread
    volatile boolean mPlaying;
 
    // Game is mPaused at the start
    boolean mPaused = true;
 
    // A Canvas and a Paint object
    Canvas mCanvas;
    Paint mPaint;
 
    // This variable tracks the game frame rate
    long mFPS;
 
    // The size of the screen in pixels
    int mScreenX;
    int mScreenY;
 
    // The players mBat
    Bat mBat;
 
    // A mBall
    Ball mBall;
 
    // For sound FX
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
 
    // The mScore
    int mScore = 0;
 
    // Lives
    int mLives = 3;
	
	
	/*
    When the we call new() on pongView
    This custom constructor runs
	*/
	public PongView(Context context, int x, int y) {
		 
		/*
			The next line of code asks the
			SurfaceView class to set up our object.
		*/
		super(context);
	 
		// Set the screen width and height
		mScreenX = x;
		mScreenY = y;
	 
		// Initialize mOurHolder and mPaint objects
		mOurHolder = getHolder();
		mPaint = new Paint();
	 
		// A new mBat
		mBat = new Bat(mScreenX, mScreenY);
	 
		// Create a mBall
		mBall = new Ball(mScreenX, mScreenY);
	 
		/* 
			Instantiate our sound pool 
			dependent upon which version 
			of Android is present
		*/
	 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			AudioAttributes audioAttributes = new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_MEDIA)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.build();
	 
			sp = new SoundPool.Builder()
					.setMaxStreams(5)
					.setAudioAttributes(audioAttributes)
					.build();
		 
		} else {
			sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}
	 
	 
		try{
			// Create objects of the 2 required classes
			AssetManager assetManager = context.getAssets();
			AssetFileDescriptor descriptor;
	 
			// Load our fx in memory ready for use
			descriptor = assetManager.openFd("beep1.ogg");
			beep1ID = sp.load(descriptor, 0);
	 
			descriptor = assetManager.openFd("beep2.ogg");
			beep2ID = sp.load(descriptor, 0);
	 
			descriptor = assetManager.openFd("beep3.ogg");
			beep3ID = sp.load(descriptor, 0);
	 
			descriptor = assetManager.openFd("loseLife.ogg");
			loseLifeID = sp.load(descriptor, 0);
	 
			descriptor = assetManager.openFd("explode.ogg");
			explodeID = sp.load(descriptor, 0);
	 
		}catch(IOException e){
			// Print an error message to the console
			Log.e("error", "failed to load sound files");
		}
	 
		setupAndRestart();
 
	}
	
	public void clearObstacleY(float y){
		mRect.bottom = y;
		mRect.top = y - mBallHeight;
	}
	 
	public void clearObstacleX(float x){
		mRect.left = x;
		mRect.right = x + mBallWidth;
	}
	 
	public void reset(int x, int y){
		mRect.left = x / 2;
		mRect.top = y - 20;
		mRect.right = x / 2 + mBallWidth;
		mRect.bottom = y - 20 - mBallHeight;
	}
	
	public void setupAndRestart(){
 
		// Put the mBall back to the start
		mBall.reset(mScreenX, mScreenY);
	 
		// if game over reset scores and mLives
		if(mLives == 0) {
			mScore = 0;
			mLives = 3;
		}
	}
	
	@Override
	public void run() {
		while (mPlaying) {
	 
			// Capture the current time in milliseconds in startFrameTime
			long startFrameTime = System.currentTimeMillis();
	 
			// Update the frame
			// Update the frame
			if(!mPaused){
				update();
			}
	 
			// Draw the frame
			draw();
	 
			/*
				Calculate the FPS this frame
				We can then use the result to
				time animations in the update methods.
			*/
			long timeThisFrame = System.currentTimeMillis() - startFrameTime;
			if (timeThisFrame >= 1) {
				mFPS = 1000 / timeThisFrame;
			}
	 
		}
	}
	
	// Everything that needs to be updated goes in here
	// Movement, collision detection etc.
	public void update() {
	 
		// Move the mBat if required
		mBat.update(mFPS);
	 
		mBall.update(mFPS);
		
		// Check for mBall colliding with mBat
		if(RectF.intersects(mBat.getRect(), mBall.getRect())) {
			mBall.setRandomXVelocity();
			mBall.reverseYVelocity();
			mBall.clearObstacleY(mBat.getRect().top - 2);
		 
			mScore++;
			mBall.increaseVelocity();
		 
			sp.play(beep1ID, 1, 1, 0, 0, 1);
		}
		
		// Bounce the mBall back when it hits the bottom of screen
	if(mBall.getRect().bottom > mScreenY){
		mBall.reverseYVelocity();
		mBall.clearObstacleY(mScreenY - 2);
	 
		// Lose a life
		mLives--;
		sp.play(loseLifeID, 1, 1, 0, 0, 1);
	 
		if(mLives == 0){
			mPaused = true;
			setupAndRestart();
		}
	}
	
	// Bounce the mBall back when it hits the top of screen
	if(mBall.getRect().top < 0){
		mBall.reverseYVelocity();
		mBall.clearObstacleY(12);
	 
		sp.play(beep2ID, 1, 1, 0, 0, 1);
	}
	
	// If the mBall hits left wall bounce
	if(mBall.getRect().left < 0){
		mBall.reverseXVelocity();
		mBall.clearObstacleX(2);
	 
		sp.play(beep3ID, 1, 1, 0, 0, 1);
	}
	
	// If the mBall hits right wall bounce
    if(mBall.getRect().right > mScreenX){
        mBall.reverseXVelocity();
        mBall.clearObstacleX(mScreenX - 22);
 
        sp.play(beep3ID, 1, 1, 0, 0, 1);
    }
}

	 // Draw the newly updated scene
	public void draw() {
	 
		// Make sure our drawing surface is valid or we crash
		if (mOurHolder.getSurface().isValid()) {
	 
			// Draw everything here
	 
			// Lock the mCanvas ready to draw
			mCanvas = mOurHolder.lockCanvas();
	 
			// Clear the screen with my favorite color
			mCanvas.drawColor(Color.argb(255, 120, 197, 87));
	 
			// Choose the brush color for drawing
			mPaint.setColor(Color.argb(255, 255, 255, 255));
	 
			// Draw the mBat
			mCanvas.drawRect(mBat.getRect(), mPaint);
	 
			// Draw the mBall
			mCanvas.drawRect(mBall.getRect(), mPaint);
			 
	 
			// Change the drawing color to white
			mPaint.setColor(Color.argb(255, 255, 255, 255));
	 
			// Draw the mScore
			mPaint.setTextSize(40);
			mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);
	 
			// Draw everything to the screen
			mOurHolder.unlockCanvasAndPost(mCanvas);
		}
	}
	
	// If the Activity is paused/stopped
	// shutdown our thread.
	public void pause() {
		mPlaying = false;
		try {
			mGameThread.join();
		} catch (InterruptedException e) {
			Log.e("Error:", "joining thread");
		}
	 
	}
	 
	// If the Activity starts/restarts
	// start our thread.
	public void resume() {
		mPlaying = true;
		mGameThread = new Thread(this);
		mGameThread.start();
	}
	
	
	// The SurfaceView class implements onTouchListener
	// So we can override this method and detect screen touches.
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
	 
		switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
	 
			// Player has touched the screen
			case MotionEvent.ACTION_DOWN:
	 
				mPaused = false;
	 
				// Is the touch on the right or left?
				if(motionEvent.getX() > mScreenX / 2){
					mBat.setMovementState(mBat.RIGHT);
				}
				else{
					mBat.setMovementState(mBat.LEFT);
				}
	 
				break;
	 
			// Player has removed finger from screen
			case MotionEvent.ACTION_UP:
	 
				mBat.setMovementState(mBat.STOPPED);
				break;
		}
		return true;
	}

}


