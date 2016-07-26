package co.com.prototype.pokemap.Model.Beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by carlosmario on 17/07/2016.
 */
public class PokemonPosition {

    @JsonProperty("city")
    public String City;

    @JsonProperty("name")
    public String Name;

    @JsonProperty("country")
    public String Country;

    @JsonProperty("lon")
    public long Longitude;

    @JsonProperty("lat")
    public long Latitude;

}
