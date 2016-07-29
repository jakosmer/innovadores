package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.Pools;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MarkerCounter {

    private Marker marker;
    private Bitmap bmp;
    protected Paint color;
    private Resources res;
    private Canvas canvas;


    public MarkerCounter(Marker  marker, Resources res, Bitmap bmp, Canvas canvas) {
        this.marker = marker;
        this.bmp = bmp;
        this.res = res;
        this.canvas = canvas;
    }

    public void startCounter() {
        color = new Paint();
        color.setTextSize(15);
        color.setFakeBoldText(true);
        color.setTextAlign(Paint.Align.CENTER);
        color.setColor(Color.BLACK);

        int i = 0;

        AsyncAnimator animator = new AsyncAnimator();
        animator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);

    }

    public void destroyMarker(){
        marker.remove();
        marker = null;
    }

    public class AsyncAnimator extends AsyncTask<Integer, Bitmap, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int i = params[0];

            while(true) {
                i++;

                if(i > 20){
                    break;
                }

                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(80, 90, conf);

                Canvas canvas = new Canvas(bmp);

                Paint paint = new Paint();
                paint.setColor(Color.argb(50,43,223,243));
                paint.setStyle(Paint.Style.FILL);


                canvas.drawRect(15,70,55,90,paint);

                canvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.marker_p2_64x64), 0, 0, color);
                canvas.drawText(String.valueOf(i), 30, 85, color);

                publishProgress(bmp);

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);

            if(marker != null) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(values[0]));
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            destroyMarker();
        }
    }

}


