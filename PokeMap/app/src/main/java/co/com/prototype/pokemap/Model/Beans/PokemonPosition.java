package co.com.prototype.pokemap.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlosmario on 17/07/2016.
 */
public class PokemonPosition {

    @SerializedName("city")
    String city;

    @SerializedName("name")
    String name_;

    @SerializedName("country")
    String country;

    @SerializedName("lon")
    String longitude;

    @SerializedName("lat")
    String latitude;

    public PokemonPosition(String city, String name, String country, String longitude, String latitude){
        this.city = city;
        this.name_ = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
