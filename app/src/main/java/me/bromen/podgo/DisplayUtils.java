package me.bromen.podgo;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by jeff on 5/13/17.
 */

public class DisplayUtils {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float dpWidth = dm.widthPixels / dm.density;
        return (int) dpWidth / 125;
    }

    public static int calculateNoOfColumns(Context context, int minNoOfColumns) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float dpWidth = dm.widthPixels / dm.density;
        int numColumns = (int) dpWidth / 125;
        return numColumns > 3 ? numColumns : 3;
    }
}
