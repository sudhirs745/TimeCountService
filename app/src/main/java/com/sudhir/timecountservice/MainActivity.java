package com.sudhir.timecountservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.time);

        startPause= findViewById(R.id.startPause);
        mIntent = new Intent(this, MyService.class);
        startService(mIntent);
        bindService(mIntent, myConnection, Context.BIND_AUTO_CREATE);

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

        myService.startStop();
        if(!isRuning){
            startPause.setText("pause");

            isRuning = true;
        } else if(isRuning){
            startPause.setText("start");

            isRuning = false;
        }

    }

    public void resetChronometer(View v) {

        myService.reset();
        mins = 0;
        secs = 0;
        millis = 0;
        setTime();
        startPause.setText("start");

        isRuning=false;

    }


    public void exit(View v) {

        myService.reset();
        stopService(mIntent);
        finishAffinity();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
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
}
