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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MapZoneGym extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerManager markerManager;

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
            LatLng loc = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
            markerManager = new MarkerManager(mMap, getResources(), this.getActivity().getPackageName());
            final Marker[] myPosition = {markerManager.addMarkerGeneric(loc)};

            if (gpsLocation.validarGPS()){
                TaskAnimation taskAnimation = new TaskAnimation(markerManager,"");
                taskAnimation.execute();


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 3));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12)
                        , 1000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });

            }

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
        }catch (Exception e){
        }

    }

    private void getPositions(MarkerManager markerM, String team, ProgressDialog dialog){
        PokeSecurity pokeSecurity = PokeSecurity.getInstance(getActivity());
        PokeCredential pokeCredential = pokeSecurity.getCredential();

//        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);
//
//        HashMap<String, Object> params = new HashMap<>();
////        params.put("token", "1/tonF2rg3bavTh84gxnN9OC3_xLVr5YK5ZO1xWwNeGmE");
//        params.put("token", pokeCredential.getToken());
//        params.put("width", 9);
//        params.put("position", new Position(6.2538345, -75.57843804));



        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                .getService(pokeCredential.getToken(),9,new Position(6.2538345, -75.57843804))
                .build();

        Call<List<GymPosition>> caller = endPoints.getGymPositions(params);
//        Call<List<GymPosition>> caller = endPoints.getGymPositions(params);

        caller.enqueue(new Callback<List<GymPosition>>() {
            @Override
            public void onResponse(Call<List<GymPosition>> call, Response<List<GymPosition>> response) {
                List<GymPosition> pos = response.body();

                dialog.dismiss();

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
                dialog.dismiss();
                Toast toast = Toast.makeText(getContext(), "ERROR DE CONEXIÓN AL SERVICIO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    class TaskAnimation extends AsyncTask<Void, String, Void> {

        String color;
        MarkerManager markerManager;
        ProgressDialog progressDialog;


        public  TaskAnimation(MarkerManager marker,String color){
            this.color = color;
            this.markerManager = marker;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getPositions(markerManager,color, progressDialog);
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
        TaskAnimation taskAnimation = new TaskAnimation(markerManager,gymName);
        taskAnimation.execute();
        }
    }
