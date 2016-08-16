package co.com.prototype.pokemap.Security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import co.com.prototype.pokemap.LoginActivity;
import co.com.prototype.pokemap.MainActivity;
import co.com.prototype.pokemap.WebViewContainerActivity;

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
    private GoogleApiClient googleApiClientInstance;

    private SharedPreferences preferences;
    private static String LOG_GOOGLE_STATE = "co.com.prototype.pokemap.Security.PokeSecurity:GoogleState";

    private PokeSecurity(Activity activity){
        this.activity = activity;

        preferences = activity.getPreferences(Context.MODE_PRIVATE);

        //Google SignIn Configuration
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.EMAIL), new Scope(Scopes.APP_STATE), new Scope(Scopes.PROFILE))
                .requestServerAuthCode("401881601919-omjth38md0au6gbtoo3pgrv57ja5lal2.apps.googleusercontent.com")
                .build();

        googleApiClientInstance = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity, (GoogleApiClient.OnConnectionFailedListener) activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }

    public static PokeSecurity getInstance(Activity activity){

        if(instance == null){
            instance = new PokeSecurity(activity);
        }

        instance.activity = activity;
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
     * Almacena una credencial google en las sharedPreferences del usuario
     * @param authCode codigo de authorización del usuario
     * @return True si se logra almacenar la credencial, False en otro caso
     */
    public boolean saveGoogleCredentials(String authCode){

            try {

                Log.i(LOG_GOOGLE_STATE, "AuthToken: " + (authCode == null ? "" : authCode));

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PokeCredential.TOKEN_ATTR, authCode);
                editor.apply();

                return true;

            }catch (Exception ne){
                Log.e(LOG_GOOGLE_STATE, "Hubo un problema obteniendo el token de autenticación", ne);
            }

        return false;
    }

    /**
     * Comprueba la existencia de un token y en caso que no se encuentre, se redirige hacia la actividad Login
     */
    public void requestUserToken(){

        if(!preferences.contains(PokeCredential.TOKEN_ATTR)){
            startLoginPoint();
        }

    }

    /**
     * Inicia la actividad principal de la aplicación
     */
    public void startEntryPoint(){

        if(preferences.contains(PokeCredential.TOKEN_ATTR)) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }

    }

    /**
     * Inicia la actividad para el acceso del usuario
     */
    public void startLoginPoint(){

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);

    }

    /**
     * Invoca la actividad de google para hacer sign-in
     * @param idForResult codigo enviado en el callback onActivityResult que debe implementar la actividad consumidora
     */
    public void signIn(int idForResult){

        Intent singInIntent = new Intent(activity, WebViewContainerActivity.class);//Auth.GoogleSignInApi.getSignInIntent(googleApiClientInstance);
        activity.startActivityForResult(singInIntent, idForResult);

    }

    /**
     * Elimina los datos de sesion y reenvia hacia la actividad Login
     */
    public void singOut(){

        instance = null;
        googleApiClientInstance = null;


        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PokeCredential.TOKEN_ATTR);
        editor.remove(PokeCredential.EMAIL_ATTR);
        editor.remove(PokeCredential.USERNAME_ATTR);
        editor.apply();

        preferences = null;

        startLoginPoint();
    }

}


