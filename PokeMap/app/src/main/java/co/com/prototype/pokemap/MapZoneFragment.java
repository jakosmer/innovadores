package co.com.prototype.pokemap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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




        //markerManager.addMarkerPokemon(sydney, "p_3");

        for (int i=0;i<2;i++){
            MarkerManager markerManager = new MarkerManager(mMap,getResources(), this.getActivity().getPackageName());
            markerManager.addMarkerPokemon(generateRadomGpsLocation(sydney), "p_3");
        }


        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(6.26718156, -75.58027267))
                .radius(300)
                .fillColor(Color.argb(150, 84, 162, 208))
                .strokeWidth(1).strokeColor(Color.argb(150, 84, 162, 208));

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);


        /*Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(300, 300, conf);
        Canvas canvas1 = new Canvas(bmp);

        // paint defines the text color, stroke width and size
        Paint color = new Paint();
        color.setTextSize(20);
        color.setColor(Color.BLACK);

        // modify canvas
        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pikachu), 0,0, color);
        canvas1.drawText("Contador", 30, 40, color);
        MarkerOptions options = new MarkerOptions().position(new LatLng(6.26718156, -75.58027267)).title("Med 2").icon(BitmapDescriptorFactory.fromBitmap(bmp)).anchor(0.5f, 1);
        Marker marker =  mMap.addMarker(options);

        Thread t = new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                try {
                    while (true) {
                        i++;
                        canvas1.drawText("Contador " + String.valueOf(i), 30, 40, color);

                        AsyncAnimator animator = new AsyncAnimator(marker);
                        animator.execute(i);

                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    Log.e("MapZone", e.getMessage(), e);
                }
            }
        });
        t.start(); */

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Med 1").icon(BitmapDescriptorFactory.fromResource(R.drawable.bulbasaur)));

        mMap.addMarker(new MarkerOptions().position(new LatLng(6.25532226, -75.5848217)).title("Med 3").icon(BitmapDescriptorFactory.fromResource(R.drawable.squirtle)));
        /*mMap.addMarker(new MarkerOptions().position(new LatLng(6.23603966, -75.56619644)).title("Med 4").icon(BitmapDescriptorFactory.fromResource(R.drawable.pikachu)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(6.25958823, -75.5459404)).title("Med 5").icon(BitmapDescriptorFactory.fromResource(R.drawable.squirtle)));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 3));
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

    private LatLng generateRadomGpsLocation(LatLng actualPos){

        double y0 = actualPos.latitude;
        double x0 = actualPos.longitude;
        double radio = 2000f / 111300;

        double seedRadomA = Math.random();
        double seedRadomB = Math.random();

        double w = radio * Math.sqrt(seedRadomA);
        double t = 2 * Math.PI * seedRadomB;

        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        return new LatLng( (y + y0), (x + x0) );
    }

}    /*class AsyncAnimator extends AsyncTask<Integer, Void, Bitmap>{

        private Marker marker;

        public AsyncAnimator(Marker marker){
            this.marker = marker;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(300, 300, conf);
            Canvas canvas1 = new Canvas(bmp);

            // paint defines the text color, stroke width and size
            Paint color = new Paint();
            color.setTextSize(20);
            color.setColor(Color.BLACK);

            // modify canvas
            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pikachu), 0,0, color);
            canvas1.drawText("Contador", 30, 40, color);
            canvas1.drawText("Contador " + String.valueOf(params[0]), 30, 40, color);

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
    }

}*/
