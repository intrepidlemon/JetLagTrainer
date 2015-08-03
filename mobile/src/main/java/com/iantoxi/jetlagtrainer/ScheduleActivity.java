package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class ScheduleActivity extends FragmentActivity {
    private long scheduleId;
    private Schedule schedule;
    private SchedulePagerAdapter mSchedulePagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_schedule);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

        // Set up the ViewPager.
        mSchedulePagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.main_view);
        mViewPager.setAdapter(mSchedulePagerAdapter);
//
//        Intent intent = getIntent();
//        scheduleId = (long) intent.getExtras().get("scheduleId");
//        schedule = Schedule.findById(Schedule.class, scheduleId);
//
//        LayoutInflater inflater = getLayoutInflater();
//        RelativeLayout main = (RelativeLayout) findViewById(R.id.main_view);
//
//        View view = inflater.inflate(R.layout.night_layout, null,true);
//
//        main.addView(view);
//
//        drawSleepScheduleGraph();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

    /**
     * Currently has issue with Night object not being created fast enough
     * from previous activity, leaving the currentNight object null.
     */
    private void drawSleepScheduleGraph() {
        Night currentNight = schedule.currentNight;
        SleepScheduleGraphView graph = (SleepScheduleGraphView) findViewById(R.id.sleepScheduleGraph);
        graph.setSleepSchedule(currentNight.sleepTime, currentNight.wakeTime,
                schedule.destinationWakeTime, schedule.destinationSleepTime,
                schedule.zoneGap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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

}
