package com.tinnlabs.pokeholmes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
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

import com.getbase.floatingactionbutton.FloatingActionButton;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinnlabs.pokeholmes.Model.Beans.PokemonPosition;
import com.tinnlabs.pokeholmes.Model.Beans.Position;
import com.tinnlabs.pokeholmes.Model.Services.ApiFactoryClient;
import com.tinnlabs.pokeholmes.Model.Services.IApiContract;
import com.tinnlabs.pokeholmes.Security.PokeCredential;
import com.tinnlabs.pokeholmes.Security.PokeSecurity;
import com.tinnlabs.pokeholmes.Utils.ApiEndPointsBodyGenerator;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapZoneFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerManager markerManager;
    private LatLng localizacion;
    private PulsatorLayout pulsator;
    private FloatingActionButton search;
    private Circle area;

    private Socket mSocket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_zone, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsator.start();

        search = (FloatingActionButton) view.findViewById(R.id.search_poke);
        search.setOnClickListener(this::floatingClick);
        search.setOnClickListener(v -> {
            pulsator.setVisibility(View.VISIBLE);
            search.setVisibility(View.INVISIBLE);
            floatingClick(v);
        });

        try {

            //192.168.42.74
            mSocket = IO.socket("http://50.116.54.176:3000");

        } catch (URISyntaxException e) {

        }

        mSocket.on("57", onMessageFromServerReceived);
        mSocket.connect();

        mSocket.emit("connection", "mobile");

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
        localizacion = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
        markerManager = new MarkerManager(mMap, getResources(), this.getActivity().getPackageName());
        final Marker[] myPosition = {markerManager.addMarkerGeneric(localizacion)};

        if (gpsLocation.validarGPS()){
            search.setVisibility(View.INVISIBLE);
            pulsator.setVisibility(View.VISIBLE);
            TaskAnimation taskAnimation = new TaskAnimation(localizacion);
            taskAnimation.execute();

            area = markerManager.addCircle(localizacion,70);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 5));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16)
                    , 1000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 16));
        }

        mMap.setOnMapClickListener(latLng -> {
            if (myPosition[0] != null) {
                myPosition[0].remove();
            }
            localizacion = latLng;
            myPosition[0] = markerManager.addMarkerGeneric(localizacion);
        });

        mMap.setOnMyLocationButtonClickListener(() -> {
            GpsLocation gpsLocation1 = new GpsLocation(getActivity().getApplicationContext());
            LatLng loc1 = new LatLng(gpsLocation1.getLatitud(), gpsLocation1.getLongitud());
            if (myPosition[0] != null) {
                myPosition[0].remove();
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 18));
            myPosition[0] = markerManager.addMarkerGeneric(loc1);
            localizacion = loc1;

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

    private void getPositions(ProgressDialog dialog, LatLng loc){

        PokeSecurity pokeSecurity = PokeSecurity.getInstance(getActivity());
        PokeCredential pokeCredential = pokeSecurity.getCredential();

        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                .getService(pokeCredential.getToken(),9,new Position(loc.latitude, loc.longitude))
                .build();

        Call<List<PokemonPosition>> caller = endPoints.getPokemonPositions(params);

        caller.enqueue(new Callback<List<PokemonPosition>>() {
            @Override
            public void onResponse(Call<List<PokemonPosition>> call, Response<List<PokemonPosition>> response) {
                List<PokemonPosition> pos = response.body();

                pulsator.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                if(pos.isEmpty()){
                    Toast toast = Toast.makeText(getContext(), "No se encontraron pokemones cerca", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                for (PokemonPosition pokemonPosition: pos){
                    markerManager.addMarkerPokemon(pokemonPosition);
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

    private Emitter.Listener onMessageFromServerReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Object data = "nada";
            if(args.length > 0) {
                data = args[0];
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<PokemonPosition>>(){}.getType();
            List<PokemonPosition> pos = gson.fromJson(data.toString(), listType);

            for (PokemonPosition pokemonPosition: pos){
                    markerManager.addMarkerPokemon(pokemonPosition);
            }

            Log.i("TAG", "llego desde socket: " + data.toString());
        }
    };

    class TaskAnimation extends AsyncTask<Void, String, Void> {

        ProgressDialog progressDialog;
        LatLng latLng;


        public  TaskAnimation(LatLng loc){
            this.latLng = loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getPositions(progressDialog, latLng);
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
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.show();
//            progressDialog.setContentView(R.layout.custom_progressdialog);
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    public void floatingClick(View view){
        area.remove();
        area = markerManager.addCircle(localizacion,70);
        TaskAnimation taskAnimation = new TaskAnimation(localizacion);
        taskAnimation.execute();
    }
}