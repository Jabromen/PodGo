package me.bromen.podgo;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by jeff on 7/15/17.
 */

public class RecyclerMainActions {

    private static class ClickFeedImage implements ViewAction {

        ViewAction click = click();

        @Override
        public Matcher<View> getConstraints() {
            return click.getConstraints();
        }

        @Override
        public String getDescription() {
            return " click on feed image";
        }

        @Override
        public void perform(UiController uiController, View view) {
            click.perform(uiController, view.findViewById(R.id.podcastImageListItem));
        }
    }

    private static class ClickFeedOptions implements ViewAction {

        ViewAction click = click();

        @Override
        public Matcher<View> getConstraints() {
            return click.getConstraints();
        }

        @Override
        public String getDescription() {
            return " click feed options";
        }

        @Override
        public void perform(UiController uiController, View view) {
            click.perform(uiController, view.findViewById(R.id.podcastOptionListItem));
        }
    }

    public static ClickFeedImage clickFeedImage() {
        return new ClickFeedImage();
    }

    public static ClickFeedOptions clickFeedOptions() {
        return new ClickFeedOptions();
    }
}
