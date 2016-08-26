package co.com.prototype.pokemap.Security;

/**
 * @author Carlos Mario Villadiego
 * @version 1.0
 *
 * Objeto que representa una credencial (solo google por el momento)
 */
public class PokeCredential {

    public static String TOKEN_ATTR = "co.com.prototype.pokemap.Security.PokeCredential.Token";
    public static String AUTH_ATTR = "co.com.prototype.pokemap.Security.PokeCredential.Auth";

    private String token;
    private String auth;

    public PokeCredential(String token, String auth) {
        this.token = token;
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String email) {
        this.auth = email;
    }

}