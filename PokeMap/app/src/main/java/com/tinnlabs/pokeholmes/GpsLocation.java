package com.tinnlabs.pokeholmes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by jorgmecs on 2016/07/18.
 */
public class GpsLocation extends Service implements LocationListener {
    private Context context;
    private double latitud;
    private double longitud;
    Location location;
    LocationManager locationManager;
    boolean gpsActivo= false;

    private static final long TIME_LOCATION = 0; //* 60; // 1 minuto
    private static final long DISTANCE = 0; // 1 metro

    public GpsLocation(){
        super();
        this.context = getApplicationContext();
    }

    public GpsLocation(Context context) {
        super();
        this.context = context;
        getLocation();
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){}

        if(gpsActivo){
            if (ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 1, this);

                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if(location==null){
                    Toast toast = Toast.makeText(context, "GPS INACTIVO", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else{
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                }
            }
        }
    }


    @Override
    public void onLocationChanged(android.location.Location location) {
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast toast = Toast.makeText(context, "GPS ACTIVO, vuelva a consultar", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast toast = Toast.makeText(context, "GPS ACTIVO, vuelva a consultar", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast toast = Toast.makeText(context, "GPS INACTIVO", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public boolean validarGPS(){
        boolean gpsActivo = false;

        if (location != null)
            gpsActivo= true;
        else
            gpsActivo=false;
        return gpsActivo;
    }
}
