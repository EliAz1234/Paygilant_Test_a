package com.example.test_with_maven;

import android.Manifest;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.paygilant_test_a.SDKScan;


public class MainActivity extends AppCompatActivity {
    Button button;
    SDKScan sdkScan;
    public static final int READ_PHONE_STATE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.activity_main);
        button = findViewById(R.id.button);
        sdkScan = new SDKScan(this,view,this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,   Manifest.permission.CAMERA}
                    , READ_PHONE_STATE_PERMISSION);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkScan.startNewScreenListener();

            }
        });
    }

}