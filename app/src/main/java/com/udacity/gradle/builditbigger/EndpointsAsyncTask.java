package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ymsgsoft.udacity.backend.joke.myApi.MyApi;

import java.io.IOException;

public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private final String LOG_TAG = EndpointsAsyncTask.class.getSimpleName();
    private MyApi myApiService = null;
    public Context mContext;
    public PostExecuteCallbacks mCallback;
    @Override
    protected String doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(mContext.getString(R.string.joke_server_url))
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
            Log.d(LOG_TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (mCallback != null)
            mCallback.AsyncTaskPostCallbackResult(result);
    }
}
