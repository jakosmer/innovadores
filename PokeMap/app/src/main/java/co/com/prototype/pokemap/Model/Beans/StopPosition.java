package co.com.prototype.pokemap.Model.Beans;

/**
 * Created by carlviar on 2016/07/27.
 */
public class StopPosition extends ObjectPokemonGo {

    private String description;

    public StopPosition(String description, Position position){
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
