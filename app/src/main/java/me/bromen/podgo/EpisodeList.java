package me.bromen.podgo;

import android.support.annotation.NonNull;

import com.icosillion.podengine.models.Episode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jeff on 5/19/17.
 */

public class EpisodeList implements List<Episode> {

    private List<Episode> episodeList = new ArrayList<>();

    @Override
    public int size() {
        return episodeList.size();
    }

    @Override
    public boolean isEmpty() {
        return episodeList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return episodeList.contains(o);
    }

    @NonNull
    @Override
    public Iterator<Episode> iterator() {
        return episodeList.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return episodeList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    @Override
    public boolean add(Episode episode) {
        return episodeList.add(episode);
    }

    @Override
    public boolean remove(Object o) {
        return episodeList.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return episodeList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Episode> c) {
        return episodeList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Episode> c) {
        return episodeList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return episodeList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return episodeList.retainAll(c);
    }

    @Override
    public void clear() {
        episodeList.clear();
    }

    @Override
    public Episode get(int index) {
        return episodeList.get(index);
    }

    @Override
    public Episode set(int index, Episode element) {
        return episodeList.set(index, element);
    }

    @Override
    public void add(int index, Episode element) {
        episodeList.add(index, element);
    }

    @Override
    public Episode remove(int index) {
        return episodeList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return episodeList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return episodeList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Episode> listIterator() {
        return episodeList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Episode> listIterator(int index) {
        return episodeList.listIterator(index);
    }

    @NonNull
    @Override
    public List<Episode> subList(int fromIndex, int toIndex) {
        return episodeList.subList(fromIndex, toIndex);
    }
}
