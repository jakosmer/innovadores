package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v4.util.Pools;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MarkerCounter {

    private Marker marker;
    private Bitmap bmp;
    protected Paint color;
    private Resources res;
    private Canvas canvas;
    private int idPoke;


    public MarkerCounter(Marker  marker, Resources res, Bitmap bmp, Canvas canvas, int idPoke) {
        this.marker = marker;
        this.bmp = bmp;
        this.res = res;
        this.canvas = canvas;
        this.idPoke = idPoke;
    }

    public void startCounter(long timeToHide) {
        color = new Paint();
        color.setTextSize(12);
        color.setTextAlign(Paint.Align.CENTER);
        color.setColor(Color.BLACK);

        AsyncAnimator animator = new AsyncAnimator();
        animator.executeOnExecutor(PoolManager.getInstance().getExecutor(), timeToHide);

    }

    public void destroyMarker(){
        marker.remove();
        marker = null;
    }

    public class AsyncAnimator extends AsyncTask<Long, Bitmap, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            Looper.prepare();
            new CountDownTimer(params[0], 1000) {

                public void onTick(long millisUntilFinished) {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    Bitmap bmp = Bitmap.createBitmap(80, 90, conf);

                    Canvas canvas = new Canvas(bmp);

                    Paint paint = new Paint();
                    paint.setColor(Color.argb(50,43,223,243));
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        canvas.drawRoundRect(5,0,55,25,10,10,paint);
                    }else
                        canvas.drawRect(5,0,55,25,paint);
                    canvas.drawBitmap(BitmapFactory.decodeResource(res, idPoke), 0, 15, color);
                    canvas.drawText(timeCalculate(millisUntilFinished), 30, 15, color);

                    publishProgress(bmp);
                }

                public void onFinish() {

                    Looper.myLooper().quit();

                }
            }.start();
            Looper.loop();

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

        private String timeCalculate(long millisUntilFinished) {

            long days, hours, minutes, seconds;
            String restT = "";

            hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
            minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished));
            seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));

            restT = String.format("%02d:%02d", minutes, seconds);
            //restT = String.format("%02d:%02d:%02d", hours, minutes, seconds);


            return restT;
        }
    }

}


