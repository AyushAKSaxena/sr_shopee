package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.my.shopee.myshopee.R;

public class splash_activity extends AppCompatActivity {

    ImageView rocket;
    int SPLASH_DISPLAY_LENGTH = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        rocket = findViewById(R.id.splash_screen_rocket);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Animation rocketAnim = AnimationUtils.loadAnimation(splash_activity.this, R.anim.mid_to_top_animation);
                rocketAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startMainActivity();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rocket.startAnimation(rocketAnim);

            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(splash_activity.this,adminPanelActivity.class);
        splash_activity.this.startActivity(mainIntent);
        splash_activity.this.finish();
    }

}
