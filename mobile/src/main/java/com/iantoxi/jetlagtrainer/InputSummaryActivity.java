package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/** Class that displays all the information that the user has inputted so that they can verify
 *  everything is correct before calculating their sleep schedule. */
public class InputSummaryActivity extends Activity {

    private long scheduleId;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_summary);
        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);

        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

        updateTextView(R.id.origin, schedule.originTimezone);
        updateTextView(R.id.destination, schedule.destinationTimezone);
        updateTextView(R.id.travel_date, formatDate(schedule.travelDate));

        updateTextView(R.id.sleep_time, timeConversion(schedule.originSleepTime));
        updateTextView(R.id.wake_time, timeConversion(schedule.originWakeTime));

        updateTextView(R.id.melatonin, boolToYesNo(schedule.melatoninStrategy));
        updateTextView(R.id.light, boolToYesNo(schedule.lightStrategy));
    }

    private String formatDate(Calendar date) {
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);
        return month + " " + day + ", " + year;
    }

    private String boolToYesNo(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    private void updateTextView(int id, String text) {
        TextView view = (TextView) findViewById(id);
        view.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calculateSchedule(View view) {
        schedule.calculateSchedule();
        schedule.save();
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        intent.putExtra("placeholder", "normal");
        intent.putExtra("finished", true);
        intent.putExtra("reminder", true);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

    /** Converts time from being 0 - 2880 min format to human readable AM PM format. */
    private String timeConversion(Integer minutes) {
        minutes %= 1440;
        String ampm = " AM";
        String colon = ":";
        int hours;

        if (minutes >= 720) {
            ampm = " PM";
            minutes %= 720;
        }

        hours = minutes/60;
        if (hours == 0)
            hours = 12;
        minutes %= 60;

        if (minutes < 10)
            colon = ":0";

        return hours + colon + minutes + ampm;
    }
}
