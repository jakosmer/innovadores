package co.com.prototype.pokemap.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlviar on 2016/07/27.
 */
public class GymPosition extends ObjectPokemonGo {

    @SerializedName("teamColor")
    private String teamColor;

    public GymPosition(String teamColor, Position position){
        super(position);

        this.teamColor = teamColor;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }
}
