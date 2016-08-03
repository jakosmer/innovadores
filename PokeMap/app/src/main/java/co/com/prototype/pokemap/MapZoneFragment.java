package co.com.prototype.pokemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;
import co.com.prototype.pokemap.Model.Beans.Position;
import co.com.prototype.pokemap.Model.Services.ApiFactoryClient;
import co.com.prototype.pokemap.Model.Services.IApiContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapZoneFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_zone, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        GpsLocation gpsLocation = new GpsLocation(getActivity().getApplicationContext());

        final Marker[] myPosition = {markerManager.addMarkerGeneric(loc)};

        getPositions(markerManager);

//        for (int i = 1; i < 10; i++) {
//            LatLng pos = generateRadomGpsLocation(loc);
//            Position position = new Position(pos.latitude,pos.longitude);
//            Random random = new Random();
//            PokemonPosition pokemonPosition = new PokemonPosition
//                    (i,"p_00"+i, String.valueOf(SystemClock.currentThreadTimeMillis() +
//                            ((random.nextInt(600 - 100) + 100) * 1000)),position);
//            markerManager.addMarkerPokemon(pokemonPosition);
//        }

        /*CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(6.26718156, -75.58027267))
                .radius(300)
                .fillColor(Color.argb(150, 84, 162, 208))
                .strokeWidth(1).strokeColor(Color.argb(150, 84, 162, 208));
        Circle circle = mMap.addCircle(circleOptions);*/

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 3));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12)
            , 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                }

                @Override
                public void onCancel() {

                }
            });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (myPosition[0] != null) {
                    myPosition[0].remove();
                }
                myPosition[0] = markerManager.addMarkerGeneric(latLng);
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                GpsLocation gpsLocation = new GpsLocation(getActivity().getApplicationContext());
                LatLng loc = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
                if (myPosition[0] != null) {
                    myPosition[0].remove();
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                myPosition[0] = markerManager.addMarkerGeneric(loc);

                return true;
            }
        });
    }

    private LatLng generateRadomGpsLocation(LatLng actualPos) {
        double y0 = actualPos.latitude;
        double x0 = actualPos.longitude;
        double radio = 2000f / 111300;

        double seedRadomA = Math.random();
        double seedRadomB = Math.random();

        double w = radio * Math.sqrt(seedRadomA);
        double t = 2 * Math.PI * seedRadomB;

        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        return new LatLng((y + y0), (x + x0));
    }

    private void getPositions(MarkerManager markerM){
        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", "");
        params.put("width", 9);
        params.put("position", new Position(6.254010, -75.578931));
        Call<List<PokemonPosition>> caller = endPoints.getPokemonPositions(params);

        caller.enqueue(new Callback<List<PokemonPosition>>() {
            @Override
            public void onResponse(Call<List<PokemonPosition>> call, Response<List<PokemonPosition>> response) {
                List<PokemonPosition> pos = response.body();

                for (PokemonPosition pokemonPosition: pos){
                    markerM.addMarkerPokemon(pokemonPosition);
                }
            }

            @Override
            public void onFailure(Call<List<PokemonPosition>> call, Throwable t) {
                Log.e("PKMERROR", "Error llamando servicio", t);
            }
        });
    }
}