package com.joor.lab1_spelutveckling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

/*
* @author Jonas Örnfelt
*/

public class MainActivity extends AppCompatActivity {

    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    //gridWidth 20 skapar ett någorlunda stort rutnät
    int gridWidth = 20;
    int gridHeight;

    // Här instansieras objekt av klasser vi använder för att rita upp vyn
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        numberHorizontalPixels = displayMetrics.widthPixels;
        numberVerticalPixels = displayMetrics.heightPixels;

        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        Log.d("RESOLUTION", "Vertikala pixlar: " + numberVerticalPixels + ", Horisontella pixlar: "
                + numberHorizontalPixels);
        Log.d("RESOLUTION", "blockSize: " + blockSize + ", gridHeight: " + gridHeight + ", gridWidth: " + gridWidth);

        // Initiera alla objekt
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels,
                numberVerticalPixels,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        // Ändrar vyn till gameView
        setContentView(gameView);

        gameView.setImageBitmap(blankBitmap);
        // Gör vyn helvit
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        // Ändra målarfärg till svart
        paint.setColor(Color.argb(255, 0, 0, 0));

        // Måla upp de vertikala linjerna av grid
        for(int i = 0; i < gridWidth; i++){
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, numberVerticalPixels,
                    paint);
        }
        // Måla upp de horisontella linjerna av grid
        for(int i = 0; i < gridHeight; i++){
            canvas.drawLine(0, blockSize * i,
                    numberHorizontalPixels, blockSize * i,
                    paint);
        }

/*
*       // För att printa ut info om display i spelet
        paint.setTextSize(blockSize/2);
        canvas.drawText("numberHorizontalPixels = "
                        + numberHorizontalPixels,
                40, blockSize * 1, paint);
        canvas.drawText("numberVerticalPixels = "
                        + numberVerticalPixels,
                40, blockSize * 2, paint);
        canvas.drawText("blockSize = " + blockSize,
                40, blockSize * 3, paint);
        canvas.drawText("gridWidth = " + gridWidth,
                40, blockSize * 4, paint);
        canvas.drawText("gridHeight = " + gridHeight,
                40, blockSize * 5, paint);


 */


    }
}
