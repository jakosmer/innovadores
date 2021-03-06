package com.tinnlabs.pokeholmes;

import android.Manifest;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

import com.tinnlabs.pokeholmes.Model.Beans.GymPosition;
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

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MapZoneGym extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerManager markerManager;
    private LatLng localizacion;
    private PulsatorLayout pulsator;
    private FloatingActionButton search;
    private Circle area;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_zone_gym, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        FloatingActionButton actionR = (FloatingActionButton) view.findViewById(R.id.action_r);
        actionR.setOnClickListener(this::floatingClick);

        FloatingActionButton actionB = (FloatingActionButton) view.findViewById(R.id.action_b);
        actionB.setOnClickListener(this::floatingClick);

        FloatingActionButton actionY = (FloatingActionButton) view.findViewById(R.id.action_y);
        actionY.setOnClickListener(this::floatingClick);

        FloatingActionButton actionA = (FloatingActionButton) view.findViewById(R.id.action_a);
        actionA.setOnClickListener(this::floatingClick);

        pulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsator.start();

        search = (FloatingActionButton) view.findViewById(R.id.search_poke);
        search.setOnClickListener(this::floatingSearch);
        search.setOnClickListener(v -> {
            pulsator.setVisibility(View.VISIBLE);
            search.setVisibility(View.INVISIBLE);
            floatingClick(v);
        });

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

        try {
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
                pulsator.setVisibility(View.VISIBLE);
                search.setVisibility(View.INVISIBLE);
                TaskAnimation taskAnimation = new TaskAnimation(markerManager,"",localizacion);
                taskAnimation.execute();


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 3));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12)
                        , 1000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacion, 15));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                area = markerManager.addCircle(localizacion,800);
            }

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (myPosition[0] != null) {
                        myPosition[0].remove();
                    }
                    localizacion = latLng;
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

                    localizacion = loc;

                    return true;
                }
            });
        }catch (Exception e){
        }

    }

    private void getPositions(MarkerManager markerM, String team, LatLng loc){
        PokeSecurity pokeSecurity = PokeSecurity.getInstance(getActivity());
        PokeCredential pokeCredential = pokeSecurity.getCredential();

        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                .getService(pokeCredential.getToken(),4,new Position(loc.latitude, loc.longitude))
                .build();

        Call<List<GymPosition>> caller = endPoints.getGymPositions(params);

        caller.enqueue(new Callback<List<GymPosition>>() {
            @Override
            public void onResponse(Call<List<GymPosition>> call, Response<List<GymPosition>> response) {
                List<GymPosition> pos = response.body();

                pulsator.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                //dialog.dismiss();

                for (GymPosition gymPosition: pos){
                    if(team.isEmpty())
                        markerM.addMarkerGym(gymPosition, gymPosition.getTeamColor());

                    if (team.equals("RED") && gymPosition.getTeamColor().equals("RED")){
                        markerM.addMarkerGym(gymPosition, team);
                    }
                    if (team.equals("BLUE") && gymPosition.getTeamColor().equals("BLUE")){
                        markerM.addMarkerGym(gymPosition, team);
                    }
                    if (team.equals("YELLOW") && gymPosition.getTeamColor().equals("YELLOW")){
                        markerM.addMarkerGym(gymPosition, team);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GymPosition>> call, Throwable t) {
                Log.e("PKMERROR", "ERROR DE CONEXIÓN AL SERVICIO", t);
                Toast toast = Toast.makeText(getContext(), "ERROR DE CONEXIÓN AL SERVICIO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                pulsator.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
            }
        });
    }

    class TaskAnimation extends AsyncTask<Void, String, Void> {

        String color;
        MarkerManager markerManager;
        LatLng latLng;

        public  TaskAnimation(MarkerManager marker,String color, LatLng loc){
            this.color = color;
            this.markerManager = marker;
            this.latLng = loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getPositions(markerManager,color, latLng);
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
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);
        }
    }

    public void floatingClick(View view){
        String gymName = "";

        switch (view.getId()) {
            case R.id.action_r:
                gymName = "RED";
                break;
            case R.id.action_b:
                gymName = "BLUE";
                break;
            case R.id.action_y:
                gymName = "YELLOW";
                break;
            case R.id.action_a:
                gymName = "";
                break;
        }
        mMap.clear();
        TaskAnimation taskAnimation = new TaskAnimation(markerManager,gymName, localizacion);
        taskAnimation.execute();
        }


    private void floatingSearch(View view) {

        area.remove();
        area = markerManager.addCircle(localizacion,400);
        TaskAnimation taskAnimation = new TaskAnimation(markerManager,"" ,localizacion);
        taskAnimation.execute();
    }

}
