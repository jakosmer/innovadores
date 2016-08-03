package co.com.prototype.pokemap.Security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import co.com.prototype.pokemap.LoginActivity;
import co.com.prototype.pokemap.MainActivity;

/**
 * @author Carlos Mario Villadiego
 * @version 1.0
 *
 * Clase encargada de gestionar el token de seguridad del usuario
 *
 */
public class PokeSecurity {

    private Activity activity;
    private static PokeSecurity instance;
    private SharedPreferences preferences;
    private GoogleApiClient googleApiClientInstance;
    private static String LOG_GOOGLE_STATE = "co.com.prototype.pokemap.Security.PokeSecurity:GoogleState";

    private PokeSecurity(Activity activity){
        this.activity = activity;

        preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    private PokeSecurity(Activity activity, GoogleApiClient googleApiClient){
        this.activity = activity;
        this.googleApiClientInstance = googleApiClient;

        preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public static PokeSecurity getInstance(Activity activity){
        return getInstance(activity, null);
    }

    public static PokeSecurity getInstance(Activity activity, GoogleApiClient googleApiClient){

        if(instance == null){
            instance = googleApiClient == null ? new PokeSecurity(activity) : new PokeSecurity(activity, googleApiClient);
        }

        return instance;
    }

    /**
     * Devuelve la credencial {@link PokeCredential} con los datos del usuario logueado
     * @return {@link PokeCredential}
     */
    public PokeCredential getCredential(){

            return new PokeCredential(preferences.getString(PokeCredential.TOKEN_ATTR, null),
                                          preferences.getString(PokeCredential.EMAIL_ATTR, null),
                                          preferences.getString(PokeCredential.USERNAME_ATTR, null));
    }

    /**
     * Almacena una credencial google en las sharedPreferences del usuario
     * @param signInResult respuesta de la autenticacion por google
     * @return True si se logra almacenar la credencial, False en otro caso
     */
    public boolean saveGoogleCredentials(GoogleSignInResult signInResult){

        if(signInResult.isSuccess()){
            GoogleSignInAccount acct = signInResult.getSignInAccount();

            try {

                String idToken = acct.getIdToken();
                String authToken = acct.getServerAuthCode();

                Log.i(LOG_GOOGLE_STATE, "Token: " + (idToken == null ? "" : idToken));
                Log.i(LOG_GOOGLE_STATE, "AuthToken: " + (authToken == null ? "" : authToken));

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PokeCredential.TOKEN_ATTR, authToken);
                editor.putString(PokeCredential.EMAIL_ATTR, acct.getEmail());
                editor.putString(PokeCredential.USERNAME_ATTR, acct.getDisplayName());
                editor.apply();

                return true;

            }catch (Exception ne){
                Log.e(LOG_GOOGLE_STATE, "Hubo un problema obteniendo el token de autenticación", ne);
            }
        }else{
            Log.e(LOG_GOOGLE_STATE, "conexion fallida con código: " + String.valueOf(signInResult.getStatus().getStatusCode()));
        }

        return false;
    }

    /**
     * Comprueba la existencia de un token y en caso que no se encuentre, se redirige hacia la actividad Login
     */
    public void requestUserToken(){

        if(!preferences.contains(PokeCredential.TOKEN_ATTR)){
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        }

    }

    /**
     * Inicia la actividad principal de la aplicación
     */
    public void startEntryPoint(){

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

    }


    public void singOut(){
        Auth.GoogleSignInApi.signOut(googleApiClientInstance).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }
        });
    }

}


