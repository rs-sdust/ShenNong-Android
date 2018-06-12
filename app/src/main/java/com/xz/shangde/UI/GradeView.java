package com.xz.shangde.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxz
 * 绘制等级图（图例），显示在fragment200中
 */

public class GradeView extends View {

    private LinkedHashMap<Integer, Object[]> map;

    private int width;
    private int height;

    private int parts;
    private int part_length;
    private int padding=100;
    private int bottom_padding=20;

    private int line_width=20;

    public GradeView(Context context, LinkedHashMap<Integer, Object[]> map) {
        super(context);
        this.map=map;
    }

    public GradeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GradeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

        parts=map.size();
        part_length=(int)((width-2*padding)/parts);

        Paint paint_line=new Paint();
        paint_line.setStrokeWidth(line_width);

        Paint paint_text=new Paint();
        paint_text.setTextSize((float)((height-bottom_padding-line_width)/2));
        paint_text.setColor(Color.WHITE);

        for (int i=0;i<parts;i++){
            int real=i+1;
            Object[] objects=map.get(real);
            paint_line.setColor((Integer) objects[0]);
            String s= (String) objects[1];

            int line_start_x=padding+part_length*i;
            int line_end_x=line_start_x+part_length;
            int line_y=(int)(height-bottom_padding-line_width/2);
            canvas.drawLine(line_start_x,line_y,line_end_x,line_y,paint_line);

            int baseline_x=padding+part_length*i+20;
            int baseline_y=height-bottom_padding-line_width-20;
            canvas.drawText(s,baseline_x,baseline_y,paint_text);
        }


    }
}
