package com.joor.soundgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Display;
import android.view.View;
import android.widget.Button;

/**
 *
 * @author Jonas Ornfelt
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();

    }

    private void setListeners() {
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_info).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Button btn = (Button)v;

        switch (btn.getId()) {
            case R.id.btn_play : {
                //Navigate to playActivity
                //Intent intent = new Intent(this, playActivity.class);
                //startActivity(intent);

                // Get a Display object to access screen details
                Display display = getWindowManager().getDefaultDisplay();

                // Load the resolution into a Point object
                Point size = new Point();
                display.getSize(size);
                GameView gw = new GameView(this, size.x, size.y);
                setContentView(gw);
                gw.resume();
                break;
            } case R.id.btn_info : {
                //Navigate to infoActivity
                Intent intent = new Intent(this, infoActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}

