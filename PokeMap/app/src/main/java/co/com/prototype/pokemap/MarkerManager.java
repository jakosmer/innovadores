package co.com.prototype.pokemap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import co.com.prototype.pokemap.Model.Beans.GymPosition;
import co.com.prototype.pokemap.Model.Beans.PokeStopPosition;
import co.com.prototype.pokemap.Model.Beans.PokemonPosition;


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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_p2_64x64))
                .draggable(true));

        return  marker;
    }

    public void addMarkerPokemon(PokemonPosition pokemonPosition){

        String idPoke = String.valueOf(pokemonPosition.getId());
        String icoName = "p_00"+idPoke;

        int id = resources.getIdentifier(icoName, "drawable", this.paquete);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

        Paint color = new Paint();
        color.setTextSize(30);
        color.setColor(Color.BLACK);

        Canvas canvas = new Canvas(bmp);

        MarkerOptions options = new MarkerOptions().position(pokemonPosition.getPosition().convertToLatLng()).title(pokemonPosition.getName()).icon(BitmapDescriptorFactory.fromResource(id));
        Marker marker =  mapa.addMarker(options);

        MarkerCounter counter = new MarkerCounter(marker, this.resources, bmp, canvas);
        counter.startCounter(Long.parseLong(pokemonPosition.getTimeToHide()));
    }

    public void addMarkerGym(GymPosition gymPosition, String team){
        try {
            team = team.toLowerCase();
            String name = "battle_arena_"+team+"_80";

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
