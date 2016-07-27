package co.com.prototype.pokemap.Model.Repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carlviar on 2016/07/26.
 */
public class ApiClient {

    static final String BASE_URL = "http://192.168.56.1:9000/";
    private static IApiContract endPoints = null;

    public static IApiContract getClient(Class<IApiContract> endPoints){

        if(ApiClient.endPoints == null){
            ApiClient.endPoints = new Retrofit.Builder()
                                   .baseUrl(BASE_URL)
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .build()
                                   .create(endPoints);
        }

        return ApiClient.endPoints;
    }

}
