package me.bromen.podgo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import me.bromen.podgo.R;
import me.bromen.podgo.activities.MainActivity;

/**
 * Created by jeff on 6/1/17.
 */

public class AddNewFeedFragment extends Fragment {

    public static final String ADD_NEW_FEED_TAG = "add_new_feed";

    private Toolbar mToolbar;
    private EditText feedUrlEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_feed, container, false);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.add_new_feed);

        Button searchItunesButton = (Button) rootView.findViewById(R.id.search_itunes_button);

        searchItunesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItunesSearchFragment fragment = new ItunesSearchFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_main, fragment, ItunesSearchFragment.ITUNES_SEARCH_TAG);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        feedUrlEditText = (EditText) rootView.findViewById(R.id.feed_url_edittext);

        Button feedUrlButton = (Button) rootView.findViewById(R.id.feed_url_button);

        feedUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedUrl = feedUrlEditText.getText().toString();

                if (feedUrl.trim().length() == 0) {
                    feedUrlEditText.setText("");
                    return;
                }

                if (!feedUrl.startsWith("http://") && !feedUrl.startsWith("https://")) {
                    feedUrl = "http://" + feedUrl;
                }

                ((MainActivity) getActivity()).downloadPodcastXml(feedUrl, false);

                getFragmentManager().popBackStackImmediate();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }
}
