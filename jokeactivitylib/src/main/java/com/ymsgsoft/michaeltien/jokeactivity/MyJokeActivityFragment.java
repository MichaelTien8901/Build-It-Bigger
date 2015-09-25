package com.ymsgsoft.michaeltien.jokeactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyJokeActivityFragment extends Fragment {

    public MyJokeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_joke, container, false);
        Bundle arguments = getArguments();
        if (arguments == null) return rootView;
        String joke = arguments.getString(MyJokeActivity.JOKE_KEY);
        TextView joke_text = (TextView) rootView.findViewById(R.id.joke_text_view);
        joke_text.setText(joke);
        return rootView;
    }
}
