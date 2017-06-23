package me.bromen.podgo.extras.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by jeff on 5/13/17.
 */

public class DisplayUtils {

    /**
     * Calculates the number of items of a certain width that can fit side by side on the screen
     * @param context   - Activity context
     * @param itemWidth - Width of items in dp
     * @return          - Number of columns that fit on screen
     */
    public static int calculateNoOfColumns(Context context, int itemWidth) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float dpWidth = dm.widthPixels / dm.density;
        return (int) dpWidth / itemWidth;
    }

    /**
     * Calculates the number of items of a certain width that can fit side by side on the screen.
     * Includes a minimum number of columns.
     * @param context        - Acivity context
     * @param itemWidth      - Width of items in dp
     * @param minNoOfColumns - Minimum number of columns
     * @return               - Number of columns that fit on screen
     */
    public static int calculateNoOfColumns(Context context, int itemWidth, int minNoOfColumns) {
        int numColumns = calculateNoOfColumns(context, itemWidth);
        return numColumns > minNoOfColumns ? numColumns : minNoOfColumns;
    }
}
