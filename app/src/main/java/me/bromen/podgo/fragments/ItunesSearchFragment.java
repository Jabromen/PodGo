package me.bromen.podgo.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.bromen.podgo.R;
import me.bromen.podgo.activities.MainActivity;
import me.bromen.podgo.adapters.ItunesRecyclerAdapter;
import me.bromen.podgo.structures.ItunesPodcast;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/1/17.
 */

public class ItunesSearchFragment extends Fragment {

    public static final String TAG = "itunes_search";
    private static final String ITUNES_SEARCH_URL = "https://itunes.apple.com/search?media=podcast&term=%s";

    private Toolbar mToolbar;
    private SearchView searchView;
    private RecyclerView searchResultsView;
    private RecyclerView.Adapter searchAdapter;

    private JSONArray resultsList;
    private List<ItunesPodcast> podcastList = new ArrayList<>();;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            try {
                resultsList = new JSONArray(savedInstanceState.getString("RESULTS"));

                for (int i = 0; i < resultsList.length(); i++) {
                    JSONObject jsonPodcast = resultsList.getJSONObject(i);
                    podcastList.add(new ItunesPodcast(jsonPodcast));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itunes_search, container, false);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        mToolbar.setTitle(R.string.search_itunes);

        searchResultsView = (RecyclerView) rootView.findViewById(R.id.itunes_search_recyclerview);
        setUpSearchResultsView();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mToolbar != null) {
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_itunes_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.itunes_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("RESULTS", resultsList != null ? resultsList.toString() : "[]");
    }

    private void setUpSearchResultsView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        searchResultsView.setLayoutManager(layoutManager);
        searchResultsView.setHasFixedSize(true);

        searchAdapter = new ItunesRecyclerAdapter(podcastList);
        searchResultsView.setAdapter(searchAdapter);
    }

    private void search(String query) {
        String searchUrl = String.format(ITUNES_SEARCH_URL, query).replace(' ', '+');

        new ItunesSearchTask().execute(searchUrl);
    }

    private class ItunesSearchTask extends AsyncTask<String, Void, List<ItunesPodcast>> {
        @Override
        protected List<ItunesPodcast> doInBackground(String... searchUrl) {
            OkHttpClient httpClient = new OkHttpClient();
            Request.Builder httpRequest = new Request.Builder().url(searchUrl[0]);

            List<ItunesPodcast> searchList = new ArrayList<>();
            try {
                Response response = httpClient.newCall(httpRequest.build()).execute();

                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    JSONObject results = new JSONObject(responseString);
                    resultsList = results.getJSONArray("results");

                    for (int i = 0; i < resultsList.length(); i++) {
                        JSONObject jsonPodcast = resultsList.getJSONObject(i);
                        searchList.add(new ItunesPodcast(jsonPodcast));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return searchList;
        }

        @Override
        protected void onPostExecute(List<ItunesPodcast> searchList) {
            podcastList = new ArrayList<>();
            podcastList.addAll(searchList);
            ((ItunesRecyclerAdapter) searchAdapter).updateList(podcastList);
        }
    }
}
