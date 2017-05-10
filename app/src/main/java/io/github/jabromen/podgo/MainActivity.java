package io.github.jabromen.podgo;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity
        implements NewPodcastFragment.OnDataPass, DownloadPodcastTaskFragment.TaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private DownloadPodcastTaskFragment mTaskFragment;

    private PodcastList podcastList;
    private ListView podcastListView;

    private boolean isDownloadingXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        // Load saved podcast info from files
        podcastList = new PodcastList();
        podcastList.loadPodcastInfo(this);

        // Find download task fragment if exists
        mTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);

        isDownloadingXml = mTaskFragment != null;

        // Set up listview displaying podcasts
        ListAdapter listAdapter = new PodcastListAdapter(this, podcastList);
        podcastListView = (ListView) findViewById(R.id.podcastListMain);
        podcastListView.setAdapter(listAdapter);

        podcastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Temp fix to avoid crashing when changing activity while download task is running
                if (isDownloadingXml) {
                    return;
                }

                Podcast selectedPodcast = (Podcast) parent.getAdapter().getItem(position);

                try {
                    Intent podActivity = new Intent(getApplicationContext(), PodcastAcitivity.class);
                    podActivity.putExtra("TITLE", selectedPodcast.getTitle());

                    startActivity(podActivity);

                } catch (MalformedFeedException e) {
                    e.printStackTrace();
                }
            }
        });
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

        if (feed == null)
            return;

        // Find download task fragment if exists
        mTaskFragment = (DownloadPodcastTaskFragment)
                getFragmentManager().findFragmentByTag(TAG_TASK_FRAGMENT);

        // If doesn't exist, start new download task fragment
        if (mTaskFragment == null) {

            mTaskFragment = new DownloadPodcastTaskFragment();

            // Pass feed URL to task
            Bundle bundle = new Bundle();
            bundle.putString("URL", feed);
            mTaskFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        else {
            Toast.makeText(this, "Other podcast info being downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPreExecute() {
        isDownloadingXml = true;
        Toast.makeText(this, "Downloading Podcast Info", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelled(String message) {
        isDownloadingXml = false;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute(Podcast podcast) {
        isDownloadingXml = false;

        if (podcast != null) {
            try {
                if (!podcastList.contains(podcast.getTitle())) {
                    podcastList.add(podcast);
                    PodcastSaver.savePodcastInfo(getApplicationContext(), podcast);
                    ((BaseAdapter) podcastListView.getAdapter()).notifyDataSetChanged();
                    Toast.makeText(this, "Added " + podcast.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, podcast.getTitle() + " Already Saved", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedFeedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: Malformed Feed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
