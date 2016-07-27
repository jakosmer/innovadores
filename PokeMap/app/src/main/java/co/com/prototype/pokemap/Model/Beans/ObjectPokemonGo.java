package co.com.prototype.pokemap.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlviar on 2016/07/27.
 */
public class ObjectPokemonGo {

    @SerializedName("position")
    private Position position;

    public ObjectPokemonGo(Position position){
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
