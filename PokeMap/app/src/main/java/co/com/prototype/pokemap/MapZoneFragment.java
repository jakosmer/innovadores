package co.com.prototype.pokemap;

import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

        // Add a marker in Sydney and move the camera
        //LocationService prueba = new LocationService();


        GpsLocation gpsLocation = new GpsLocation(getActivity().getApplicationContext());

        LatLng sydney = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Med"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,3));
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
    }
}
