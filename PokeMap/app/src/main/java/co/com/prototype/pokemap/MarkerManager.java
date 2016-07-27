package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MarkerManager {

    private GoogleMap mapa;
    private Resources resources;

    public MarkerManager(GoogleMap mMap, Resources res){
        this.mapa = mMap;
        this.resources = res;
    }

    public void addMarker(LatLng position, String namePokemon){

        int pokemon = R.drawable.p_3;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

        Paint color = new Paint();
        color.setTextSize(30);
        color.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(BitmapFactory.decodeResource(this.resources, pokemon ), 0,0, color);

        MarkerOptions options = new MarkerOptions().position(position).title("Nombre Poke").icon(BitmapDescriptorFactory.fromBitmap(bmp)).anchor(0.5f, 1);
        Marker marker =  mapa.addMarker(options);

        MarkerCounter counter = new MarkerCounter(marker, this.resources, bmp, canvas);
        counter.startCounter();
    }

}
