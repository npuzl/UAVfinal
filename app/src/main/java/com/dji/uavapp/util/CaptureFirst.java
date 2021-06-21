package com.dji.uavapp.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class CaptureFirst {

    public  static Bitmap getVideoThumb(String path) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();

        media.setDataSource(path);

        return  media.getFrameAtTime();

    }
}