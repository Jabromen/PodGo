package me.bromen.podgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jeff on 5/14/17.
 */

public class FileTarget extends SimpleTarget<Bitmap> {

    private String filename;
    private Bitmap.CompressFormat format;
    private int quality;

    private boolean setView;
    private Context context;
    private ImageView imageView;
    private int viewWidth;
    private int viewHeight;

    public FileTarget(String filename, int width, int height) {
        this(filename, width, height, Bitmap.CompressFormat.JPEG, 70);
        setView = false;
    }

    public FileTarget(String filename, int width, int height,
                      Context context, ImageView imageView, int viewWidth, int viewHeight) {
        this(filename, width, height, Bitmap.CompressFormat.JPEG, 70);
        this.context = context;
        this.imageView = imageView;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        setView = true;
    }

    public FileTarget(String filename, int width, int height, Bitmap.CompressFormat format, int quality) {
        super(width, height);
        this.filename = filename;
        this.format = format;
        this.quality = quality;
        setView = false;
    }

    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
            onFileSaved();
        } catch (IOException e) {
            e.printStackTrace();
            onSaveException(e);
        }
    }

    private void onFileSaved() {
        if (setView) {
            Glide.with(context)
                    .load(new File(filename))
                    .asBitmap()
                    .override(viewWidth, viewHeight)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void onSaveException(Exception e) {

    }
}
