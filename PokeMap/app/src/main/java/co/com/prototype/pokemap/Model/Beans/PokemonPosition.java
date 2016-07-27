package co.com.prototype.pokemap.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlosmario on 17/07/2016.
 */
public class PokemonPosition extends ObjectPokemonGo {

    @SerializedName("name")
    private String name_;

    @SerializedName("timetohide")
    private String timeToHide;

    public PokemonPosition(String name, String timeToHide, Position position){
        super(position);

        this.name_ = name;
        this.timeToHide = timeToHide;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getTimeToHide() {
        return timeToHide;
    }

    public void setTimeToHide(String timeToHide) {
        this.timeToHide = timeToHide;
    }
}
