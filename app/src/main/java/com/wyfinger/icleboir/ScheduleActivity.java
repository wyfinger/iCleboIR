package com.wyfinger.icleboir;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends AppCompatActivity {

    class SheduleAdapter extends BaseAdapter {

        ScheduleActions data;

        SheduleAdapter(Context context, ScheduleActions data) {
            if (data != null) {
                this.data = data;
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(App.getAppContext());
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sheduler_list_item, parent, false);
            }
            final TextView textDayAndTime = (TextView) convertView.findViewById(R.id.textDayAndTime);
            final TextView textScript = (TextView) convertView.findViewById(R.id.textScript);
            Switch switchEnable = (Switch) convertView.findViewById(R.id.switchEnable);

            final String line1 = data.get(position).days + ", " + data.get(position).time;
            String line2 = data.get(position).script.toString();
            textDayAndTime.setText(line1);
            textScript.setText(line2);
            switchEnable.setChecked(data.get(position).enable);

            // attach listener to switch
            switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean newStatus) {
                    // switch
                    if (newStatus & data.get(position).days.isNewer()) {
                        compoundButton.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                R.string.scheduler_message_never, Toast.LENGTH_LONG).show();
                    } else {
                        data.get(position).enable = newStatus;
                        data.clearAlarms();
                        data.setAlarms();
                        textDayAndTime.setEnabled(newStatus);
                        textScript.setEnabled(newStatus);
                    }
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openContextMenu(v);
                    return true;
                }
            });

            return convertView;
        }

    }

    SheduleAdapter listAdapter;

    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // show back button with arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // add floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(ScheduleActivity.this, NewActionActivity.class);
                intent.putExtra(getPackageName()+".action", new SheduleAction());
                startActivityForResult(intent, REQUEST_ADD);
            }
        });

        // action list adapter
        ListView listActions;
        listActions = (ListView) this.findViewById(R.id.listActions);
        registerForContextMenu(listActions);

        listAdapter = new SheduleAdapter(this, App.actionsData);
        listActions.setAdapter(listAdapter);

        // in first time show help message
        if (App.actionsData.needInfoTip) {
            Snackbar.make(listActions, R.string.scheduler_message_about, 10000)
                .setAction("Action", null).show();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_item_menu, menu);
    }

    int actionPos;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}

        if ((requestCode == REQUEST_ADD) & (resultCode == RESULT_OK)) {
            SheduleAction action = data.getParcelableExtra(getPackageName()+".action");
            App.actionsData.add(action);
            App.actionsData.save();
            listAdapter.notifyDataSetChanged();
        }
        if ((requestCode == REQUEST_EDIT) & (resultCode == RESULT_OK)) {
            SheduleAction action = data.getParcelableExtra(getPackageName()+".action");
            App.actionsData.set(actionPos, action);
            App.actionsData.save();
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                // edit Action
                Intent intent = new Intent(ScheduleActivity.this, NewActionActivity.class);
                intent.putExtra(getPackageName() + ".action", App.actionsData.get(info.position));
                intent.putExtra(getPackageName() + ".request", REQUEST_EDIT);
                actionPos = info.position;
                startActivityForResult(intent, REQUEST_EDIT);
                return true;
            case R.id.action_delete:
                // delete Action
                App.actionsData.remove(info.position);
                App.actionsData.save();
                listAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onBackShedulerClick(View v) {
        finish();
    }

    //@Override
    //public boolean onSupportNavigateUp() {
    //    onBackPressed();
    //    return true;
    //}

}
