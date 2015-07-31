package com.iantoxi.jetlagtrainer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Josef Nunez on 7/29/15.
 *
 * View that draws Sleep Schedule.
 */
public class SleepScheduleGraphView extends View {

    // Stroke Width for Paint Object. Determines width of sine curve line.
    private final float STROKE_WIDTH = 6f;
    // Initial hour on graph.
    private final int INITIAL_TIME = 12;
    // Terminal hour on graph (not mod 24).
    private final int TERMINAL_TIME = 36;
    // x-coordinate for Left border of View.
    private int LEFT;
    // x-coordinate for Right border of View.
    private int RIGHT;
    // Width of View.
    private int WIDTH;
    // Number of seconds per hour.
    private final int NUM_SECS_PER_HOUR = 3600;
    // Number of seconds per day.
    private final int NUM_SECS_PER_DAY = 86400;
    // Shift to place sine values on graph.
    private int verticalShift;
    // Amplitude to fill space on graph.
    private int amplitude;
    // Graph width.
    private int graphWidth;
    // y-coordinate of Current Time axis labels.
    private int yCurrentTimeAxis;
    // y-coordinate of Target Time axis labels.
    private int yTargetTimeAxis;
    // Height of axes labels.
    private int axesTextSize;
    // Paint object to use throughout drawing process.
    private Paint paint;
    // Rect object to use throughout drawing process.
    private Rect rect;
    // Canvas to draw sleep schedule.
    private Canvas mCanvas;
    // Current Bedtime and Wake Time.
    private float bedTime;
    private float wakeTime;
    // Target Bedtime and Wake Time.
    private float targetBedTime;
    private float targetWakeTime;
    // Time Zone Difference.
    private float timeDiff;

    public SleepScheduleGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint = new Paint();
        paint.getFontMetrics(fm);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        LEFT = getLeft();
        RIGHT = getRight();
        WIDTH = RIGHT - LEFT;

        // Set heights of different graph sections:
        // 1) Title bar heights.
        // 2) Font size of titles and axes labels.
        // 3) Graph size (sine function section of View).
        int timeLabelsHeight = 120;
        int timeLabelsFontSize = timeLabelsHeight/2;
        int timeAxesHeight = 90;
        axesTextSize = timeAxesHeight/2;
        int graphHeight = 400;

        // Draw Current Time title bar.
        int topcurrentTimeTitle = 0;
        int bottomCurrentTimeTitle = timeLabelsHeight;
        setPaintAttributes(paint, Color.BLACK, Paint.Style.FILL);
        drawRect(canvas, LEFT, topcurrentTimeTitle, RIGHT, bottomCurrentTimeTitle, paint);
        setPaintAttributes(paint, Color.WHITE, timeLabelsFontSize, Paint.Align.LEFT, Typeface.DEFAULT);
        canvas.drawText("Current Time", LEFT, bottomCurrentTimeTitle-20, paint);

        // Draw Current Time axis.
        int topCurrentTimeAxis = bottomCurrentTimeTitle;
        int bottomCurrentTimeAxis = topCurrentTimeAxis + timeAxesHeight;
        yCurrentTimeAxis = bottomCurrentTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topCurrentTimeAxis, RIGHT, bottomCurrentTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topCurrentTimeAxis, RIGHT, bottomCurrentTimeAxis, paint);

        // Draw Graph. Dark shaded night half created by additional rectangle.
        int topGraph = bottomCurrentTimeAxis;
        int bottomGraph = topGraph + graphHeight;
        int topNight = topGraph + (graphHeight/2);
        int bottomNight = bottomGraph;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topGraph, RIGHT, bottomGraph, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topGraph, RIGHT, bottomGraph, paint);
        setPaintAttributes(paint, Color.GRAY, Paint.Style.FILL);
        drawRect(canvas, LEFT, topNight, RIGHT, bottomNight, paint);

        // Save graph mid-line coordinate and height of graph.
        verticalShift = topNight;
        amplitude = Math.abs((graphHeight/2) - 5);
        graphWidth = RIGHT - LEFT;

        // Draw Target Time axis.
        int topTargetTimeAxis = bottomGraph;
        int bottomTargetTimeAxis = topTargetTimeAxis + timeAxesHeight;
        yTargetTimeAxis = bottomTargetTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topTargetTimeAxis, RIGHT, bottomTargetTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topTargetTimeAxis, RIGHT, bottomTargetTimeAxis, paint);

        // Draw Target Time label.
        int topTargetTimeTitle = bottomTargetTimeAxis;
        int bottomTargetTimeTitle = topTargetTimeTitle + timeLabelsHeight;
        setPaintAttributes(paint, Color.BLACK, Paint.Style.FILL);
        drawRect(canvas, LEFT, topTargetTimeTitle, RIGHT, bottomTargetTimeTitle, paint);
        setPaintAttributes(paint, Color.WHITE, timeLabelsFontSize, Paint.Align.LEFT, Typeface.DEFAULT);
        canvas.drawText("Target Time", LEFT, bottomTargetTimeTitle-20, paint);

        // Draw Border around the entire view.
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topcurrentTimeTitle, RIGHT, bottomTargetTimeTitle, paint);

        // Draw the schedule on the phone.
        drawSleepSchedule(bedTime, wakeTime, targetBedTime, targetWakeTime, timeDiff);
    }

    private float convertSecToHourFloat(float seconds) {
        return seconds / NUM_SECS_PER_HOUR;
    }

    private int converSecToHourInt(float seconds) {
        return (int) (seconds / NUM_SECS_PER_HOUR);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style) {
        p.setColor(color);
        p.setStyle(style);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style, float strokeWidth) {
        setPaintAttributes(p, color, style);
        p.setStrokeWidth(strokeWidth);
    }

    private void setPaintAttributes(Paint p, int color, float textSize, Paint.Align textAlign, Typeface typeface) {
        p.setColor(color);
        p.setTextSize(textSize);
        p.setTextAlign(textAlign);
        p.setTypeface(typeface);
    }

    private void drawRect(Canvas c, int left, int top, int right, int bottom, Paint p) {
        rect.set(left, top, right, bottom);
        c.drawRect(left, top, right, bottom, p);
    }

    public void setSleepSchedule(float bedTime, float wakeTime, float targetBedTime,
                                 float targetWakeTime, float timeDiff) {
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.targetBedTime = targetBedTime;
        this.targetWakeTime = targetWakeTime;
        this.timeDiff = timeDiff;
        invalidate();
    }

    /**
     * Draw daylight cycle and sleep schedule. Assumes bedTime and wakeTime
     * given in seconds from midnight.
     */
    private void drawSleepSchedule(float bedTime, float wakeTime, float targetBedTime,
                                   float targetWakeTime, float timeDiff) {
        Paint black = new Paint();
        setPaintAttributes(black, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        Paint white = new Paint();
        setPaintAttributes(white, Color.WHITE, Paint.Style.STROKE, STROKE_WIDTH);
        float delta = graphWidth/((TERMINAL_TIME - INITIAL_TIME)*10);
        float x0 = LEFT;
        float y0 = daylightCycle(bedTime);
        // Graphs from Noon to Noon.
        float eps = (float) Math.pow(10.0, -3.0);
        for (float x1 = INITIAL_TIME; x1 < TERMINAL_TIME+1; x1+=0.10) {
            float y1 = daylightCycle((float) x1);
            if (y1 > verticalShift) {
                mCanvas.drawLine(x0, y0, x0+delta, y1, white);
            } else {
                mCanvas.drawLine(x0, y0, x0+delta, y1, black);
            }
            if (Math.abs(x1-Math.round(x1)) < eps) {
                drawAxisLabel(x0, (float) x1, timeDiff);
            }
            x0 += delta;
            y0 = y1;
        }

        // Draw shaded region for sleep times.
        drawSleepRegion(bedTime, wakeTime, Color.CYAN);
        drawSleepRegion(targetBedTime, targetWakeTime, Color.YELLOW);
    }

    /**
     * Draw axis label, current and target time.
     * @param x        x-coordinate to begin text
     * @param time     military time entry to add to axis, added to axis mod 12
     */
    private void drawAxisLabel(float x, float time, float timeDiff) {
        setPaintAttributes(paint, Color.BLACK, 20, Paint.Align.LEFT, Typeface.MONOSPACE);
        int currentHour = Math.round(time);
        int targetHour = Math.round(time + timeDiff);
        String currentLabel = null;
        String targetLabel = null;
        if (currentHour <= 12) {
            currentLabel = String.valueOf(currentHour); // + "am"
        } else {
            currentHour = (currentHour % 12) + 1;
            currentLabel = String.valueOf(currentHour); // + "pm"
        }
        if (targetHour <= 12) {
            targetLabel = String.valueOf(targetHour); // + "am"
        } else {
            targetHour = (targetHour % 12) + 1; // + "pm"
            targetLabel = String.valueOf(targetHour); // + "pm"
        }
        mCanvas.drawText(currentLabel, x, yCurrentTimeAxis-40, paint);
        mCanvas.drawText(targetLabel, x, yTargetTimeAxis - 40, paint);
    }

    /**
     * Draw sleep region on graph. Currently only supports startTime
     * in the after noon, and endTime before noon.
     * @param startTime   time input in seconds from midnight
     * @param endTime     time input in seconds from midnight
     * @param color       color of striped region
     */
    private void drawSleepRegion(float startTime, float endTime, int color) {
        float delta = graphWidth/((TERMINAL_TIME - INITIAL_TIME)*10);
        startTime = convertSecToHourFloat(startTime);   // assumes startTime after noon
        endTime = convertSecToHourFloat(endTime) + 24;  // assumes endTime before noon
        float left = INITIAL_TIME + ((startTime - INITIAL_TIME)/.10f)*delta;
        float right = INITIAL_TIME + ((endTime - INITIAL_TIME)/.10f)*delta;
        drawStripedRegion(left, verticalShift - amplitude, right, verticalShift + amplitude,
                2, 10, color, 2f);
    }

    /**
     * Draw striped region on graph. Note that Android device y-coordinates are
     * inverted, i.e. y increases as you move down the graph.
     * @param left         left border coordinate (x value)
     * @param top          top border coordinate (y value)
     * @param right        right border coordinate (x value)
     * @param bottom       bottom border coordinate (y value)
     * @param slope        slope of stripes, must be positive
     * @param numStripes   number of stripes
     * @param color        color of striped region; use Color class fields.
     * @param stripeWidth  width of each stripe
     */
    private void drawStripedRegion(float left, float top, float right, float bottom,
                                   float slope, int numStripes, int color, float stripeWidth) {
        // Draw region border.
        setPaintAttributes(paint, color, Paint.Style.STROKE, stripeWidth*1.5f);
        mCanvas.drawLine(left, top, left, bottom, paint);     // Left border
        mCanvas.drawLine(left, top, right, top, paint);       // Top border
        mCanvas.drawLine(right, top, right, bottom, paint);   // Right border
        mCanvas.drawLine(left, bottom, right, bottom, paint); // Bottom border

        // Draw stripes.
        setPaintAttributes(paint, color, Paint.Style.STROKE, stripeWidth);
        float yDelta = (bottom - top) / numStripes;
        float y0 = 0f;
        float y1 = 0f;
        for (y0 = top; y1 <= bottom; y0 += yDelta) {
            y1 = -1*(right-left)*slope + y0;
            if (y1 < top && y0 > bottom) {
                // Cut line off at both top and bottom of graph.
                float x0 = left + (y0-bottom)/slope;
                float x1 = left + (y0-top)/slope;
                if (x0 <= right && x1 <= right) {
                    mCanvas.drawLine(x0, bottom, x1, top, paint);
                }
            } else if (y1 < top) {
                // Cut line off at top of graph.
                float x1 = left + (y0-top)/slope;
                if (x1 <= right) {
                    Log.d("y1:", String.valueOf(y1));
                    mCanvas.drawLine(left, y0, x1, top, paint);
                }
            } else if (y0 > bottom) {
                // Cut line off at bottom of graph.
                float x0 = left + (y0-bottom)/slope;
                if (x0 <= right) {
                    mCanvas.drawLine(x0, bottom, right, y1, paint);
                }
            } else {
                mCanvas.drawLine(left, y0, right, y1, paint);
            }
        }
    }

    /**
     * Daylight cycle sine funciton. Assumes input in military time,
     * sunrise at 7am (7:00), and sunset at 7pm (19:00).
     *
     * (x,y) coordinates reflected across the x-axis based on Android screen coordinates.
     */
    private float daylightCycle(float hour) {
        return (float) -1*amplitude * (float) Math.sin((Math.PI / (float) 12)*(hour - 7)) + verticalShift;
    }
}