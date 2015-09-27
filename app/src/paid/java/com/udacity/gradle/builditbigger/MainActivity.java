package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ymsgsoft.michaeltien.jokeactivity.MyJokeActivity;


public class MainActivity extends AppCompatActivity implements
        MainActivityFragment.ProgressBarCallbacks,
        PostExecuteCallbacks {
    private ProgressBar mProgressBar;
    private String newJoke;
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
        task.mContext = this;
        task.execute();
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
        if ( result != null) {
            newJoke = result;
            showNewJoke();
        } else {
            Toast.makeText(this, "Joke Server is down", Toast.LENGTH_LONG).show();
        }
    }
}


