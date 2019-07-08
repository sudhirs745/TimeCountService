package com.sudhir.timecountservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {


    private boolean isRunning = false;
    private long startTime = 0;
    private long timeInMilliseconds = 0;
    private long timeSwapBuff = 0;
    private long updatedTime = 0;
    private final IBinder mBinder = new LocalBinder();
    private Message timeMsg;

    public MyService( ) {


    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            Log.d("Czas:", String.valueOf(updatedTime));

            timeMsg = new Message();
            timeMsg.obj = updatedTime;
            MainActivity.sHandler.sendMessage(timeMsg);
            MainActivity.sHandler.postDelayed(this, 10);

        }
    };

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


    public void startStop(long timeSwapBuff , boolean isRunning){

        this.timeSwapBuff =timeSwapBuff;
        this.isRunning =isRunning;

        if (isRunning) {
            timeSwapBuff += timeInMilliseconds;
            MainActivity.sHandler.removeCallbacks(updateTimer);
            isRunning = false;
        } else {
            startTime = SystemClock.uptimeMillis();
            updateTimer.run();
//            MainActivity.sHandler.postDelayed(updateTimer, 10);
            isRunning = true;
        }
    }


    public void startStop(){

        if (isRunning) {
            timeSwapBuff += timeInMilliseconds;
            MainActivity.sHandler.removeCallbacks(updateTimer);
            isRunning = false;
        } else {
            startTime = SystemClock.uptimeMillis();
            updateTimer.run();
//            MainActivity.sHandler.postDelayed(updateTimer, 10);
            isRunning = true;
        }
    }


    public void reset(){

        MainActivity.sHandler.removeCallbacks(updateTimer);
        isRunning=false;
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;

        timeMsg = new Message();
        timeMsg.obj = updatedTime;
        MainActivity.sHandler.sendMessage(timeMsg);

    }



    public class LocalBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }


}
