package no.ntnu.tomme87.imt3673.lab3;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tomme on 11.03.2018.
 */

public class Ball extends ShapeDrawable {
    private static final String TAG = "Ball";
    private static final float BALL_SIZE = 0.05f;
    private static final int SPEED = 10;

    private CollisionListener collisionListener;

    private int width;
    private int height;
    private int x;
    private int y;
    private int maxX;
    private int maxY;
    private int minXY;

    private boolean started;
    private boolean colliding;

    /**
     * Black ball
     */
    public Ball(CollisionListener collisionListener) {
        super(new OvalShape());
        this.getPaint().setColor(Color.BLACK);

        this.collisionListener = collisionListener;

        this.started = false;
    }

    /**
     * Initialize the ball with position in center of the screen
     *
     * @param totalWidth of screen
     * @param totalHeight of screen
     */
    public void initialize(final int totalWidth, final int totalHeight) {
        // Width of the ball based on screen size
        width = (int) (totalWidth * BALL_SIZE);
        height = width;


        // Center of the screen
        x = totalWidth / 2-(width/2);
        y = totalHeight / 2-(width/2);

        // Minimym X and Y values is the size of paddings
        minXY = GameDrawableView.STROKE_WIDTH + GameDrawableView.RECT_PADDING - 5;

        // Maximum X and Y values is the total length - width of ball and padding
        maxX = totalWidth - width-minXY;
        maxY = totalHeight - width-minXY;

        Log.d(TAG, "initialized. x="+x+":y="+y+":maxX="+maxX+":maxY="+maxY+":minXY="+minXY);
        this.started = true;
    }

    public void setBounds() {
        this.setBounds(x, y, width+x, height+y);
    }

    /**
     * Move ball based on orientation and bounce on collision
     *
     * @param orientationAngles
     */
    public void move(final float[] orientationAngles) {
        if(!this.started) {
            return;
        }

        int xForce = (int) (orientationAngles[1] * SPEED);
        int yForce = (int) (orientationAngles[2] * SPEED);

        x -= xForce;
        y -= yForce;
        this.colided(xForce, yForce);
    }

    /**
     * Collide if collided
     */
    private void colided(int xForce, int yForce) {
        if(x > maxX) {
            x = maxX;
            collide(AnimateBounce.LEFT_OR_RIGHT, xForce, yForce);
        } else if(x < minXY) {
            x = minXY;
            collide(AnimateBounce.LEFT_OR_RIGHT, xForce, yForce);
        }

        if(y > maxY) {
            y = maxY;
            collide(AnimateBounce.TOP_OR_BOTTOM, xForce, yForce);
        } else if(y < minXY) {
            y = minXY;
            collide(AnimateBounce.TOP_OR_BOTTOM, xForce, yForce);
        }
    }

    /**
     * We have collided. bounce.
     *
     * @param type
     * @param xForce
     * @param yForce
     */
    private void collide(int type, int xForce, int yForce) {
        if(colliding) {
           return;
        }
        colliding = true;
        Timer timer = new Timer();
        timer.schedule(new AnimateBounce(40, type, xForce, yForce), 0, 10);
        collisionListener.onCollision();
    }

    /**
     * To animate the bounce we move the ball X times with force.
     */
    class AnimateBounce extends TimerTask {
        private static final int LEFT_OR_RIGHT = 1;
        private static final int TOP_OR_BOTTOM = 2;

        int times;
        int type;
        int xForce;
        int yForce;

        AnimateBounce(int times, int type, int xForce, int yForce) {
            this.times = times;
            this.type = type;
            this.xForce = xForce;
            this.yForce = yForce;
        }

        @Override
        public void run() {
            this.times--;
            switch (type) {
                case LEFT_OR_RIGHT:
                    x += this.xForce*2;
                    y += this.yForce * -1;
                    break;
                case TOP_OR_BOTTOM:
                    y += this.yForce*2;
                    x += this.xForce * -1;
                    break;
            }
            if(times <= 0) {
                colliding = false;
                this.cancel();
            }
        }
    }
}
