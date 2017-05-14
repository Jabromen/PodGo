package me.bromen.podgo;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jeff on 5/10/17.
 */

public class DownloadImageTaskFragment extends Fragment {

    interface  TaskCallBacks {
        void onPreExecuteImage();
        void onCancelledImage(String message);
        void onPostExecuteImage(Bitmap bitmap, String podcastTitle);
    }

    private TaskCallBacks mCallbacks;
    private DownloadImageTask mTask;
    private Fragment mFragment = this;
    private String podcastTitle;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (TaskCallBacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        String url = getArguments().getString("URL");
        podcastTitle = getArguments().getString("TITLE");

        mTask = new DownloadImageTask();
        mTask.execute(url);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private String message = "";

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            URLConnection ic = null;
            InputStream is = null;
            try {
                URL url = new URL(params[0]);
                ic = url.openConnection();
                is = ic.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                message = "Malformed URL";
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
                message = "I//O Error";
                cancel(true);
            } finally {
                IOUtils.close(ic);
                IOUtils.closeQuietly(is);
            }

            if (bitmap == null) {
                message = "Can't Retrieve Image";
                cancel(true);
            }

            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecuteImage();
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecuteImage(bitmap, podcastTitle);
            }
            getActivity().getFragmentManager().beginTransaction().remove(mFragment).commit();
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelledImage(message);
            }
            getActivity().getFragmentManager().beginTransaction().remove(mFragment).commit();
        }
    }
}
