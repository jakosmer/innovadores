package co.com.prototype.pokemap.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlosmario on 17/07/2016.
 */
public class PokemonPosition extends ObjectPokemonGo {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name_;

    @SerializedName("timeToHide")
    private String timeToHide;

    public PokemonPosition(int id, String name, String timeToHide, Position position){
        super(position);

        this.id = id;
        this.name_ = name;
        this.timeToHide = timeToHide;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
