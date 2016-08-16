package co.com.prototype.pokemap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by carlosmario on 03/08/2016.
 */
public class PokeBrowser extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {

        view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementById('code').value);");
        /*if(!url.equals("https://accounts.google.com/o/oauth2/auth?client_id=848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=openid%20email%20https://www.googleapis.com/auth/userinfo.email")) {
            view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementById('code').value);");
            view.loadUrl("about:blank");
        }*/

    }
}
