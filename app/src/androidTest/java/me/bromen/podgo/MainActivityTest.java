package me.bromen.podgo;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.bromen.podgo.activities.home.MainActivity;
import me.bromen.podgo.app.storage.DbHelper;
import me.bromen.podgo.extras.structures.Feed;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jeff on 7/15/17.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void prepareDatabase() {
        Feed feed = new Feed();
        feed.setTitle("MainActivityTest");
        feed.setFeedUrl("mainActivityTest.com");
        new DbHelper(InstrumentationRegistry.getTargetContext()).saveFeed(feed);
        mActivityRule.launchActivity(null);
    }

    @After
    public void clearDatabase() {
        DbHelper dbHelper = new DbHelper(InstrumentationRegistry.getTargetContext());
        dbHelper.deleteFeed(dbHelper.getFeedId("mainActivityTest.com"));
    }

    @Test
    public void clickFeedImage() throws Exception {
        onView(withId(R.id.recycler_main)).perform(RecyclerViewActions.actionOnItemAtPosition(0, RecyclerMainActions.clickFeedImage()));
        onView(withId(R.id.toolbar_feeddetail)).check(matches(isDisplayed()));
    }

    @Test
    public void clickFeedOptions() throws Exception {
        onView(withId(R.id.recycler_main)).perform(RecyclerViewActions.actionOnItemAtPosition(0, RecyclerMainActions.clickFeedOptions()));
        onView(withText(InstrumentationRegistry.getTargetContext()
                .getString(R.string.refresh_feed))).check(matches(isDisplayed()));
        onView(withText(InstrumentationRegistry.getTargetContext()
                .getString(R.string.delete_feed))).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddNewFeed() throws Exception {
        onView(withId(R.id.action_new_podcast)).perform(click());
        onView(withId(R.id.toolbar_newfeed)).check(matches(isDisplayed()));
    }
}
