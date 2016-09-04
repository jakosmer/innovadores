package com.tinnlabs.pokeholmes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

import com.tinnlabs.pokeholmes.Model.Beans.PokemonPosition;
import com.tinnlabs.pokeholmes.Model.Beans.Position;
import com.tinnlabs.pokeholmes.Model.Services.ApiFactoryClient;
import com.tinnlabs.pokeholmes.Model.Services.IApiContract;
import com.tinnlabs.pokeholmes.Security.PokeCredential;
import com.tinnlabs.pokeholmes.Security.PokeSecurity;
import com.tinnlabs.pokeholmes.Utils.ApiEndPointsBodyGenerator;
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
        LatLng loc = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
        MarkerManager markerManager = new MarkerManager(mMap, getResources(), this.getActivity().getPackageName());
        final Marker[] myPosition = {markerManager.addMarkerGeneric(loc)};

        if (gpsLocation.validarGPS()){
            TaskAnimation taskAnimation = new TaskAnimation(markerManager, loc);
            taskAnimation.execute();

//            CircleOptions circleOptions = new CircleOptions()
//                    .center(loc)
//                    .radius(300)
//                    .fillColor(Color.argb(150, 84, 162, 208))
//                    .strokeWidth(1).strokeColor(Color.argb(150, 84, 162, 208));
//            Circle circle = mMap.addCircle(circleOptions);
            markerManager.addCircle(loc,100);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 5));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18)
                    , 1000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18));
        }

        mMap.setOnMapClickListener(latLng -> {
            if (myPosition[0] != null) {
                myPosition[0].remove();
            }
            myPosition[0] = markerManager.addMarkerGeneric(latLng);
        });

        mMap.setOnMyLocationButtonClickListener(() -> {
            GpsLocation gpsLocation1 = new GpsLocation(getActivity().getApplicationContext());
            LatLng loc1 = new LatLng(gpsLocation1.getLatitud(), gpsLocation1.getLongitud());
            if (myPosition[0] != null) {
                myPosition[0].remove();
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 18));
            myPosition[0] = markerManager.addMarkerGeneric(loc1);

            return true;
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

    private void getPositions(MarkerManager markerM, ProgressDialog dialog, LatLng loc){

        PokeSecurity pokeSecurity = PokeSecurity.getInstance(getActivity());
        PokeCredential pokeCredential = pokeSecurity.getCredential();

        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                .getService(pokeCredential.getToken(),9,new Position(loc.latitude, loc.longitude))
                .build();

//        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("token", "1/tonF2rg3bavTh84gxnN9OC3_xLVr5YK5ZO1xWwNeGmE");
//        params.put("token", pokeCredential.getToken());
//        params.put("width", 9);
//        params.put("position", new Position(6.2538345, -75.57843804));
        Call<List<PokemonPosition>> caller = endPoints.getPokemonPositions(params);

        caller.enqueue(new Callback<List<PokemonPosition>>() {
            @Override
            public void onResponse(Call<List<PokemonPosition>> call, Response<List<PokemonPosition>> response) {
                List<PokemonPosition> pos = response.body();

                dialog.dismiss();

                for (PokemonPosition pokemonPosition: pos){
                    markerM.addMarkerPokemon(pokemonPosition);
                }
            }

            @Override
            public void onFailure(Call<List<PokemonPosition>> call, Throwable t) {
                Log.e("PKMERROR", "Error llamando servicio", t);
                dialog.dismiss();
                Toast toast = Toast.makeText(getContext(), "Error llamando servicio", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    class TaskAnimation extends AsyncTask<Void, String, Void> {

        MarkerManager markerManager;
        ProgressDialog progressDialog;
        LatLng latLng;


        public  TaskAnimation(MarkerManager marker, LatLng loc){
            this.markerManager = marker;
            this.latLng = loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getPositions(markerManager, progressDialog, latLng);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            // execution of result of Long time consuming operation
        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.custom_progressdialog);
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}