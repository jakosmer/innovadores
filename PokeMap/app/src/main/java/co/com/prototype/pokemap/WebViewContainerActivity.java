package co.com.prototype.pokemap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import co.com.prototype.pokemap.Security.PokeSecurity;

public class WebViewContainerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    public static final String PARAM_AUTH_CODE = "co.com.prototype.pokemap.WebViewContainerActivity.paramAuthCode";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view_container);

        WebView webView = (WebView)findViewById(R.id.webView);
        if(webView != null){
            webView.setWebViewClient(new PokeBrowser());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new LoadListener(), "HTMLOUT");
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl("https://accounts.google.com/o/oauth2/auth?client_id=848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=openid email https://www.googleapis.com/auth/userinfo.email");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view_container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class LoadListener{

        @JavascriptInterface
        public void processHTML(String html)
        {
            Log.e("result", html);

            Intent intent = new Intent(WebViewContainerActivity.this, LoginActivity.class);
            intent.putExtra(PARAM_AUTH_CODE, html);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

}

