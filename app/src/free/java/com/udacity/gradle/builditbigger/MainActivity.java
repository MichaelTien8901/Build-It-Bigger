package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ymsgsoft.michaeltien.jokeactivity.MyJokeActivity;


public class MainActivity extends AppCompatActivity implements
        MainActivityFragment.ProgressBarCallbacks,
        PostExecuteCallbacks {
    private ProgressBar mProgressBar;
    private InterstitialAd mInterstitialAd;
    private boolean is_showing_intertitial_ad, joke_loaded;
    private String newJoke;
    @Override
    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
//                beginPlayingGame();
                is_showing_intertitial_ad= false;
                if ( joke_loaded)
                   showNewJoke();
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void tellJoke(View view){
        mProgressBar.setVisibility(View.VISIBLE);
        joke_loaded = false;
        EndpointsAsyncTask task =new EndpointsAsyncTask();
        task.mCallback = this;
        task.mContext = this;
        task.execute();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            is_showing_intertitial_ad = true;
        } else {
            is_showing_intertitial_ad = false;
        }
    }
    private void showNewJoke()
    {
        Intent intent = new Intent(this, MyJokeActivity.class);
        intent.putExtra(MyJokeActivity.JOKE_KEY, newJoke);
        startActivity(intent);
    }
    @Override
    public void AsyncTaskPostCallbackResult(String result) {
        mProgressBar.setVisibility(View.GONE);
        joke_loaded = true;
        if ( result != null) {
            newJoke = result;
            if ( !is_showing_intertitial_ad)
               showNewJoke();
        } else {
            Toast.makeText(this, "Joke Server is down", Toast.LENGTH_LONG).show();
        }
    }
}


