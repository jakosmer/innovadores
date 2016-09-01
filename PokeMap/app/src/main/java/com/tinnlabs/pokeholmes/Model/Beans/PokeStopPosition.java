package com.tinnlabs.pokeholmes.Model.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carlviar on 2016/07/27.
 */
public class PokeStopPosition extends ObjectPokemonGo {

    @SerializedName("description")
    private String description;

    public PokeStopPosition(String description, Position position){
        super(position);

        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
