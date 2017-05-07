package io.github.jabromen.podgo;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import org.dom4j.DocumentHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NewPodcastFragment.OnDataPass {

    private PodcastList podcastList;
    private ListView podcastListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Load saved podcast info from files
        podcastList = new PodcastList();
        podcastList.loadPodcastInfo(this);

        // Set up listview displaying podcasts
        ListAdapter listAdapter = new PodcastListAdapter(this, podcastList);
        podcastListView = (ListView) findViewById(R.id.podcastListMain);
        podcastListView.setAdapter(listAdapter);
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

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Number of podcasts: " + podcastList.size(), Toast.LENGTH_SHORT).show();

            return true;
        }
        else if (id == R.id.action_new_podcast) {
            // Bring up new podcast dialog fragment
            NewPodcastFragment fragment = new NewPodcastFragment();
            fragment.show(getFragmentManager(), "Dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // What to do when data is received from new podcast dialog fragment
    @Override
    public void onDataPass(String data) {
        addNewPodcast(data);
    }

    //
    public void addNewPodcast(String feed) {
        // Toast used for debugging
        Toast.makeText(getApplicationContext(), "Received URL: " + feed, Toast.LENGTH_SHORT).show();

        new RetrievePodcastXmlTask().execute(feed);
    }

    // Helper class to asynchronously download XML feed from URL and build Podcast object
    private class RetrievePodcastXmlTask extends AsyncTask<String, Void, Podcast> {
        @Override
        protected Podcast doInBackground(String... params) {
            try {
                return new Podcast(new URL(params[0]));
            } catch (MalformedFeedException | MalformedURLException | InvalidFeedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Podcast podcast) {
            if (podcast != null) {
                podcastList.add(podcast);
                PodcastSaver.savePodcastInfo(getApplicationContext(), podcast);
                ((BaseAdapter) podcastListView.getAdapter()).notifyDataSetChanged();
            }
        }
    }
}
