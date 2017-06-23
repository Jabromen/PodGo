package me.bromen.podgo.activities.itunessearch.mvp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.bromen.podgo.ext.structures.ItunesPodcast;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/22/17.
 */

public class ItunesApiService {

    private static final String ITUNES_SEARCH_URL = "https://itunes.apple.com/search?media=podcast&term=%s";

    private OkHttpClient okHttpClient;

    public ItunesApiService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public List<ItunesPodcast> query(String query) {

        List<ItunesPodcast> searchResults = new ArrayList<>();

        String queryUrl = String.format(ITUNES_SEARCH_URL, query).replace(' ', '+');

        Request request = new Request.Builder().url(queryUrl).build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject results = new JSONObject(response.body().string());
                JSONArray resultsList = results.getJSONArray("results");

                for (int i = 0; i < resultsList.length(); i++) {
                    JSONObject jsonPodcast = resultsList.getJSONObject(i);
                    searchResults.add(new ItunesPodcast(jsonPodcast));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return searchResults;
    }
}
