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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tomme on 08.03.2018.
 */

public class GameDrawableView extends View implements SensorEventListener, CollisionListener {
    private static final String TAG = "GameDrawView";

    protected static final int STROKE_WIDTH = 10;
    protected static final int RECT_PADDING = 10;

    private Ball ball;
    private ShapeDrawable border;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Vibrator vibrator;
    private ToneGenerator toneGenerator;

    private Timer timer;

    public GameDrawableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.setupSensors();
        this.setupFeedback();
        this.setupBall();
        this.setupBorder();

        // Update view every 20ms
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 0, 20);
    }

    /**
     * Setup the rotation sensor and listen on updates
     */
    private void setupSensors() {
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Setup vibrator and sound feedback
     */
    private void setupFeedback() {
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
    }

    /**
     * Setup the ball
     */
    private void setupBall() {
        ball = new Ball(this);
    }

    /**
     * Setup the border
     */
    private void setupBorder() {
        border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(Color.BLACK);
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setStrokeWidth(STROKE_WIDTH);
    }

    /**
     * Here we got width and height so we can initialize the ball
     *
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.ball.initialize(getWidth(), getHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // The ball
        ball.setBounds();
        ball.draw(canvas);

        // The border
        border.setBounds(RECT_PADDING, RECT_PADDING, getWidth() - RECT_PADDING, getHeight() - RECT_PADDING);
        border.draw(canvas);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
            return;
        }

        // Get orientation from rotation vector
        final float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        final float[] orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // Move the ball
        ball.move(orientationAngles);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Vibrate and beep when we collide
     */
    @Override
    public void onCollision() {
        vibrator.vibrate(100);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
    }

    /**
     * Cancel timer and unregister listener.
     */
    public void stop() {
        sensorManager.unregisterListener(this);
        timer.cancel();
        timer.purge();
    }
}
