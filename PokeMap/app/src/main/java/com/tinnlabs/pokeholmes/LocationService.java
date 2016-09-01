package com.tinnlabs.pokeholmes;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.text.DecimalFormat;

public class LocationService extends Service{

    public static final String APP_ID = "co.com.suramericana.gps.location";

    //acciones del servicio
    public static final String ACTION_RESPONSE_GPS = "co.com.suramericana.carlviar.proto.mqttclient.action.RESPONSE";
    public static final String ACTION_CLEAR_LINES_GPS = "co.com.suramericana.carlviar.proto.mqttclient.action.CLEAR_LINES";

    // parametros
    public static final String PARAMNAME_RESPONSE_MESSAGE = "co.com.suramericana.carlviar.proto.mqttclient.params.RESPONSEMSG";
    public static final String PARAMNAME_RESPONSE_CODE = "co.com.suramericana.carlviar.proto.mqttclient.params.CODE";

    public static final String LAST_LATITUDE_LOCATED = "co.com.suramericana.carlviar.proto.mqttclient.latloc";
    public static final String LAST_LONGITUDE_LOCATED = "co.com.suramericana.carlviar.proto.mqttclient.lonloc";

    // distancia minima en metros
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 100 metros

    // tiempo minimo entre actualizaciones en minutos
    private static final long MIN_TIME_BW_UPDATES = 5000; //* 60; // 1 minuto

    private boolean configChanged, firstLocationGot;
    private Location lastLocationReceived;
    private long initialTimestamp;

    public enum RESPONSE_CODES{
        OK,
        NOTNETWORK_GPS,
        NOTNETWORK_WIFI,
        UNKNOWN,
        WARNING,
        MAPPOINT
    }

    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        sendLastLocationReceived();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handle();
                    }
                }, 2000);
                Looper.loop();

            }
        }, "LocationService").start();

        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        configChanged = true;

        if(lastLocationReceived == null){
            super.onConfigurationChanged(newConfig);
            return;
        }

        SharedPreferences preferences = getSharedPreferences(APP_ID, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_LATITUDE_LOCATED, String.valueOf(lastLocationReceived.getLatitude()));
        editor.putString(LAST_LONGITUDE_LOCATED, String.valueOf(lastLocationReceived.getLongitude()));
        editor.apply();

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try
        {
            if(locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }

            if(!configChanged) {
                sendBroadcastToUI("Servicio Gps Detenido", RESPONSE_CODES.UNKNOWN);
            }

        }catch (SecurityException e)
        {
            sendBroadcastToUI(e.getMessage(), RESPONSE_CODES.UNKNOWN);
        }

        lastLocationReceived = null;
    }

    /**
     * Este metodo se encarga de capturar la ubicacion gps del dispositivo
     */
    synchronized void handle(){

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            sendBroadcastToUI("Gps no esta activo", RESPONSE_CODES.NOTNETWORK_GPS);
            stopSelf();
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                /*SharedPreferences preferences = getSharedPreferences(APP_ID, 0);
                if(preferences.getString(LAST_LATITUDE_LOCATED, "").equals(String.valueOf(location.getLatitude())) &&
                   preferences.getString(LAST_LONGITUDE_LOCATED, "").equals(String.valueOf(location.getLongitude()))){

                    return;
                }*/

                if(!firstLocationGot){
                    lastLocationReceived = location;
                    initialTimestamp = location.getTime();
                    firstLocationGot = true;
                    return;
                }

                float speed = location.hasSpeed() &&
                              location.getSpeed() > 0 ? location.getSpeed() :
                                                        location.distanceTo(lastLocationReceived) / ((location.getTime() - initialTimestamp) / 1000);

                speed = (speed * 3600) / 1000;

                lastLocationReceived = location;
                initialTimestamp = location.getTime();
                DecimalFormat formatter = new DecimalFormat("#.0000000");

                sendBroadcastToUI(formatter.format(location.getLatitude()) + ";" + formatter.format(location.getLongitude()) + ";" + speed, RESPONSE_CODES.OK);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                sendBroadcastToUI("Provedor de red desactivado", RESPONSE_CODES.WARNING);
            }
        };

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }
        catch (SecurityException e)
        {
            sendBroadcastToUI(e.getMessage(), RESPONSE_CODES.UNKNOWN);
        }

    }

    public void sendLastLocationReceived(){
        if(lastLocationReceived == null) {

            try {
                lastLocationReceived = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                sendBroadcastToUI(e.getMessage(), RESPONSE_CODES.UNKNOWN);
            }

        }

        if(lastLocationReceived == null){
            return;
        }

        sendBroadcastToUI(lastLocationReceived.getLatitude() + ";" + lastLocationReceived.getLongitude(), RESPONSE_CODES.MAPPOINT);
    }

    /**
     * Envia un mensaje hacia el hilo principal (interface grafica)
     * @param message mensaje que se envia
     * @param type tipo de mensaje basado en el enum RESPONSE_CODES
     */
    private void sendBroadcastToUI(String message, RESPONSE_CODES type){

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESPONSE_GPS);
        broadcastIntent.putExtra(PARAMNAME_RESPONSE_MESSAGE, message);
        broadcastIntent.putExtra(PARAMNAME_RESPONSE_CODE, type);
        sendBroadcast(broadcastIntent);

    }
}
