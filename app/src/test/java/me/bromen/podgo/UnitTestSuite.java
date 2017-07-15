package me.bromen.podgo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import me.bromen.podgo.presenters.FeedDetailPresenterTest;
import me.bromen.podgo.presenters.HomePresenterTest;
import me.bromen.podgo.presenters.ItunesSearchPresenterTest;
import me.bromen.podgo.presenters.MediaControlPresenterTest;
import me.bromen.podgo.presenters.MediaplayerBarPresenterTest;
import me.bromen.podgo.presenters.NewFeedPresenterTest;
import me.bromen.podgo.utilities.DisplayUtilsTest;
import me.bromen.podgo.utilities.FileUtilsTest;
import me.bromen.podgo.utilities.TimeUtilsTest;

/**
 * Created by jeff on 7/14/17.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({FeedDetailPresenterTest.class,
        HomePresenterTest.class,
        ItunesSearchPresenterTest.class,
        MediaControlPresenterTest.class,
        MediaplayerBarPresenterTest.class,
        NewFeedPresenterTest.class,
        TimeUtilsTest.class,
        FileUtilsTest.class,
        DisplayUtilsTest.class})
public class UnitTestSuite {
}
