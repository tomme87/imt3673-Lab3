package no.ntnu.tomme87.imt3673.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Tomme on 08.03.2018.
 */

public class GameDrawableView extends View {
    private ShapeDrawable shapeDrawable;
    private ShapeDrawable shapeDrawable2;

    public GameDrawableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        int x = 100;
        int y = 500;
        int width = 200;
        int height = 200;

        shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(Color.BLACK);
        shapeDrawable.setBounds(x, y, x+width, y+height);

        shapeDrawable2 = new ShapeDrawable(new RectShape());
        shapeDrawable2.getPaint().setColor(Color.BLACK);
        shapeDrawable2.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable2.getPaint().setStrokeWidth(10);
        shapeDrawable2.setBounds(x, y, x+width, y+height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = (int) (getWidth() * 0.025);
        int height = width;

        int x = getWidth() / 2-(width/2);
        int y = getHeight() / 2-(width/2);

        Log.d("game", "x="+x+":y="+y+":bwidth="+width+":bheight="+height+":theight"+getHeight()+"twidth:"+getWidth());

        shapeDrawable.setBounds(x, y, width+x, height+y);
        //shapeDrawable.setBounds(getWidth() / 2, getHeight(), getWidth() / 2, width);
        shapeDrawable.draw(canvas);


        shapeDrawable2.setBounds(10, 10, getWidth() -10, getHeight()-10);
        shapeDrawable2.draw(canvas);
    }
}
