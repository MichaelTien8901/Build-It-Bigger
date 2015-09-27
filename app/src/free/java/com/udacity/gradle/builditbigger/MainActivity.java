package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ymsgsoft.michaeltien.jokeactivity.MyJokeActivity;
import com.ymsgsoft.udacity.backend.joke.myApi.MyApi;

import java.io.IOException;


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
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
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

    public static class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
        private final String LOG_TAG = EndpointsAsyncTask.class.getSimpleName();
        private MyApi myApiService = null;
        public PostExecuteCallbacks mCallback;
        @Override
        protected String doInBackground(Void... params) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver
                myApiService = builder.build();
            }

            // add wait time to test progress
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Log.d( LOG_TAG, e.getMessage());
//                return null;
//            }
            try {
                return myApiService.getJoke().execute().getData();
            } catch (IOException e) {
                Log.d( LOG_TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mCallback != null)
                mCallback.AsyncTaskPostCallbackResult(result);
        }
    }
}


