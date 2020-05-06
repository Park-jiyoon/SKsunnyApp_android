package com.example.wooisso.sunnyapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Startactivity extends AppCompatActivity {
    private final static String TAG = Startactivity.class.getSimpleName();

    public final static int APP_LIST_RESULT_ACITIVTYKEY = 100;
    public final static String USE_APP_LIST = "app_list";
    public final static String USE_APP_MODE = "app_allowed_mode";
    Button startButton = null;
    TextView appSelectTextView = null;

    Context activityContext = null;
    ArrayList<String> appList = new ArrayList<String>();
    boolean appMode = false;
     int swFacebook;
    int swKakao;
    int swYoutube;
    int swInstagram;

    private iMyCounterService binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            binder = iMyCounterService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private class GetCountThread implements Runnable {

        private Handler handler = new Handler();

        @Override
        public void run() {

            while(running) {
                if (binder==null)
                    continue;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 실시간 D-HOUR 측정


                        SharedPreferences st = getSharedPreferences("timeinfo",MODE_PRIVATE);
                        long setnow = st.getLong("time_mil",0);
                        long timer;
                        boolean timesignal;



                        long now = (System.currentTimeMillis() + 9 * 60 * 60 * 1000)% (24 * 60 * 60 * 1000); // GMT +09:00 만큼 차이남. 우선 그냥 더했는데 해결법을 찾아야 할 듯함.


                        if (setnow > now) {
                            timer = (setnow - now) / 1000; // ( Hour * 3600  + Min * 60  + sec )* 10 # 1sec 단위
                            timesignal = true;
                        }
                        else {
                            timer = (now - setnow) / 1000; // ( Hour * 3600  + Min * 60  + sec )* 10 # 1sec 단위)
                            timesignal = false;
                        }

                        // SystemCurrrentTimeMillis 는 1970년 1월 1일 부터 진행한 ms

                        int setsec = (int) timer % 60;
                        int setmin = (int) (timer / 60) % 60;
                        int sethour = (int) timer / 3600;



                        TextView txt1 = findViewById(R.id.D_HOUR);
                        if (timesignal)
                            txt1.setText(String.format("D - %02d:%02d:%02d", sethour, setmin, setsec));
                        else
                            txt1.setText(String.format("D + %02d:%02d:%02d", sethour, setmin, setsec));
                        Intent intent = new Intent(getApplicationContext(),MyCounterService.class);
                        intent.putExtra("sethour",sethour);
                        intent.putExtra("setmin",setmin);
                        intent.putExtra("setsec",setsec);

                        intent.putExtra("swFacebook",swFacebook);
                        intent.putExtra("swInstagram",swInstagram);
                        intent.putExtra("swKakao",swKakao);
                        intent.putExtra("swYoutube",swYoutube);
                        startService(intent);



                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Intent intent;
    private boolean running = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startactivity);
        //Intent intent = new Intent(this, Loadingactivity.class);
        //startActivity(intent);

        final Button btn1 = (Button) findViewById(R.id.main_time);

        // App package id 저장하기
        SharedPreferences appid = getSharedPreferences("appinfo", MODE_PRIVATE);

        // 저장된 값을 불러와 Switch 값 변경하기






        // 저장된 시간을 불러와 Button 위의 Text로 바꾸기
        SharedPreferences st = getSharedPreferences("timeinfo", MODE_PRIVATE);

        String str = st.getString("time", "");
        if (str != "") {
            btn1.setText(str);
        } else {
            SharedPreferences.Editor editor = st.edit();
            editor.putString("time", "00:00");
            editor.commit();
            btn1.setText("00:00");
        }
        // 저장된 시간을 불러와 Button 위의 Text로 바꾸기p


        // 저장된 시간을 불러와 D-시간 계산하기 # Handler 사용

        intent = new Intent(Startactivity.this, MyCounterService.class);
        //startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
        running = true;
        new Thread(new GetCountThread()).start();
        // 저장된 시간을 불러와 D-시간 계산하기

        // Button을 눌러 TimePicker 호출하기
        btn1.setOnClickListener(
                new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TimePickerFragment mTimePickerFragment = new TimePickerFragment();
                        mTimePickerFragment.show(getSupportFragmentManager(), "timePicker"); // show
                        // Timepicker 호출 완료

                    }
                }

        );
        // 저장된 시간을 불러와 Button 위의 Text로 바꾸기

//
//        appSelectTextView = (TextView) findViewById(R.id.app_list_text);
//
//        startButton = (Button) findViewById(R.id.Edit_Button);
//        activityContext = this;

//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "Click Button");
//                Intent applist = new Intent(activityContext, AppPackageSelectListVIew.class);
//                startActivityForResult(applist, APP_LIST_RESULT_ACITIVTYKEY);
//            }
//        });

        SwitchCheck();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Activity 종료 전 저장
        SharedPreferences app = getSharedPreferences("appinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = app.edit();
        Switch swc = (Switch) findViewById(R.id.switch_facebook);
        editor.putBoolean("com.facebook.katana", swc.isChecked());
        swc = (Switch) findViewById(R.id.switch_kakao);
        editor.putBoolean("com.kakao.talk", swc.isChecked());
        swc = (Switch) findViewById(R.id.switch_youtube);
        editor.putBoolean("com.google.android.youtube", swc.isChecked());
        editor.commit();
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == APP_LIST_RESULT_ACITIVTYKEY && result == Activity.RESULT_OK) {
            if (data == null) {
                return;  // failed
            }

            ArrayList<String> applist = data.getStringArrayListExtra(USE_APP_LIST);

            if (applist != null && applist.size() != 0) {
                StringBuilder stringBuf = new StringBuilder();
                for (String appPackageName : applist) {
                    Log.d(TAG, "App getpackage name :" + appPackageName);
                    stringBuf.append("App getpackage name :" + appPackageName);
                    stringBuf.append("\r\n");
                }

                // 앱 설정 리스트 획득 // get App list
                appList = (ArrayList<String>) applist.clone();
                // 앱 설정 모드 획득 // get Set App Mode
                appMode = data.getBooleanExtra(USE_APP_MODE, true);


                Log.d(TAG, "App Mode :" + appMode);
                stringBuf.append("\r\n");
                stringBuf.append("App Mode :" + appMode);

                appSelectTextView.setText(stringBuf);

            } else {
                Log.e(TAG, " onAcitivyResult Not Define : " +
                        request + " : " + (result == Activity.RESULT_OK ? "ok" : "cancel"));

            }

            return;
        }
    }

    //    private void getPackageList() {
//
//        PackageManager pm = this.getPackageManager();
//
//        List<PackageInfo> packs =                   getPackageManager().getInstalledPackages(PackageManager.PERMISSION_GRANTED);
//
//        for (PackageInfo pack : packs) {
//
//            Log.d("TAG", "| name    : " + pack.packageName);
//
//            Log.d("TAG", "| package : " + pack.packageName);
//
//            Log.d("TAG", "| version : " + pack.versionName);
//
//        }
//
//    }


    public void SwitchCheck(){
  Switch aSwitchFacebook = (Switch) findViewById(R.id.switch_facebook);

      Switch aSwitchKakao = (Switch) findViewById(R.id.switch_kakao);

      Switch aSwitchYoutube = (Switch) findViewById(R.id.switch_youtube);

     Switch aSwitchInstagram = (Switch) findViewById(R.id.switch_instagram);
        aSwitchFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    swFacebook = (isChecked)? 1: 0;

            }
        });
        aSwitchInstagram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Intent intent = new Intent(getApplicationContext(), MyCounterService.class);
                    swInstagram = (isChecked)? 1: 0;

                //Toast.makeText(getApplication(),i,Toast.LENGTH_SHORT).show();
            }
        });
        aSwitchKakao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent intent = new Intent(getApplicationContext(), MyCounterService.class);
                    swKakao = (isChecked)? 1: 0;

                //Toast.makeText(getApplication(),i,Toast.LENGTH_SHORT).show();
            }
        });
        aSwitchYoutube.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Intent intent = new Intent(getApplicationContext(), MyCounterService.class);
                    swYoutube = (isChecked)? 1: 0;

               //
            }
        });


    }

}