package co.com.prototype.pokemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

import co.com.prototype.pokemap.Model.Beans.GymPosition;
import co.com.prototype.pokemap.Model.Beans.PokemonPosition;
import co.com.prototype.pokemap.Model.Beans.Position;
import co.com.prototype.pokemap.Model.Services.ApiFactoryClient;
import co.com.prototype.pokemap.Model.Services.IApiContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jorgmecs on 2016/07/27.
 */
public class MapZoneGym extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_zone_gym, container, false);

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

        FloatingActionButton selectGym = (FloatingActionButton)this.getActivity().findViewById(R.id.gym);
        GpsLocation gpsLocation = new GpsLocation(getActivity().getApplicationContext());
        LatLng loc = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
        MarkerManager markerManager = new MarkerManager(mMap, getResources(), this.getActivity().getPackageName());
        final Marker[] myPosition = {markerManager.addMarkerGeneric(loc)};

        getPositions(markerManager,"");

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

        /*markerManager.addMarkerGym(new LatLng(6.26718156, -75.58027267), "RED");
        markerManager.addMarkerGym(new LatLng(6.25133355, -75.56647539), "RED");
        markerManager.addMarkerGym(new LatLng(6.25730594, -75.55932999), "BLUE");
        markerManager.addMarkerGym(new LatLng(6.25171749, -75.56819201), "YELLOW");*/



        selectGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast toast = Toast.makeText(MapZoneGym.this.getContext(), "PokemonGO not found",
//                        Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER,0,0);
//                toast.show();

                PopupMenu popup = new PopupMenu(MapZoneGym.this.getContext(), selectGym);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu_gyms, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mMap.clear();
                        myPosition[0] = markerManager.addMarkerGeneric(loc);

                        if (item.getTitle().equals("BLUE teams"))
                            getPositions(markerManager,"BLUE");
                        if (item.getTitle().equals("RED teams"))
                            getPositions(markerManager,"RED");
                        if (item.getTitle().equals("YELLOW teams"))
                            getPositions(markerManager,"YELLOW");
                        if (item.getTitle().equals("ALL teams"))
                            getPositions(markerManager,"");

                        return true;
                    }
                });

                popup.show(); //showing popup menu
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

    private void getPositions(MarkerManager markerM, String team){
        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", "1/tonF2rg3bavTh84gxnN9OC3_xLVr5YK5ZO1xWwNeGmE");
        params.put("width", 9);
        params.put("position", new Position(6.2538345, -75.57843804));
        Call<List<GymPosition>> caller = endPoints.getGymPositions(params);

        caller.enqueue(new Callback<List<GymPosition>>() {
            @Override
            public void onResponse(Call<List<GymPosition>> call, Response<List<GymPosition>> response) {
                List<GymPosition> pos = response.body();

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
                Log.e("PKMERROR", "Error llamando servicio", t);
            }
        });
    }
}
