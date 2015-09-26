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
        EndpointsAsyncTask task =new EndpointsAsyncTask();
        task.mCallback = this;
        task.execute();
    }
    @Override
    public void AsyncTaskPostCallbackResult(String result) {
        mProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent( this, MyJokeActivity.class);
        intent.putExtra(MyJokeActivity.JOKE_KEY, result);
        startActivity(intent);
    }

    public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d( LOG_TAG, e.getMessage());
                return null;
            }
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


