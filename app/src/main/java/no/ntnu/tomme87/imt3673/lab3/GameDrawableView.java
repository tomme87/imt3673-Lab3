package no.ntnu.tomme87.imt3673.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tomme on 08.03.2018.
 */

public class GameDrawableView extends View implements SensorEventListener {
    private static final String TAG = "GameDrawView";

    private static final int STROKE_WIDTH = 10;
    private static final int RECT_PADDING = 10;
    private static final int SPEED = 20;
    private static final float BALL_SIZE = 0.025f;

    private ShapeDrawable ball;
    private ShapeDrawable border;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Vibrator vibrator;

    private int collide = -1;


    // Ball values
    int width;
    int height;
    int x;
    int y;
    int maxX;
    int maxY;
    int minXY;

    public GameDrawableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        ball = new ShapeDrawable(new OvalShape());
        ball.getPaint().setColor(Color.BLACK);

        border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(Color.BLACK);
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        width = (int) (getWidth() * BALL_SIZE);
        height = width;

        // Center of the screen
        x = getWidth() / 2-(width/2);
        y = getHeight() / 2-(width/2);

        minXY = STROKE_WIDTH + RECT_PADDING - 5;

        maxX = getWidth() - width-minXY;
        maxY = getHeight() - width-minXY;

        Log.d(TAG, "maxY:"+maxY);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // The ball
        ball.setBounds(x, y, width+x, height+y);
        ball.draw(canvas);

        // The border
        border.setBounds(RECT_PADDING, RECT_PADDING, getWidth() -RECT_PADDING, getHeight()-RECT_PADDING);
        border.draw(canvas);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
            return;
        }

        final float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        final float[] orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        //Log.d(TAG, "0="+orientationAngles[0]+":1="+orientationAngles[1]+":2="+orientationAngles[2]);

        int newX = x - (int) (orientationAngles[1] * SPEED);
        int newY = y - (int) (orientationAngles[2] * SPEED);

        collide = -1;
        if (newX > maxX) {
            newX = maxX;
            collide = AnimateBounce.RIGHT_TO_LEFT;
        } else if(newX < minXY) {
            newX = minXY;
            collide = AnimateBounce.LEFT_TO_RIGHT;
        }
        if(newY > maxY) {
            newY = maxY;
            collide = AnimateBounce.TOP_TO_BOTTOM;
        } else if(newY < minXY) {
            newY = minXY;
            collide = AnimateBounce.BOTTOM_TO_TOP;
        }


        if(newX == x && newY == y) {
            collide = -1;
            return;
        }

        x = newX;
        y = newY;

        if(collide > -1) {
            colision(collide);
            return;
        }



        Log.d(TAG, "X = " + x + " : Y = " +y);
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void colision(int type) {
        vibrator.vibrate(100);
        Timer timer = new Timer();
        timer.schedule(new AnimateBounce(30, type), 0, 20);
    }

    class AnimateBounce extends TimerTask {
        private static final int LEFT_TO_RIGHT = 1;
        private static final int RIGHT_TO_LEFT = 2;
        private static final int TOP_TO_BOTTOM = 3;
        private static final int BOTTOM_TO_TOP = 4;

        int times;
        int type;

        AnimateBounce(int times, int type) {
            this.times = times;
            this.type = type;
        }

        @Override
        public void run() {
            this.times--;
            Log.d(TAG, "type = " + this.type);
            switch (type) {
                case LEFT_TO_RIGHT:
                    x += 20;
                    break;
                case RIGHT_TO_LEFT:
                    x -= 20;
                    break;
                case TOP_TO_BOTTOM:
                    y -= 20;
                    break;
                case BOTTOM_TO_TOP:
                    y += 20;
                    break;
            }
            Log.d(TAG, "times: " + this.times);
            postInvalidate();
            if(times <= 0) {
                collide = -1;
                this.cancel();
            }
        }
    }
}
