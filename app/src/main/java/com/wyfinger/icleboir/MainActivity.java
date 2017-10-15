package com.wyfinger.icleboir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    ConsumerIrManager manager;
    Vibrator vibrator;
    SendTimerTask sendTimerTask = new SendTimerTask();
    Timer sendTimer = new Timer();
    int lastCommand = 0;
    long lastCmdTime = 0;
    int lastCmdCntr = 0;
    //boolean repeat = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // no title
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // vertical screen only
        manager = (ConsumerIrManager) this.getSystemService(CONSUMER_IR_SERVICE);
        //vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sendTimerTask = new SendTimerTask();
        sendTimer = new Timer();
        sendTimer.schedule(sendTimerTask, 150, 150);


        // set onTouch listener for all buttons on view
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //vibrator.vibrate(50);
                    // send code
                    switch (v.getId()) {
                        case R.id.btnPower:  onPowerClick(null);   break;
                        case R.id.btnStart:  onStartClick(null);   break;
                        case R.id.btnUp:  onUpClick(null);   break;
                        case R.id.btnLeft:  onLeftClick(null);   break;
                        case R.id.btnSelect:  onSelectClick(null);   break;
                        case R.id.btnRight:  onRightClick(null);   break;
                        case R.id.btnDown:  onDownClick(null);   break;
                        case R.id.btnMode:  onModeClick(null);   break;
                        case R.id.btnClock:  onClockClick(null);   break;
                        case R.id.btnTimer:  onTimerClick(null);   break;
                        case R.id.btnBase:  onBaseClick(null);   break;
                        case R.id.btnClimb:  onClimbClick(null);   break;
                        case R.id.btnSpot:  onSpotClick(null);   break;
                        case R.id.btnMax:  onMaxClick(null);   break;
                    }
                    // and start repeat timer
                    //repeat = true;
                    lastCommand = v.getId();
                    lastCmdTime = System.currentTimeMillis();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //repeat = false;
                    lastCommand = 0;
                }
                return false;
            }
        };
        ((Button) findViewById(R.id.btnPower)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnStart)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnUp)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnLeft)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnSelect)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnRight)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnDown)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnMode)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnClock)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnTimer)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnBase)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnClimb)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnSpot)).setOnTouchListener(touchListener);
        ((Button) findViewById(R.id.btnMax)).setOnTouchListener(touchListener);
    }

    private class SendTimerTask extends TimerTask {

        @Override
        public void run() {
            if ((lastCommand > 0) & (System.currentTimeMillis() - lastCmdTime > 150)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastCmdCntr < 8) {             // send REPEAT code
                            int[] pattern = {9000, 2250, 562, 1000};
                            manager.transmit(38000, pattern);
                            lastCmdCntr = lastCmdCntr + 1;
                        } else {
                            switch (lastCommand) {         //
                                case R.id.btnPower:  onPowerClick(null);   break;
                                case R.id.btnStart:  onStartClick(null);   break;
                                case R.id.btnUp:  onUpClick(null);   break;
                                case R.id.btnLeft:  onLeftClick(null);   break;
                                case R.id.btnSelect:  onSelectClick(null);   break;
                                case R.id.btnRight:  onRightClick(null);   break;
                                case R.id.btnDown:  onDownClick(null);   break;
                                case R.id.btnMode:  onModeClick(null);   break;
                                case R.id.btnClock:  onClockClick(null);   break;
                                case R.id.btnTimer:  onTimerClick(null);   break;
                                case R.id.btnBase:  onBaseClick(null);   break;
                                case R.id.btnClimb:  onClimbClick(null);   break;
                                case R.id.btnSpot:  onSpotClick(null);   break;
                                case R.id.btnMax:  onMaxClick(null);   break;
                            }
                            lastCmdCntr = 0;
                        }
                    }
                });
            }
        }
    }

    private void onPowerClick(View view) {
        int[] pattern = {8950, 4450, 550, 1700, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 550, 1700, 550, 1700, 550,
                1700, 550, 1700, 550, 1700, 550, 1700, 550, 1700, 550, 1700, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700,
                550, 1700, 550, 1700, 550, 1700, 550};
        manager.transmit(38000, pattern);
    }

    private void onStartClick(View view) {
        int[] pattern = {8900, 4450, 550, 1700, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 550, 1700, 550, 1700, 550,
                1700, 550, 1700, 550, 1700, 550, 550, 550, 550, 550, 1700, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 550, 550, 550, 1700, 550,
                1700, 550, 1700, 550, 1700, 550, 1700, 550};
        manager.transmit(38000, pattern);
    }

    private void onUpClick(View view) {
        int[] pattern = {8900, 4450, 550, 1700, 550, 600, 550, 600, 550, 600, 550, 600, 550, 600,
                550, 600, 550, 600, 550, 550, 550, 1700, 550, 1700, 550, 1700, 550, 1700, 550,
                1700, 550, 1700, 550, 1700, 550, 600, 550, 600, 550, 550, 550, 1700, 550, 600,
                550, 600, 550, 550, 550, 600, 550, 1700, 550, 1700, 550, 1700, 550, 550, 550,
                1700, 550, 1700, 550, 1700, 550, 1700, 550};
        manager.transmit(38000, pattern);
    }

    private void onLeftClick(View view) {
        int[] pattern = {8950, 4450, 600, 1650, 550, 550, 600, 550, 600, 550, 550, 550, 600, 550,
                600, 550, 550, 550, 550, 550, 600, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1700, 550, 1650, 550, 1650, 550, 550, 600, 1650, 550, 550,
                600, 550, 600, 550, 600, 550, 550, 550, 550, 550, 600, 1650, 550, 550, 600, 1650,
                550, 1650, 550, 1700, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onSelectClick(View view) {
        int[] pattern = {8950, 4450, 600, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 550, 550, 550, 550, 1650, 550, 1700, 550, 550,
                550, 550, 550, 550, 600, 550, 550, 1650, 550, 1650, 550, 550, 550, 550, 550, 1650,
                550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onRightClick(View view) {
        int[] pattern = {8950, 4450, 600, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 550, 550, 1700, 550, 550,
                550, 550, 550, 550, 550, 550, 600, 1650, 550, 550, 550, 1650, 550, 550, 550,
                1700, 550, 1650, 550, 1700, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onDownClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550, 550, 550, 550, 550, 1650, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 550, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onModeClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1700, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 1650, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 550, 550, 550, 550, 1700, 550,
                1650, 550, 1700, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onClockClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 550, 550, 1650, 550, 1650,
                550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onTimerClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 600, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onBaseClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 550, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onClimbClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1700, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onSpotClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650,
                550, 1650, 550, 1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 1650, 550,
                550, 550, 550, 550, 550, 550, 550, 550, 550, 550, 1650, 550, 550, 550, 550, 550,
                1650, 550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }

    private void onMaxClick(View view) {
        int[] pattern = {8950, 4450, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 550, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 1650, 550,
                1650, 550, 1650, 550, 1650, 550, 550, 550, 1650, 550, 1650, 550, 1650, 550, 550,
                550, 550, 550, 550, 550, 550, 550, 1650, 550, 550, 550, 550, 550, 550, 550, 1650,
                550, 1650, 550, 1650, 550, 1650, 550};
        manager.transmit(38000, pattern);
    }
}
