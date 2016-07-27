package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzf;

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

        while(true) {
            i++;
            AsyncAnimator animator = new AsyncAnimator(marker, canvas);
            animator.execute(i);
            if(i > 20){
                //destroy();
                break;
            }
        }
    }

    public void destroy(){
        marker.remove();
    }

    public class AsyncAnimator extends AsyncTask<Integer, Void, Bitmap> {

        private Marker marker;
        private Canvas canvas;


        public AsyncAnimator(Marker marker, Canvas canvas){
            this.marker = marker;
            this.canvas = canvas;
        }


        @Override
        protected Bitmap doInBackground(Integer... params) {
            try {
                Thread.sleep(1000);
            }catch (Exception e){
            }
            this.canvas.drawText("Contador " + String.valueOf(params[0]), 30, 40, color);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            this.marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
        }
    }

}


