package com.xz.shangde.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by yxq on 2018/6/5.
 */

public class DateView extends View {

    private int width;
    private int height;

    private int Date;
    private int Durantion;

    public DateView(Context context,int date_till_now,int duration) {
        super(context);
        Date=date_till_now;
        Durantion=duration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;
        height = heightSize;
        Log.i("onMeasure","width:"+width);
        Log.i("onMeasure","height:"+height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int padding= (int) (width*0.1);
        int height_center=(int)(height/2);

        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setColorFilter(null);

        canvas.drawLine(padding,height_center,width-padding,height_center,paint);
    }
}
