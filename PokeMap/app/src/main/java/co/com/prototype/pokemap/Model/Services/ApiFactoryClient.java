package co.com.prototype.pokemap.Model.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carlviar on 2016/07/26.
 */
public class ApiFactoryClient {

    static final String BASE_URL = "http://10.201.78.203:9000/";
    private static IApiContract endPoints = null;

    public static IApiContract getClient(Class<IApiContract> endPoints){

        if(ApiFactoryClient.endPoints == null){
            ApiFactoryClient.endPoints = new Retrofit.Builder()
                                   .baseUrl(BASE_URL)
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .build()
                                   .create(endPoints);
        }

        return ApiFactoryClient.endPoints;
    }

}
