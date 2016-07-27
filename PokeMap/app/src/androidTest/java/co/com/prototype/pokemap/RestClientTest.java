package co.com.prototype.pokemap;


import android.util.Log;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;
import co.com.prototype.pokemap.Model.Beans.Position;
import co.com.prototype.pokemap.Model.Services.ApiFactoryClient;
import co.com.prototype.pokemap.Model.Services.IApiContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carlosmario on 17/07/2016.
 */

public class RestClientTest {


    @Test
    public void getPokemonByNameTest(){

        IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUwNzgyYmNmMGE5NzQxZTZiZjkwMjY2ZGMzNTY4YWE5MDc5MWYxNmYifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoiOXJWT25NZnFlYUxwbEs3OWZ0REFVUSIsImF1ZCI6Ijg0ODIzMjUxMTI0MC03M3JpM3Q3cGx2azk2cGo0Zjg1dWo4b3RkYXQyYWxlbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMTQyMTY1MjcxMjA1NzEwMDc1MCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDgyMzI1MTEyNDAtNzNyaTN0N3Bsdms5NnBqNGY4NXVqOG90ZGF0MmFsZW0uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImFhcmlhc3RhQGdtYWlsLmNvbSIsImlhdCI6MTQ2OTYzNjAxNSwiZXhwIjoxNDY5NjM5NjE1fQ.d7ljeFK7nlZbPMaPVIcL9UW-zqds-fu6uQFK2XSQxsj_-a-Jis4PdfIIGX4rMcECL4yLVvoyk50Ne__cLO_50onKYE4UleVFEBhT1qozIfVJfT8Kf7Y3WxrHjPwFVb-NTjcA0nfrmxmm25OO4d-QWlcdm2bhhLtkkqewDAgRN66X_QXsIkQ0__JGzjnK775IHxqS0f4NQt3qSw9amjFRiQTiYUdekQ8HyjkmmpfY4LA9Mzw_vb_lDTiTfn9RVcMHtA3-fI2PO9o7-InaU-lhXSDoXcYFeJ7XW0FShvCZyte26rNQ93ZjjqjWR8BA-dAqvdwMfDDzucwmmoYMhNVuLg");
        params.put("position", new Position(6.254053, -75.578910));
        Call<List<PokemonPosition>> caller = endPoints.getPokemonPositions(params);

        caller.enqueue(new Callback<List<PokemonPosition>>() {
            @Override
            public void onResponse(Call<List<PokemonPosition>> call, Response<List<PokemonPosition>> response) {
                List<PokemonPosition> pos = response.body();
            }
            @Override
            public void onFailure(Call<List<PokemonPosition>> call, Throwable t) {
                Log.e("PKMERROR", "Error llamando servicio", t);
            }
        });

    }

}