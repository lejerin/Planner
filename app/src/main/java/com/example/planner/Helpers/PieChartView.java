package com.example.planner.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

//직접 원그래프 그리는 뷰, 사용 x

public class PieChartView extends View {
    private Paint slicePaint;
    private int[] sliceClrs = { Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW };
    private RectF rectf; // Our box
    private ArrayList<Float> datapoints; // Our values

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        slicePaint = new Paint();
        slicePaint.setAntiAlias(true);
        slicePaint.setDither(true);
        slicePaint.setStyle(Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.datapoints != null) {
            int startTop = 0;
            int startLeft = 0;
            int endBottom = getWidth();
            int endRight = endBottom; // To make this an equal square
            // Create the box
            rectf = new RectF(startLeft, startTop, endRight, endBottom); // Creating the box

            float[] scaledValues = scale(); // Get the scaled values
            float sliceStartPoint = -90;

//
//
//
//            int centerX = (int) ((rectf.left + rectf.right) / 2);
//            int centerY = (int) ((rectf.top + rectf.bottom) / 2);
//            int radius = (int) ((rectf.right - rectf.left) / 2);
//
//            radius *= 0.5; // 1 will put the text in the border, 0 will put the text in the center. Play with this to set the distance of your text.
//



            for (int i = 0; i < scaledValues.length; i++) {
                slicePaint.setColor(sliceClrs[i]);
                canvas.drawArc(rectf, sliceStartPoint, scaledValues[i], true, slicePaint); // Draw slice

                slicePaint.setColor(Color.WHITE); // set this to the text color.
                float medianAngle = ( datapoints.get(i) + (sliceStartPoint / 2f)) * (float)Math.PI / 180f; // this angle will place the text in the center of the arc.
//                slicePaint.setTextSize(40);
//                slicePaint.setTextAlign(Paint.Align.CENTER);
//                canvas.drawText("gg", (float)(centerX + (radius * Math.cos(medianAngle))), (float)(centerY + (radius * Math.sin(medianAngle))), slicePaint);
//


                sliceStartPoint += scaledValues[i]; // Update starting point of the next slice
            }
        }
    }

    public void setDataPoints(ArrayList<Float> datapoints) {
        this.datapoints = datapoints;
        invalidate(); // Tells the chart to redraw itself
    }

    private float[] scale() {
        float[] scaledValues = new float[this.datapoints.size()];
        float total = getTotal(); // Total all values supplied to the chart
        for (int i = 0; i < this.datapoints.size(); i++) {
            scaledValues[i] = (this.datapoints.get(i) / total) * 360; // Scale each value
        }
        return scaledValues;
    }

    private float getTotal() {
        float total = 0;
        for (float val : this.datapoints)
            total += val;
        return total;
    }
}