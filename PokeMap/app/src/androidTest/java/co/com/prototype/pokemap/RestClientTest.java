package co.com.prototype.pokemap;


import android.util.Log;

import org.junit.Test;

import java.util.List;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;
import co.com.prototype.pokemap.Model.Repository.ApiClient;
import co.com.prototype.pokemap.Model.Repository.IApiContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carlosmario on 17/07/2016.
 */

public class RestClientTest {


    @Test
    public void getPokemonByNameTest(){

        IApiContract endPoints = ApiClient.getClient(IApiContract.class);

        Call<List<PokemonPosition>> caller = endPoints.getPokemonPositions("Pikachu");
        caller.enqueue(new Callback<List<PokemonPosition>>() {
            @Override
            public void onResponse(Call<List<PokemonPosition>> call, Response<List<PokemonPosition>> response) {
                List<PokemonPosition> pos = response.body();
            }

            @Override
            public void onFailure(Call<List<PokemonPosition>> call, Throwable t) {
                Log.e("PKMERROR", t.getMessage());
            }
        });

    }

}