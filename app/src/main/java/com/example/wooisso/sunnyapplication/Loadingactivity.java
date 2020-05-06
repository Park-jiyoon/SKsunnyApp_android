package com.example.wooisso.sunnyapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Loadingactivity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try{
            Thread.sleep(3000);
            Intent mainIntent = new Intent(this,Startactivity.class);
            startActivity(mainIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
