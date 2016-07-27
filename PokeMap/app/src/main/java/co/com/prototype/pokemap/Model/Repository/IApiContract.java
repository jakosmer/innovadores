package co.com.prototype.pokemap.Model.Repository;

import java.util.List;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by carlosmario on 17/07/2016.
 */
public interface IApiContract {

    @GET("findPokemon/{pokemonName}")
    @Headers({"Accept: application/json" })
    Call<List<PokemonPosition>> getPokemonPositions(@Path("pokemonName") String pokemonName);

}
