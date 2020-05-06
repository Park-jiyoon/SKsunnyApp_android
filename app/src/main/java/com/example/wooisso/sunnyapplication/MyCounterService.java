package com.example.wooisso.sunnyapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import static android.content.Intent.getIntent;
import static android.content.Intent.parseIntent;

public class MyCounterService extends Service   {
    public  String packageName = null;
    public String strPackageKakao = "com.kakao.talk";
    public String strPackageFacebook = "com.facebook.katana";
    public String strPackageYoutube = "com.google.android.youtube";
    public String strPackageInstagram = "com.instagram.android";
    public int sethour,setmin,setsec;
    public int swFacebook;
    public int swKakao;
    public int swYoutube;
    public int swInstagram;


    public MyCounterService() {    }

    private int count;
    private boolean isStop;


    iMyCounterService.Stub binder = new iMyCounterService.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread counter = new Thread(new Counter());
        counter.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isStop = true;
        return super.onUnbind(intent);
    }



    private class Counter implements Runnable {


        private Handler handler = new Handler();

        @Override
        public void run() {

            while (!isStop) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // BackGround 에서 실행할 내용
                        SharedPreferences st = getSharedPreferences("timeinfo",MODE_PRIVATE);
                        long setnow = st.getLong("time_mil",0);
                        long timer;
                        boolean timesignal;

                        long now = (System.currentTimeMillis() + 9 * 60 * 60 * 1000) % (24 * 60 * 60 * 1000); // GMT +09:00 만큼 차이남. 우선 9시간만큼 더했는데, 오류해결법을 찾아야할 듯함.

                        if (setnow > now) {
                            timer = (setnow - now) / 1000; // ( Hour * 3600  + Min * 60  + sec )* 10 # 1sec 단위
                            timesignal = true;
                        }
                        else {
                            timer = (now - setnow) / 1000; // ( Hour * 3600  + Min * 60  + sec )* 10 # 1sec 단위)
                            timesignal = false;
                        }
                        int setsec = (int) timer % 60;
                        // SystemCurrrentTimeMillis 는 1970년 1월 1일 부터 진행한 ms

                        boolean start;
                        start = AppActivityCheck();
                        // Alarm Maker
                        if (timesignal = true && start==true &&setsec ==0) {

                            Toast.makeText(getApplicationContext(),sethour +"시간"+setmin +"분 남았습니다.", Toast.LENGTH_LONG).show();
                        }


                        // App Detector

                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post((new Runnable() {
                @Override
                public void run() {

                }
            }));

        }


    }



    public boolean AppActivityCheck() {

        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.ic_icon);
        builder.setContentTitle("Slower Than Now");
        builder.setContentText("잠들기까지 "+sethour+"시간 "+setmin+"분 남았어요!");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process : list) {
            if (process.processName.equals(strPackageKakao) && swKakao==1) {
                packageName = process.processName;
                Log.d("TEST", packageName);
                //Toast.makeText(getApplicationContext(), "카카오톡 실행중", Toast.LENGTH_LONG).show();
                notificationManager.notify(1, builder.build());
                return true;
            }
            else if (process.processName.equals(strPackageInstagram) && swInstagram==1){
                packageName = process.processName;
                Log.d("TEST", packageName);
                notificationManager.notify(1, builder.build());
                return true;
                //Toast.makeText(getApplicationContext(), "인스타 그램실행중", Toast.LENGTH_LONG).show();
            }
            else if (process.processName.equals(strPackageFacebook) && swFacebook==1){
                packageName = process.processName;
                Log.d("TEST", packageName);
                notificationManager.notify(1, builder.build());
                return true;
                //Toast.makeText(getApplicationContext(), "페이스북 실행중", Toast.LENGTH_LONG).show();
            }
            else if (process.processName.equals(strPackageYoutube) && swYoutube==1){
                packageName = process.processName;
                Log.d("TEST", packageName);
                notificationManager.notify(1, builder.build());
                return true;
                //Toast.makeText(getApplicationContext(), "유튜브 실행중", Toast.LENGTH_LONG).show();
            }
        }
//        for(int i=0;i<list.size();i++){
//            list.remove(i);
//        }

        return false;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
     swFacebook = intent.getIntExtra("swFacebook",0);
     swKakao = intent.getIntExtra("swKakao",0);
     swYoutube = intent.getIntExtra("swYoutube",0);
     swInstagram = intent.getIntExtra("swInstagram",0);

     sethour = intent.getIntExtra("sethour",0);
     setmin = intent.getIntExtra("setmin",0);
     setsec = intent.getIntExtra("setsec",0);


     return START_STICKY;
    }
}
