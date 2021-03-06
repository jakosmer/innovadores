package com.tinnlabs.pokeholmes.Model.Services;

import java.util.HashMap;
import java.util.List;

import com.tinnlabs.pokeholmes.Model.Beans.GymPosition;
import com.tinnlabs.pokeholmes.Model.Beans.PokemonPosition;

import com.tinnlabs.pokeholmes.Model.Beans.PokeStopPosition;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by carlosmario on 17/07/2016.
 */
public interface IApiContract {

    @POST("findActivePokemon")
    @Headers({"Accept: application/json" })
    Call<List<PokemonPosition>> getPokemonPositions(@Body HashMap<String, Object> body);

    @POST("findPokeGym")
    @Headers({"Accept: application/json" })
    Call<List<GymPosition>> getGymPositions(@Body HashMap<String, Object> body);

    @POST("findPokeStop")
    @Headers({"Accept: application/json" })
    Call<List<PokeStopPosition>> getPokeStopPositions(@Body HashMap<String, Object> body);

    @POST("getRefresh")
    Call<String> getToken(@Body HashMap<String, Object> body);

    @POST("getHeaders")
    Call<String> getHeaders(@Body HashMap<String, Object> body);

}
