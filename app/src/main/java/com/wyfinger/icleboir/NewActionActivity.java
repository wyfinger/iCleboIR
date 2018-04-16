package com.wyfinger.icleboir;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Arrays;

public class NewActionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView editDays, editTime, editActions;
    private String[] daysOfWeek;
    private SheduleAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daysOfWeek = new String[]{
                getResources().getString(R.string.scheduler_everyday),
                getResources().getString(R.string.scheduler_workdays),
                getResources().getString(R.string.scheduler_weekend),
                getResources().getString(R.string.scheduler_monday),
                getResources().getString(R.string.scheduler_tuesday),
                getResources().getString(R.string.scheduler_wednesday),
                getResources().getString(R.string.scheduler_thursday),
                getResources().getString(R.string.scheduler_friday),
                getResources().getString(R.string.scheduler_saturday),
                getResources().getString(R.string.scheduler_sunday)
        };

        setContentView(R.layout.activity_newaction);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // add back button with arrow to ActionBar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set Views OnClick listeners
        findViewById(R.id.textDays).setOnClickListener(this);
        (editDays = (TextView) findViewById(R.id.editDays)).setOnClickListener(this);
        findViewById(R.id.textTime).setOnClickListener(this);
        (editTime = (TextView) findViewById(R.id.editTime)).setOnClickListener(this);
        findViewById(R.id.textActions).setOnClickListener(this);
        (editActions = (TextView) findViewById(R.id.editActions)).setOnClickListener(this);

        // load Action from Intent (blank or Action for edit)
        action = getIntent().getParcelableExtra(getPackageName() + ".action");
        editDays.setText(action.days.toString());
        editTime.setText(action.time.toString());
        editActions.setText(action.script.toString());
        action.enable = true;

        // if edit mode change toolbar title
        if (getIntent().getIntExtra(getPackageName() + ".request",
                ScheduleActivity.REQUEST_ADD) == ScheduleActivity.REQUEST_ADD) {
            //getSupportActionBar().setTitle(R.string.scheduler_new_action);
            ((TextView) findViewById(R.id.textTitleNewAction)).setText(R.string.scheduler_new_action);
        } else {
            //getSupportActionBar().setTitle(R.string.scheduler_edit_action);
            ((TextView) findViewById(R.id.textTitleNewAction)).setText(R.string.scheduler_edit_action);
        }

    }

    //@Override
    //public boolean onSupportNavigateUp() {
    //    onBackPressed();
    //    return false;
    //}

    public void onBackNewActionClick(View v) {
        finish();
    }

    public void onOkNewActionClick(View v) {
        Intent intent = new Intent();
        if (action.days.isNewer()) {
            action.enable = false;
        }
        intent.putExtra(getPackageName() + ".action", action);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean[] daysChecksSave;
    private boolean[] daysChecks;

    private void ActionDaysToDaysChecks() {
        daysChecks[0] = action.days.isEveryday();
        daysChecks[1] = action.days.isWorkdays();
        daysChecks[2] = action.days.isWeekend();
        daysChecks[3] = action.days.mon;
        daysChecks[4] = action.days.tue;
        daysChecks[5] = action.days.wed;
        daysChecks[6] = action.days.thu;
        daysChecks[7] = action.days.fri;
        daysChecks[8] = action.days.sat;
        daysChecks[9] = action.days.sun;
    }

    public static final int REQUEST_RECORD = 1;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editDays:
                // load checks array
                daysChecks = new boolean[10];
                ActionDaysToDaysChecks();
                daysChecksSave = Arrays.copyOf(daysChecks, daysChecks.length);
                // day of week select dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewActionActivity.this);
                builder.setTitle(getResources().getString(R.string.scheduler_dayofweek));
                builder.setCancelable(true);
                builder.setMultiChoiceItems(daysOfWeek, daysChecks,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                ListView list = ((AlertDialog) dialog).getListView();
                                switch (which) {
                                    case 0:
                                        action.days.setEveryday(isChecked);
                                        break;
                                    case 1:
                                        action.days.setWorkdays(isChecked);
                                        break;
                                    case 2:
                                        action.days.setWeekend(isChecked);
                                        break;
                                    case 3:
                                        action.days.mon = isChecked;
                                        break;
                                    case 4:
                                        action.days.tue = isChecked;
                                        break;
                                    case 5:
                                        action.days.wed = isChecked;
                                        break;
                                    case 6:
                                        action.days.thu = isChecked;
                                        break;
                                    case 7:
                                        action.days.fri = isChecked;
                                        break;
                                    case 8:
                                        action.days.sat = isChecked;
                                        break;
                                    case 9:
                                        action.days.sun = isChecked;
                                        break;
                                }
                                ActionDaysToDaysChecks();
                                for (int i = 0; i < list.getCount(); ++i) {
                                    list.setItemChecked(i, daysChecks[i]);
                                }
                            }
                        });
                builder.setPositiveButton(getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // ok
                                editDays.setText(action.days.toString());
                            }
                        });
                builder.setNegativeButton(getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel, restore days checkboxes
                                daysChecks = Arrays.copyOf(daysChecksSave, daysChecksSave.length);
                                action.days.mon = daysChecks[3];
                                action.days.tue = daysChecks[4];
                                action.days.wed = daysChecks[5];
                                action.days.thu = daysChecks[6];
                                action.days.fri = daysChecks[7];
                                action.days.sat = daysChecks[8];
                                action.days.sun = daysChecks[9];
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.editTime:
                // time select dialog
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        action.time.setHours(hourOfDay);
                        action.time.setMinutes(minute);
                        editTime.setText(action.time.toString());
                    }
                }, action.time.getHours(), action.time.getMinutes(), true).show();
                break;
            case R.id.editActions:
                // show main activity with request
                Intent intent = new Intent(this, MainActivity.class);
                intent.setAction("record");
                startActivityForResult(intent, REQUEST_RECORD);
                Toast.makeText(getApplicationContext(),
                        R.string.scheduler_message_script, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if ((requestCode == REQUEST_RECORD) & (resultCode == RESULT_OK)) {
            action.script = data.getParcelableExtra(getPackageName() + ".recordScript");
            editActions.setText(action.script.toString());
        }
    }

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    if (item.getItemId() == R.id.action_ok) {
    //
    //    }
    //    return false;
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_action_menu, menu);
        return true;
    }

}
