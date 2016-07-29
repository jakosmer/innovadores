package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

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
        color.setTextSize(20);
        color.setColor(Color.BLACK);

        int i = 0;

        AsyncAnimator animator = new AsyncAnimator();
        animator.execute(i);
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
                Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

                Canvas canvas = new Canvas(bmp);

                canvas.drawBitmap(BitmapFactory.decodeResource(res, R.drawable.pikachu), 0, 0, color);
                canvas.drawText("Contador " + String.valueOf(i), 30, 40, color);

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


