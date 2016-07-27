package co.com.prototype.pokemap.Model.Repository;

import java.util.List;

import co.com.prototype.pokemap.Model.Beans.GymPosition;
import co.com.prototype.pokemap.Model.Beans.PokemonPosition;

import co.com.prototype.pokemap.Model.Beans.Position;
import co.com.prototype.pokemap.Model.Beans.StopPosition;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by carlosmario on 17/07/2016.
 */
public interface IApiContract {

    @POST("findActivePokemon")
    @Headers({"Accept: application/json" })
    Call<List<PokemonPosition>> getPokemonPositions(@Field("token") String token, @Field("position")Position position);

    @POST("findPokeGym")
    @Headers({"Accept: application/json" })
    Call<List<GymPosition>> getGymPositions(@Field("token") String token, @Field("position")Position position);

    @POST("findPokeStop")
    @Headers({"Accept: application/json" })
    Call<List<StopPosition>> getPokeStopPositions(@Field("token") String token, @Field("position")Position position);

}
