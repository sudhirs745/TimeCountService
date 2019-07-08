package com.sudhir.timecountservice;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView time;

    public static Handler sHandler;
    private int secs = 0;
    private int mins = 0;
    private int millis = 0;
    private long currentTime = 0L;
    private boolean isBound = false;
    private boolean isRuning = false;
    private MyService myService;
    private Intent mIntent;
    Button startPause;
    private long mEndTime;

    long timeSwapBuff = 0L;

    SharedPreferences prefs ;

    boolean  DataIsRuning ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.time);

        startPause = findViewById(R.id.startPause);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        long DatacurrentTime = prefs.getLong("millisLeft", 0);
        DataIsRuning = prefs.getBoolean("timerRunning", false);
        long  DataEnd = prefs.getLong("endTime", 0);

  if(DatacurrentTime>0) {
      timeSwapBuff = (System.currentTimeMillis() - DataEnd) + DatacurrentTime;

  }else {
      timeSwapBuff=0;
  }
        mIntent = new Intent(this, MyService.class);

        if (!isMyServiceRunning(MyService.class)) {
            startService(mIntent);
            bindService(mIntent, myConnection, Context.BIND_AUTO_CREATE);
        } else {
            bindService(mIntent, myConnection, Context.BIND_AUTO_CREATE);


        }

        MainActivity.sHandler = new Handler() {

            @Override
            public void handleMessage(Message timeMsg) {
                super.handleMessage(timeMsg);

                currentTime = Long.valueOf(timeMsg.obj.toString());

                secs = (int) (currentTime / 1000);
                mins = secs / 60;
                secs = secs % 60;
                millis = (int) (currentTime % 1000);
                setTime();
            }
        };


    }


    public void startPause(View v) {

        if (timeSwapBuff > 0) {

            if (!DataIsRuning) {
                myService.startStop(timeSwapBuff, false);
                isRuning = true;
                DataIsRuning=true;
                startPause.setText("pause");
            }else {
                myService.startStop(timeSwapBuff, true);
                isRuning = false;
                DataIsRuning=false;
                startPause.setText("start");
            }

        }else {
            myService.startStop();

            if (!isRuning) {
                DataIsRuning=true;
                startPause.setText("pause");
                isRuning = true;
            } else if (isRuning) {
                DataIsRuning=false;
                startPause.setText("start");
                isRuning = false;
            }
        }





    }

    public void resetChronometer(View v) {

        myService.reset();
        mins = 0;
        secs = 0;
        millis = 0;
        setTime();
        startPause.setText("start");
        timeSwapBuff=0;
        isRuning = false;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", 0);
        editor.putBoolean("timerRunning", false);
        editor.putLong("endTime", 0);
        editor.commit();

    }


    public void exit(View v) {

        myService.reset();
        stopService(mIntent);
        finishAffinity();
    }



    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;

            if (timeSwapBuff > 0) {

                if (DataIsRuning) {
                    myService.startStop(timeSwapBuff, false);
                    isRuning = true;
                    DataIsRuning=true;
                    startPause.setText("pause");
                }

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void setTime() {
        time.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                + String.format("%03d", millis));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mEndTime = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", currentTime);
        editor.putBoolean("timerRunning", isRuning);
        editor.putLong("endTime", mEndTime);
        editor.commit();

    }





//    protected void () {
//        super.onStop();
//
//        mEndTime = System.currentTimeMillis() + currentTime;
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        editor.putLong("millisLeft", currentTime);
//        editor.putBoolean("timerRunning", isRuning);
//        editor.putLong("endTime", mEndTime);
//        editor.apply();
//
//
//    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//
//        currentTime = prefs.getLong("millisLeft", 0);
//        isRuning = prefs.getBoolean("timerRunning", false);
//
//        if (isRuning) {
//            mEndTime = prefs.getLong("endTime", 0);
//            currentTime = mEndTime - System.currentTimeMillis();
//
//            if (currentTime < 0) {
//                currentTime = 0;
//                isRuning = false;
//                timeSwapBuff=currentTime ;
//
//            }
//        }
//    }


}
