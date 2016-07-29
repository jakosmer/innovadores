package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MarkerManager {

    private GoogleMap mapa;
    private Resources resources;
    private String paquete;

    public MarkerManager(GoogleMap mMap, Resources res, String paquete){
        this.mapa = mMap;
        this.resources = res;
        this.paquete = paquete;
    }

    public Marker addMarkerGeneric(LatLng position){

        Marker marker = mapa.addMarker(new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_p2_48x48))
                .draggable(true));

        return  marker;
    }

    public void addMarkerPokemon(LatLng position, String namePokemon){

        int pokemon = R.drawable.p_3;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

        Paint color = new Paint();
        color.setTextSize(30);
        color.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bmp);

        //canvas.drawBitmap(BitmapFactory.decodeResource(this.resources, pokemon ), 0,0, color);

        MarkerOptions options = new MarkerOptions().position(position).title("Nombre Poke").icon(BitmapDescriptorFactory.fromResource(R.drawable.pikachu));
        Marker marker =  mapa.addMarker(options);

        //int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        //LinkedBlockingQueue cola = new LinkedBlockingQueue();
        //ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,4,60, TimeUnit.SECONDS,cola);



        MarkerCounter counter = new MarkerCounter(marker, this.resources, bmp, canvas);
        counter.startCounter();
    }

    public void addMarkerGym(LatLng position, String team){

        try {
            team = team.toLowerCase();
            String name = "battle_arena_"+team+"_80";
            //name = "marker96x96.png";

            int id = resources.getIdentifier(name, "drawable", this.paquete);
            mapa.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(id)));

        }catch (Exception e){
            Log.e("addMarkerGym",e.getMessage());
        }
    }
}
