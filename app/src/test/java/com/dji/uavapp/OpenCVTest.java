package com.dji.uavapp;

import android.graphics.Bitmap;
import android.util.Log;

import org.junit.Test;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgcodecs.*;

public class OpenCVTest {


    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 得保证先执行该语句，用于加载库，才能调用其他操作库的语句，
    }

    @Test
    public void testLoginReceiveAndSend() throws Exception {
        String pic = "7";
        String picpath = "C:\\object_detection_demo_flow\\data\\images\\train\\" + pic + ".png";
        Mat m = Imgcodecs.imread(picpath, Imgcodecs.IMREAD_COLOR);

        System.out.println(m.cols());
        System.out.println(m.rows());
        System.out.println(m);
        findFire(m);
//        Mat mat = new Mat();
//        mat.put(1, 1, 1,2,3,4,5);
    }

    private Bitmap findFire(Mat mat) {

        Imgcodecs.imwrite("C:\\Users\\zl\\Desktop\\1.png",mat);
        return null;
    }
}
