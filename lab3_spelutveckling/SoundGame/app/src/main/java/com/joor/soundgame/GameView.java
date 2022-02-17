package com.joor.soundgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class GameView extends SurfaceView implements Runnable {

    // This is our thread
    Thread mGameThread = null;

    //circle positions and color
    float r1;
    float x1;
    float y1;
    int color1;
    float r2;
    float x2;
    float y2;
    int color2;

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

    // For sound FX
    SoundPool sp;
    int sound1Id = -1;
    int sound2Id = -1;

    // The mScore
    int mScore = 0;

    // Lives
    int mLives = 3;


    /*
    When the we call new() on pongView
    This custom constructor runs
    */
    public GameView(Context context, int x, int y) {

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
            descriptor = assetManager.openFd("beep1.wav");
            sound1Id = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.wav");
            sound2Id = sp.load(descriptor, 0);


        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        //init circle values
        color1 = Color.rgb(25, 100, 100);
        r1 = 100;
        x1 = 300;
        y1 = 150;

        color2 = Color.rgb(100, 10, 10);
        r2 = 100;
        x2 = 300;
        y2 = 550;
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
    public void update() {

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

            //draw 2 circles
            mPaint.setColor(color1);
            mCanvas.drawCircle(x1, y1, r1, mPaint);
            mPaint.setColor(color2);
            mCanvas.drawCircle(x2, y2, r2, mPaint);

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

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction()) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                if(x > x1-r1 && x < x1 + r1 && y > y1-r1 && y < y1 + r1){
                    sp.play(sound1Id, 1, 1, 0, 0, 1);
                }
                if(x > x2-r2 && x < x2 + r2 && y > y2-r2 && y < y2 + r2){
                    sp.play(sound2Id, 1, 1, 0, 0, 1);
                }
        }
        return true;
    }

}


