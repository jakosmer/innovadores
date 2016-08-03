package co.com.prototype.pokemap.Security;

/**
 * @author Carlos Mario Villadiego
 * @version 1.0
 *
 * Objeto que representa una credencial (solo google por el momento)
 */
public class PokeCredential {

    public static String TOKEN_ATTR = "co.com.prototype.pokemap.Security.PokeCredential.Token";
    public static String EMAIL_ATTR = "co.com.prototype.pokemap.Security.PokeCredential.Email";
    public static String USERNAME_ATTR = "co.com.prototype.pokemap.Security.PokeCredential.UserName";

    private String token;
    private String email;
    private String username;

    public PokeCredential(String token, String email, String username) {
        this.token = token;
        this.email = email;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}