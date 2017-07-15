package me.bromen.podgo.utilities;

import org.junit.Assert;
import org.junit.Test;

import me.bromen.podgo.extras.utilities.TimeUtils;

/**
 * Created by jeff on 7/14/17.
 */

public class TimeUtilsTest {

    private int msec = 1000 * 60 * 100;
    private String hms = "1:40:00";

    private int msec2 = 0;
    private String hms2 = "0:00:00";

    private int msec3 = 1000 * 60 * 35 + 1000 * 56;
    private String hms3 = "0:35:56";

    @Test
    public void msecToHMSTest() {
        Assert.assertEquals(hms, TimeUtils.msecToHMS(msec));
        Assert.assertEquals(hms2, TimeUtils.msecToHMS(msec2));
        Assert.assertEquals(hms3, TimeUtils.msecToHMS(msec3));
    }
}
