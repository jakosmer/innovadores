package com.tinnlabs.pokeholmes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tinnlabs.pokeholmes.Security.PokeSecurity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PokeSecurity.getInstance(this).requestUserToken();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setCustomView(R.xml.actionbar_view);
                bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);

        ImageView fab = (ImageView) findViewById(R.id.imgBtnPGO);
        assert fab != null;
        fab.setOnClickListener(view ->  {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
            if (launchIntent != null) {
                startActivity(launchIntent);
            }else{
                Toast toast = Toast.makeText(MainActivity.this, "PokemonGO not found",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView_principal);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("EBB15B318D42B25D62CA995AFD484995")
                .addTestDevice("BD21A88C03DC2D22999E4D2FA94B79D8")
                .addTestDevice("AD91A0078CFE57D4DC4F184CA7C9A3C6")
                .build();

        if(mAdView != null) {
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_poketips) {
            // Handle the camera action
        } else if (id == R.id.nav_pokemon) {
            fragment = new MapZoneFragment();
        } else if (id == R.id.nav_pokeparadas) {
            fragment = new MapZonePokeStop();
        } else if (id == R.id.nav_gyms) {
            fragment = new MapZoneGym();
        } else if (id == R.id.nav_share) {
            PokeSecurity.getInstance(this).singOut();
            return true;
        } //else if (id == R.id.nav_send) {
        //}

        if(fragment!= null){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            assert drawer != null;
            drawer.closeDrawer(GravityCompat.START);

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        }else{
            Toast toast = Toast.makeText(MainActivity.this, "EN CONSTRUCCIÓN",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        return true;
    }
}
