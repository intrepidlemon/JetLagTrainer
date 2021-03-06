package com.iantoxi.jetlagtrainer;

import android.content.Intent;
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

/** Service that listens for messages or schedule information from mobile. */
public class ListenerServiceFromMobile extends WearableListenerService{

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("light")) {
            Intent intent = new Intent(this, LightSensor.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equals("schedule")) {
            Intent intent = new Intent(this, ScheduleDisplay.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (messageEvent.getPath().equals("no_schedule")) {
            Toast toast = Toast.makeText(getApplicationContext(), "No active schedule to display",
                    Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(14);
            toast.show();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri != null ? uri.getPath() : null;
            if ("/data".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Integer time = map.getInt("time");
                String evnt = map.getString("event");
                // Broadcasts schedule information data as it is received.
                Intent intent = new Intent();
                intent.setAction("data");
                intent.putExtra("time", time);
                intent.putExtra("event", evnt);
                getApplicationContext().sendBroadcast(intent);
            }
        }
    }
}
