package me.bromen.podgo.utilities;

import android.content.Context;
import android.os.Environment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import me.bromen.podgo.extras.utilities.FileUtils;

/**
 * Created by jeff on 7/15/17.
 */

public class FileUtilsTest {

    private String dirtyName = "/first+&last?";
    private String cleanName = "_first__last_";

    private File file;
    private String fileExists = "fileUtilsTestTemp.txt";
    private String fileDoesntExist = "nonExistentFile.txt";

    private Context context;
    private File externalStorage = new File("/podcasts");
    private String filename = "episode.mp3";
    private String fullpath = externalStorage + "/" + filename;

    @Before
    public void createFile() throws Exception {
        file = new File(fileExists);
        file.createNewFile();
    }

    @Before
    public void mockContext() throws Exception {
        context = Mockito.mock(Context.class);
        Mockito.when(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS))
                .thenReturn(externalStorage);
    }

    @After
    public void deleteFile() throws Exception {
        file.delete();
    }

    @Test
    public void sanitizeNameTest() throws Exception {
        Assert.assertEquals(cleanName, FileUtils.sanitizeName(dirtyName));
    }

    @Test
    public void fileExistsTest() throws Exception {
        Assert.assertTrue(FileUtils.fileExists(fileExists));
        Assert.assertFalse(FileUtils.fileExists(fileDoesntExist));
    }

    @Test
    public void getFullAudioPathTest() throws Exception {
        Assert.assertEquals(fullpath, FileUtils.getFullAudioFilePath(context, filename));
    }
}
