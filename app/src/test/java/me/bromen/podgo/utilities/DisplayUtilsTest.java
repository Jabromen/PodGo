package me.bromen.podgo.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import me.bromen.podgo.extras.utilities.DisplayUtils;

/**
 * Created by jeff on 7/15/17.
 */

public class DisplayUtilsTest {

    private Context context;
    private Resources resources;
    private DisplayMetrics dm;

    @Before
    public void mockContext() throws Exception {
        context = Mockito.mock(Context.class);
        resources = Mockito.mock(Resources.class);
        dm = Mockito.mock(DisplayMetrics.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(resources.getDisplayMetrics()).thenReturn(dm);
    }

    @Test
    public void calculateNoOfColumnsTest() throws Exception {
        dm.widthPixels = 1000;
        dm.density = 3;
        Assert.assertEquals(3, DisplayUtils.calculateNoOfColumns(context, 100));
    }

    @Test
    public void calculateNoOfColumnsMinValTest() throws Exception {
        dm.widthPixels = 1000;
        dm.density= 3;
        Assert.assertEquals(5, DisplayUtils.calculateNoOfColumns(context, 100, 5));
    }
}
