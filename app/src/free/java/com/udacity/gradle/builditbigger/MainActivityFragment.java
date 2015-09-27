package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


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

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.LoadingProgressBar);
        progressBar.setVisibility(View.GONE);
        if ( progressBar != null)
            mCallbacks.setProgressBar(progressBar);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
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
