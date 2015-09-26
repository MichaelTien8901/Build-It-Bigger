package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ymsgsoft.michaeltien.jokeactivity.MyJokeActivity;
import com.ymsgsoft.udacity.backend.joke.myApi.MyApi;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.ProgressBarCallbacks {
    private ProgressBar mProgressBar;

    @Override
    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        new EndpointsAsyncTask().execute(new Pair<Context, ProgressBar>(this, mProgressBar));
    }
    public class EndpointsAsyncTask extends AsyncTask<Pair<Context, ProgressBar>, Void, String> {
        private ProgressBar mProgressBar;
        private MyApi myApiService = null;
        private Context context;
        @Override
        protected String doInBackground(Pair<Context, ProgressBar>... params) {
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

            context = params[0].first;
            mProgressBar = params[0].second;
            // add wait time to test progress
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            try {
                return myApiService.getJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // delay sometime

            mProgressBar.setVisibility(View.GONE);
            Intent intent = new Intent( context, MyJokeActivity.class);
            intent.putExtra(MyJokeActivity.JOKE_KEY, result);
            startActivity(intent);
        }
    }
}
