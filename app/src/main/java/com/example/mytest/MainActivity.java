package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressCircle = findViewById(R.id.progress_circular);

        handler.post(periodicUpdate);


    }

    private float rep = 0;
    ProgressCircle progressCircle ;
    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            if ( rep <= 1f) { // record on every tenth seconds (0s, 10s, 20s, 30s...)
                rep += 0.001f ;
                progressCircle.setProgress(rep);

            }
            handler.postDelayed(periodicUpdate, 10);
        }
    };
}

