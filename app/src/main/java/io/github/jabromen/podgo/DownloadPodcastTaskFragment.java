package io.github.jabromen.podgo;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jeff on 5/7/17.
 */

public class DownloadPodcastTaskFragment extends Fragment {

    interface TaskCallbacks {
        void onPreExecute();
        void onCancelled(String message);
        void onPostExecute(Podcast podcast);
    }

    private TaskCallbacks mCallbacks;
    private DownloadPodcastTask mTask;
    private Fragment thisFragment = this;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (TaskCallbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        String feed = getArguments().getString("URL");

        mTask = new DownloadPodcastTask();
        mTask.execute(feed);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class DownloadPodcastTask extends AsyncTask<String, Void, Podcast> {

        private String message = "";

        @Override
        protected Podcast doInBackground(String... params) {
            try {
                return new Podcast(new URL(params[0]));

            } catch (MalformedFeedException e) {
                e.printStackTrace();
                message = "Error: Malformed feed";
                cancel(true);
            } catch (InvalidFeedException e) {
                e.printStackTrace();
                message = "Error: Invalid feed";
                cancel(true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                message = "Error: Invalid URL";
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(Podcast podcast) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(podcast);
            }
            getActivity().getFragmentManager().beginTransaction().remove(thisFragment).commit();
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled(message);
            }
            getActivity().getFragmentManager().popBackStack();
            getActivity().getFragmentManager().beginTransaction().remove(thisFragment).commit();
        }
    }
}
