package io.github.jabromen.podgo;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jeff on 5/6/17.
 */

public class PodcastList implements List<Podcast> {

    private List<Podcast> podcastList = new ArrayList<Podcast>();

    void loadPodcastInfo(Context context) {

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), "");
        // Get List of child directories
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        // Loop through subdirectories and build podcast objects if correct files are there
        for (String dir : directories) {
            File xmlFile = new File(file.getPath() + "/" + dir + "/feed.xml");
            File urlFile = new File(file.getPath() + "/" + dir + "/url.txt");
            if (!xmlFile.exists() || !urlFile.exists())
                continue;
            try {
                String xml = FileUtils.readFileToString(xmlFile, Charset.forName("UTF-8"));
                String url = FileUtils.readFileToString(urlFile, Charset.forName("UTF-8"));

                podcastList.add(new Podcast(xml, new URL(url)));
            } catch (IOException | MalformedFeedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int size() {
        return podcastList.size();
    }

    @Override
    public boolean isEmpty() {
        return podcastList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return podcastList.contains(o);
    }

    public boolean contains(String title) {
        try {
            for (Podcast p : podcastList) {
                if (p.getTitle().equals(title)) {
                    return true;
                }
            }
        } catch (MalformedFeedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @NonNull
    @Override
    public Iterator<Podcast> iterator() {
        return podcastList.iterator();
    }

    @NonNull
    @Override
    public Podcast[] toArray() {
        Podcast[] array = new Podcast[podcastList.size()];
        for (int i = 0; i < podcastList.size(); i++) {
            array[i] = podcastList.get(i);
        }
        return array;
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    @Override
    public boolean add(Podcast podcast) {
        return podcastList.add(podcast);
    }

    @Override
    public boolean remove(Object o) {
        return podcastList.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return podcastList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Podcast> c) {
        return podcastList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Podcast> c) {
        return podcastList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return podcastList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return podcastList.retainAll(c);
    }

    @Override
    public void clear() {
        podcastList.clear();
    }

    @Override
    public Podcast get(int index) {
        return podcastList.get(index);
    }

    @Override
    public Podcast set(int index, Podcast element) {
        return podcastList.set(index, element);
    }

    @Override
    public void add(int index, Podcast element) {
        podcastList.add(index, element);
    }

    @Override
    public Podcast remove(int index) {
        return podcastList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return podcastList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return podcastList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Podcast> listIterator() {
        return podcastList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Podcast> listIterator(int index) {
        return podcastList.listIterator(index);
    }

    @NonNull
    @Override
    public List<Podcast> subList(int fromIndex, int toIndex) {
        return podcastList.subList(fromIndex, toIndex);
    }
}
