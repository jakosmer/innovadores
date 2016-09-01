package com.tinnlabs.pokeholmes.Model.Beans;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by carlviar on 2016/07/27.
 */
public class Position {

    @SerializedName("latitud")
    private Double latitud;

    @SerializedName("longitud")
    private Double longitud;

    public Position(Double latitud, Double longitud){
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public LatLng convertToLatLng(){
        return new LatLng(this.latitud, this.longitud);
    }
}
