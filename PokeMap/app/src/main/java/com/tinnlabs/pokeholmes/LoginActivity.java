package com.tinnlabs.pokeholmes;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tinnlabs.pokeholmes.Model.Services.ApiFactoryClient;
import com.tinnlabs.pokeholmes.Model.Services.IApiContract;
import com.tinnlabs.pokeholmes.Security.PokeSecurity;
import com.tinnlabs.pokeholmes.Utils.ApiEndPointsBodyGenerator;
import retrofit2.*;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_FINE_LOCATION = 1;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private PokeBallView mPokeballView;

    //Google client for make singIn
    public static final int RC_SIGN_IN = 1;
    private static final String LOG_GOOGLE_STATE = "PGO_GOOGLE_STATE";
    private static final String LOGIN_STATE = "PGO_LOGIN_STATE";

    private OnMapReadyCallback callback;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /*private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.56.1:3000");
        } catch (URISyntaxException e) {}
    }*/

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PokeSecurity.getInstance(this).startEntryPoint();

        setContentView(R.layout.activity_login);
        // Set up the login form.

        /*mSocket.connect();

        mSocket.on("serverEvent", args -> runOnUiThread(() -> Toast.makeText(LoginActivity.this, args[0].toString(), Toast.LENGTH_SHORT).show()));

        mSocket.emit("connection", "MOBILE");*/

        //pruebas

        /*IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);

        HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                                                                  .addAuthCode("dasdadas")
                                                                  .build();

        Call<String> caller = endPoints.getHeaders(params);
        caller.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.i("helloworld", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(LOGIN_STATE, "Error retreving token from google " + t.getMessage());
            }
        });*/

        //fin pruebas

        mProgressView = findViewById(R.id.login_progress);

        View viewGoogleBtn = findViewById(R.id.sign_in_button);
        if (viewGoogleBtn != null) {
            viewGoogleBtn.setOnClickListener(v -> {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        PokeSecurity.getInstance(LoginActivity.this).signIn(RC_SIGN_IN);
                        break;
                }
            });
        }

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            setCallback();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(callback);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }

        mPokeballView = ((PokeBallView) findViewById(R.id.view_pokeball));

        //ad config
        AdRequest adRequest = new AdRequest.Builder()
                                           .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                           .addTestDevice("EBB15B318D42B25D62CA995AFD484995")
                                            .addTestDevice("BD21A88C03DC2D22999E4D2FA94B79D8")
                .addTestDevice("AD91A0078CFE57D4DC4F184CA7C9A3C6")
                                           .build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_principal));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                PokeSecurity.getInstance(LoginActivity.this).startEntryPoint();
            }
        });
        mInterstitialAd.loadAd(adRequest);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setCallback() {
        callback = googleMap -> {

            GpsLocation gpsLocation = new GpsLocation(getApplicationContext());
            LatLng loc = new LatLng(gpsLocation.getLatitud(), gpsLocation.getLongitud());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
            googleMap.getUiSettings().setAllGesturesEnabled(false);

        };
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    @SuppressLint("NewApi")
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setCallback();

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(callback);

                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "Sin permisos algunas funcionalidades no estarán disponibles",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                return;
            }

            case REQUEST_READ_CONTACTS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateAutoComplete();
                }
                return;
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     *
     * @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) private void showProgress(final boolean show) {
     * // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
     * // for very easy animations. If available, use these APIs to fade-in
     * // the progress spinner.
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
     * int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
     * <p>
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * mLoginFormView.animate().setDuration(shortAnimTime).alpha(
     * show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
     * @Override public void onAnimationEnd(Animator animation) {
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * }
     * });
     * <p>
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * mProgressView.animate().setDuration(shortAnimTime).alpha(
     * show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
     * @Override public void onAnimationEnd(Animator animation) {
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * }
     * });
     * } else {
     * // The ViewPropertyAnimator APIs are not available, so simply show
     * // and hide the relevant UI components.
     * mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
     * mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
     * }
     * }
     */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    //Raised when a google connection failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_GOOGLE_STATE, connectionResult.getErrorMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            String authCode = String.valueOf(data.getExtras().get(WebViewContainerActivity.PARAM_AUTH_CODE));

            IApiContract endPoints = ApiFactoryClient.getClient(IApiContract.class);
            HashMap<String, Object> params = ApiEndPointsBodyGenerator.builder()
                    .addAuthCode(authCode)
                    .build();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.custom_progressdialog);

            Call<String> caller = endPoints.getToken(params);
            caller.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    PokeSecurity security = PokeSecurity.getInstance(LoginActivity.this);
                    if (authCode != null && security.saveGoogleCredentials(authCode, response.body())) {
                        if(mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }else{
                            security.startEntryPoint();
                        }
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(LOGIN_STATE, "Error retreving token from google " + t.getMessage());
                    progressDialog.dismiss();

                    Toast toast = Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });


        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tinnlabs.pokeholmes/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tinnlabs.pokeholmes/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}

