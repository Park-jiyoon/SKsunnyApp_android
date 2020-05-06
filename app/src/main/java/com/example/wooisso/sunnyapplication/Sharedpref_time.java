package com.example.wooisso.sunnyapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Sharedpref_time extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);
    }

    // 값 불러오기
    private void loadPreferences() {
        SharedPreferences time_info = getSharedPreferences("timeinfo", MODE_PRIVATE);
        time_info.getString("time","");
    }

    // 값 저장하기
    private void savePreferences(String string) {
        SharedPreferences time_info = getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor editor = time_info.edit();
        editor.putString("time", string);
        editor.commit();
    }

    // 값 삭제하기
    private void clearPreferences() {
        SharedPreferences time_info = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = time_info.edit();
        editor.remove("time");
        editor.commit();
    }

    // 값 모두 삭제하기
    private void removeAllPreferences() {
        SharedPreferences time_info = getSharedPreferences("time_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = time_info.edit();
        editor.clear();
        editor.commit();
    }

}
