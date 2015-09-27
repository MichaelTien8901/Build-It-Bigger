/**
 * Created by Michael Tien on 2015/9/26.
 */
package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.LoadingProgressBar);
        progressBar.setVisibility(View.GONE);
        if ( progressBar != null)
            mCallbacks.setProgressBar(progressBar);
        return root;
    }
    private ProgressBarCallbacks mCallbacks = sDummyCallbacks;
    public interface ProgressBarCallbacks {
        public void setProgressBar(ProgressBar progressBar);
    }
    private static ProgressBarCallbacks sDummyCallbacks = new ProgressBarCallbacks() {
        @Override
        public void setProgressBar(ProgressBar progressBar) {
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof ProgressBarCallbacks)) {
            //throw new IllegalStateException("Activity must implement fragment's callbacks.");
            return;
        }
        mCallbacks = (ProgressBarCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
}
