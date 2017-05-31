package me.bromen.podgo.structures;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jeff on 5/31/17.
 */

public class FeedList implements List<Feed>, Serializable {

    private List<Feed> feeds = new ArrayList<>();

    public FeedList() {}

    public FeedList(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public FeedList(Cursor cursor) {
        while (cursor.moveToNext()) {
            add(new Feed(cursor));
        }
    }

    @Override
    public int size() {
        return feeds.size();
    }

    @Override
    public boolean isEmpty() {
        return feeds.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return feeds.contains(o);
    }

    public boolean contains(long id) {
        for (Feed feed: feeds) {
            if (feed.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String title) {
        for (Feed feed: feeds) {
            if (title.equals(feed.getTitle())) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Iterator<Feed> iterator() {
        return feeds.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return feeds.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return (T[]) feeds.toArray(a);
    }

    @Override
    public boolean add(Feed feed) {
        return feeds.add(feed);
    }

    @Override
    public boolean remove(Object o) {
        return feeds.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return feeds.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Feed> c) {
        return feeds.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Feed> c) {
        return feeds.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return feeds.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return feeds.retainAll(c);
    }

    @Override
    public void clear() {
        feeds.clear();
    }

    @Override
    public Feed get(int index) {
        return feeds.get(index);
    }

    public Feed get(String title) {
        for (Feed feed: feeds) {
            if (title.equals(feed.getTitle())) {
                return feed;
            }
        }
        return null;
    }

    public Feed getFromId(long podcastId) {
        for (Feed feed: feeds) {
            if (podcastId == feed.getId()) {
                return feed;
            }
        }
        return null;
    }

    @Override
    public Feed set(int index, Feed element) {
        return feeds.set(index, element);
    }

    @Override
    public void add(int index, Feed element) {
        feeds.add(index, element);
    }

    @Override
    public Feed remove(int index) {
        return feeds.remove(index);
    }

    public Feed remove(String title) {
        for (int i = 0; i < feeds.size(); i++) {
            if (title.equals(feeds.get(i).getTitle())) {
                return feeds.remove(i);
            }
        }
        return null;
    }

    public Feed removeFromId(long id) {
        for (int i = 0; i < feeds.size(); i++) {
            if (id == feeds.get(i).getId()) {
                return feeds.remove(i);
            }
        }
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return feeds.indexOf(o);
    }

    public int indexOf(String title) {
        for (int i = 0; i < feeds.size(); i++) {
            if (title.equals(feeds.get(i).getTitle())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return feeds.lastIndexOf(o);
    }

    @Override
    public ListIterator<Feed> listIterator() {
        return feeds.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Feed> listIterator(int index) {
        return feeds.listIterator(index);
    }

    @NonNull
    @Override
    public List<Feed> subList(int fromIndex, int toIndex) {
        return feeds.subList(fromIndex, toIndex);
    }
}
