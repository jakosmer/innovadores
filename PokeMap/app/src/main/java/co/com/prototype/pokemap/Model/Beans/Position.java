package co.com.prototype.pokemap.Model.Beans;

/**
 * Created by carlviar on 2016/07/27.
 */
public class Position {

    private Double latitud;

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
}
