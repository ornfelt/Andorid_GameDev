package com.joor.soundgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.media.Image;
import android.net.Uri;

import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class has the same functionality as the GameView, but has a different solution
 * author Jonas Ornfelt
 */

public class playActivity extends AppCompatActivity {

    // For sound FX
    SoundPool sp;
    int sound1ID = -1;
    int sound2ID = -1;

    Context context;
    ImageView imageFirst;
    ImageView imageSecond;
    LoadImage loadImage;
    private String FIRSTURLIMAGE = "https://i.imgur.com/DvpvklR.png";
    private String SECONDURLIMAGE = "https://i.imgur.com/zrdoLjG.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //initialize objects needed
        context = getApplicationContext();
        imageFirst = findViewById(R.id.imageViewFirst);
        imageSecond = findViewById(R.id.imageViewSecond);
        loadImage = new LoadImage(imageFirst);
        loadImage.execute(FIRSTURLIMAGE);
        loadImage = new LoadImage(imageSecond);
        loadImage.execute(SECONDURLIMAGE);

        /*
			Instantiate the sound pool (depends upon Android version)
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
            //instantiate object of required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // load sounds in memory
            descriptor = assetManager.openFd("beep1.wav");
            sound1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.wav");
            sound2ID = sp.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
    }

    //class for loading image
    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public LoadImage(ImageView ivResult){
            this.imageView = ivResult;
        }
        @Override
        protected Bitmap doInBackground(String... strings){
            String urlLink = strings[0];
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                inputStream = new java.net.URL(urlLink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Something went wrong...");
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            imageView.setImageBitmap(bitmap);
        }
    }

    //method that plays first sound
    public void onClickFirstSound(View view) {
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound1);
        //mp.start();
        sp.play(sound1ID, 1, 1, 0, 0, 1);

    }

    //method that plays second sound
    public void onClickSecondSound(View view) {
        sp.play(sound2ID, 1, 1, 0, 0, 1);
    }
}
