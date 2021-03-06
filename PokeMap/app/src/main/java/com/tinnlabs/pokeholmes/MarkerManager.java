package com.tinnlabs.pokeholmes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TimeUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.tinnlabs.pokeholmes.Model.Beans.GymPosition;
import com.tinnlabs.pokeholmes.Model.Beans.PokeStopPosition;
import com.tinnlabs.pokeholmes.Model.Beans.PokemonPosition;


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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_p2_32x32))
                .draggable(true));

        return  marker;
    }

    public Circle addCircle (LatLng position, double radio){
        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .radius(radio)
                .fillColor(Color.argb(30, 84, 162, 208))
                .strokeWidth(1).strokeColor(Color.argb(150, 84, 162, 208));
        Circle circle = mapa.addCircle(circleOptions);
        return  circle;
    }

    public void addMarkerPokemon(PokemonPosition pokemonPosition){

        MarkerOptions options = new MarkerOptions().position(pokemonPosition.getPosition().convertToLatLng()).title(pokemonPosition.getName());

        if(MarkerCounter.isActiveMarker(options)){
            return;
        }

        String idPoke = "00"+String.valueOf(pokemonPosition.getId());
        idPoke = idPoke.substring(idPoke.length()-3,idPoke.length());
        String icoName = "p_"+idPoke;
        int id = resources.getIdentifier(icoName, "drawable", this.paquete);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

        Paint color = new Paint();

        color.setTextSize(resources.getDimensionPixelSize(R.dimen.text_counter));
        color.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bmp);

        options.icon(BitmapDescriptorFactory.fromResource(id));
        Marker marker =  mapa.addMarker(options);

        MarkerCounter counter = new MarkerCounter(marker, this.resources, bmp, canvas, id);

        long timeToHide = Long.parseLong(pokemonPosition.getTimeToHide());
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        long timeDifSec = (timeToHide-currentTime);

        counter.startCounter(timeDifSec);
    }

    public void addMarkerGym(GymPosition gymPosition, String team){
        try {
            team = team.toLowerCase();
            String name = "battle_arena_"+team+"_40";

            int id = resources.getIdentifier(name, "drawable", this.paquete);
            mapa.addMarker(new MarkerOptions().position(gymPosition.getPosition().convertToLatLng()).icon(BitmapDescriptorFactory.fromResource(id)));

        }catch (Exception e){
            Log.e("addMarkerGym",e.getMessage());
        }
    }

    public void addMarkerStop(PokeStopPosition pokeStopPosition){
        try {
//            team = team.toLowerCase();
//            String name = "battle_arena_"+team+"_80";
//            int id = resources.getIdentifier(name, "drawable", this.paquete);
            mapa.addMarker(new MarkerOptions().position(pokeStopPosition.getPosition().convertToLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop)));

        }catch (Exception e){
            Log.e("addMarkerGym",e.getMessage());
        }
    }
}
