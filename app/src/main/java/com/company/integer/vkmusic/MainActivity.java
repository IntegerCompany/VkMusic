package com.company.integer.vkmusic;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;


public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabPrevious;
    FloatingActionButton fabPlayPause;
    FloatingActionButton fabNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabPrevious = (FloatingActionButton) findViewById(R.id.fab_previous);
        fabPlayPause = (FloatingActionButton) findViewById(R.id.fab_play_pause);
        fabNext = (FloatingActionButton) findViewById(R.id.fab_next);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void setTranslations(float k) {
        Log.d("sliding :", "" + k);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        fabPlayPause.setTranslationX(-(width / 2 - dpToPx(79)) * k);
        fabPlayPause.setTranslationY(-(height - dpToPx(454)) * k);
        fabPrevious.setTranslationX(-(width / 2 - dpToPx(79) + dpToPx(24)) * k);
        fabPrevious.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
        fabNext.setTranslationX(-(width / 2 - dpToPx(79) - dpToPx(24)) * k);
        fabNext.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }
}