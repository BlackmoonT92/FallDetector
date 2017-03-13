package edu.asu.msama1.falldetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

/**
 * GraphView creates a scaled line or bar graph with x and y axis labels.
 * @author Arno den Hond
 *
 */
public class GraphView extends View {

    private static String TAG = "GraphView";

    public static boolean BAR = false;
    public static boolean LINE = true;

    private Paint paint;
    private float[] normA, normG;
    private String[] horlabels;
    private String[] verlabels;
    private String title;
    private boolean type;

    private final float MIN_A = 0, MAX_A = 100;
    private final float MIN_G = 0, MAX_G = 100;

    public GraphView(Context context, float[] normA, float[] normG, String title, String[] horlabels, String[] verlabels, boolean type) {
        super(context);
        if (normA == null)
            normA = new float[0];
        else
            this.normA = normA;
        if (normG == null)
            normG = new float[0];
        else
            this.normG = normG;
        if (title == null)
            title = "";
        else
            this.title = title;
        if (horlabels == null)
            this.horlabels = new String[0];
        else
            this.horlabels = horlabels;
        if (verlabels == null)
            this.verlabels = new String[0];
        else
            this.verlabels = verlabels;
        this.type = type;
        paint = new Paint();
    }

    public void setValues(float[] normA, float[] normG)
    {
        this.normA = normA;
        this.normG = normG;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "Inside Draw");

        float border = 35;
        float horstart = border * 2;
        float height = getHeight();
        float width = getWidth() - 1;
        float graphheight = height - (2 * border);
        float graphwidth = width - (2 * border);


        paint.setTextAlign(Align.LEFT);
        int vers = verlabels.length - 1;
        for (int i = 0; i < verlabels.length; i++) {
            paint.setColor(Color.DKGRAY);
            float y = ((graphheight / vers) * i) + border;
            canvas.drawLine(horstart, y, width, y, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(verlabels[i], 0, y, paint);
        }
        int hors = horlabels.length - 1;
        for (int i = 0; i < horlabels.length; i++) {
            paint.setColor(Color.DKGRAY);
            float x = ((graphwidth / hors) * i) + horstart;
            canvas.drawLine(x, height - border, x, border, paint);
            paint.setTextAlign(Align.CENTER);
            if (i==horlabels.length-1)
                paint.setTextAlign(Align.RIGHT);
            if (i==0)
                paint.setTextAlign(Align.LEFT);
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText(horlabels[i], x, height - 4, paint);
        }

        paint.setTextAlign(Align.CENTER);
        canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

        paint.setColor(Color.LTGRAY);
        float datalength = normA.length;
        float colwidth = (width - (2 * border)) / datalength;
        float halfcol = colwidth / 2;
        float lasth = 0;
        plotValue(canvas, border, horstart, MIN_A, MAX_A, graphheight, colwidth, halfcol, lasth, normA, Color.GREEN);

        paint.setColor(Color.LTGRAY);
        datalength = normG.length;
        colwidth = (width - (2 * border)) / datalength;
        halfcol = colwidth / 2;
        lasth = 0;
        plotValue(canvas, border, horstart, MIN_G, MAX_G, graphheight, colwidth, halfcol, lasth, normG, Color.RED);

    }

    private void plotValue(Canvas canvas, float border, float horstart, float min, float diff, float graphheight, float colwidth, float halfcol, float lasth, float[] values, int color) {
        for (int i = 0; i < values.length; i++) {
            float val = values[i] - min;
            float rat = val / diff;
            float h = graphheight * rat;
            //if (i > 0)
            paint.setColor(color);
            paint.setStrokeWidth(2.0f);

            canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
            lasth = h;
        }
    }

}