package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;

public class ScheduleActivity extends Activity  {
    private static HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    private UpdateReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);
        Intent intent = new Intent(ScheduleActivity.this, SendServiceToMobile.class);
        intent.putExtra("message", "schedule");
        startService(intent);

        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, new IntentFilter("data"));

        fillSchedule();
    }

    private void fillSchedule() {
        Object[] times = hashMap.keySet().toArray();
        Arrays.sort(times);

        LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout1.setVisibility(View.INVISIBLE);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout2.setVisibility(View.INVISIBLE);
        LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout3);
        layout3.setVisibility(View.INVISIBLE);
        LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout4);
        layout4.setVisibility(View.INVISIBLE);

        if (times.length > 0) {
            layout1.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time1);
            TextView event = (TextView) findViewById(R.id.event1);
            time.setText(times[0].toString());
            event.setText(hashMap.get(times[0]));
        }
        if (times.length > 1)  {
            layout2.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time2);
            TextView event = (TextView) findViewById(R.id.event2);
            time.setText(times[1].toString());
            event.setText(hashMap.get(times[1]));
        }
        if (times.length > 2)  {
            layout3.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time3);
            TextView event = (TextView) findViewById(R.id.event3);
            time.setText(times[2].toString());
            event.setText(hashMap.get(times[2]));
        }
        if (times.length > 3)  {
            layout4.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time4);
            TextView event = (TextView) findViewById(R.id.event4);
            time.setText(times[3].toString());
            event.setText(hashMap.get(times[3]));
        }
    }

    private static class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hashMap.put(intent.getIntExtra("time", 0), intent.getStringExtra("event"));
        }
    }

/*    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(updateReceiver, new IntentFilter("data"));
    }*/

}
