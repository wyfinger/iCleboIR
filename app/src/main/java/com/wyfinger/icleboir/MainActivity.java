package com.wyfinger.icleboir;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ConsumerIrManager manager;
    Vibrator vibrator;
    SendTimerTask sendTimerTask = new SendTimerTask();
    Timer sendTimer = new Timer();
    boolean repeat = false;
    ActionScript recordScript;
    boolean recMode = false;

    private View.OnClickListener btnAboutClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    };

    AlarmManager am;
    Intent intent1;
    PendingIntent pIntent1;

    private View.OnClickListener btnSettingsClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) & (recMode)) {
            //Do stuff
            Intent intent = new Intent();
            intent.putExtra(getPackageName() + ".recordScript", recordScript);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // no title
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // vertical screen only
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sendTimerTask = new SendTimerTask();
        sendTimer = new Timer();
        sendTimer.schedule(sendTimerTask, 200, 200);

        manager = (ConsumerIrManager) this.getSystemService(CONSUMER_IR_SERVICE);

        // record mode
        recMode = ("record".equals(getIntent().getAction()));

        ImageView btnAbout = (ImageView) findViewById(R.id.btnAbout);
        ImageView btnSettings = (ImageView) findViewById(R.id.btnSettings);

        recordScript = new ActionScript();

        if (recMode) {
            btnAbout.setVisibility(View.INVISIBLE);
            btnSettings.setVisibility(View.INVISIBLE);
            recordScript.clear();
        } else {
            btnAbout.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);
            btnAbout.setOnClickListener(btnAboutClickListener);
            btnSettings.setOnClickListener(btnSettingsClickListener);
        }

        // set onTouch listener for all buttons on view
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                vibrator.vibrate(50);
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
                    case R.id.btnTurbo:  onTurboClick(null);   break;
                    case R.id.btnMax:  onMaxClick(null);   break;
                }
                // and start repeat timer
                repeat = true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                repeat = false;
            }
            return false;
            }
        };

        if (manager.hasIrEmitter()) {
            ((ImageButton) findViewById(R.id.btnPower)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnStart)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnUp)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnLeft)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnSelect)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnRight)).setOnTouchListener(touchListener);
            ((ImageButton) findViewById(R.id.btnDown)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnMode)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnClock)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnTimer)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnBase)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnClimb)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnSpot)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnTurbo)).setOnTouchListener(touchListener);
            ((Button) findViewById(R.id.btnMax)).setOnTouchListener(touchListener);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.ir_missing, Toast.LENGTH_LONG).show();
        }
    }

    private class SendTimerTask extends TimerTask {
        @Override
        public void run() {
            if (repeat) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int[] pattern = {9000, 2250, 562, 1000};
                        manager.transmit(38000, pattern);
                    }
                });
            }
        }
    }

    private void onPowerClick(View view) {
        recordScript.play(ActionScript.ID_POWER);
        recordScript.add(ActionScript.ID_POWER);
    }

    private void onStartClick(View view) {
        recordScript.play(ActionScript.ID_START);
        recordScript.add(ActionScript.ID_START);
    }

    private void onUpClick(View view) {
        recordScript.play(ActionScript.ID_UP);
        recordScript.add(ActionScript.ID_UP);
    }

    private void onLeftClick(View view) {
        recordScript.play(ActionScript.ID_LEFT);
        recordScript.add(ActionScript.ID_LEFT);
    }

    private void onSelectClick(View view) {
        recordScript.play(ActionScript.ID_SELECT);
        recordScript.add(ActionScript.ID_SELECT);
    }

    private void onRightClick(View view) {
        recordScript.play(ActionScript.ID_RIGHT);
        recordScript.add(ActionScript.ID_RIGHT);
    }

    private void onDownClick(View view) {
        recordScript.play(ActionScript.ID_DOWN);
        recordScript.add(ActionScript.ID_DOWN);
    }

    private void onModeClick(View view) {
        recordScript.play(ActionScript.ID_MODE);
        recordScript.add(ActionScript.ID_MODE);
    }

    private void onClockClick(View view) {
        recordScript.play(ActionScript.ID_CLOCK);
        recordScript.add(ActionScript.ID_CLOCK);
    }

    private void onTimerClick(View view) {
        recordScript.play(ActionScript.ID_TIMER);
        recordScript.add(ActionScript.ID_TIMER);
    }

    private void onBaseClick(View view) {
        recordScript.play(ActionScript.ID_BASE);
        recordScript.add(ActionScript.ID_BASE);
    }

    private void onClimbClick(View view) {
        recordScript.play(ActionScript.ID_CLIMB);
        recordScript.add(ActionScript.ID_CLIMB);
    }

    private void onSpotClick(View view) {
        recordScript.play(ActionScript.ID_SPOT);
        recordScript.add(ActionScript.ID_SPOT);
    }

    private void onTurboClick(View view) {
        recordScript.play(ActionScript.ID_TURBO);
        recordScript.add(ActionScript.ID_TURBO);
    }

    private void onMaxClick(View view) {
        recordScript.play(ActionScript.ID_MAX);
        recordScript.add(ActionScript.ID_MAX);
    }
}
