package com.example.wooisso.sunnyapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TimePicker;


import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {



    @Override // TimePickerFragment의 콜백함수
    public void onTimeSet(TimePicker timePicker, int hourOfday, int minute) {
        Button btn1 = (Button)getActivity().findViewById(R.id.main_time);


        SharedPreferences st = getActivity().getSharedPreferences("timeinfo", 0); // 상위 액티비티의 SharedPref "timeinfo"를 가져옴.
        SharedPreferences.Editor editor = st.edit();
        editor.remove("time");
        editor.commit(); // SharedPref "time" 키로 저장된 값 업데이트를 위해 삭제

        String newstr = String.format("%02d:%02d", hourOfday, minute); // Time 00:00 형태로 SharedPref "time"키로 저장
        editor.putString("time", newstr);
        editor.commit();
        editor.putLong("time_mil", hourOfday * 60 * 60 * 1000 + minute * 60 * 1000); // TIme * 60 + Min 의 msec값을 SharedPref "time_mil"키로 저장
        editor.commit();

        btn1.setText(newstr);

        //SharedPreference에 timepicker 시간정보 저장
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar mCalendar = Calendar.getInstance();



        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);

        TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth,this, hour, min, false);

        mTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mTimePickerDialog.setCanceledOnTouchOutside(false);

        return mTimePickerDialog;
    }



}
