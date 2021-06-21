package com.dji.uavapp.UI.my;

import android.app.Activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dji.uavapp.R;

//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;


public class ImgTestActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgtest);
        //System.loadLibrary("opencv_java4");
        imageView = (ImageView) findViewById(R.id.imageViewTest);
        //iniLoadOpencv();
        //processImg();
    }

//    private void processImg() {
//        // 这一行可以更改成 BitmapFactory.decodeFile 来加载文件中的图片
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.fire);
//
//        Mat src = new Mat();
//        Mat des = new Mat();
//        Utils.bitmapToMat(bitmap, src);
//        Imgproc.cvtColor(src, des, Imgproc.COLOR_BGR2GRAY);
//        Utils.matToBitmap(des, bitmap);
//        imageView.setImageBitmap(bitmap);
//        src.release();
//        des.release();
//    }
//
//    private void iniLoadOpencv() {
//        boolean success = OpenCVLoader.initDebug();
//        if (success) {
//            showToast("Success load opencv");
//        } else {
//            showToast("load opencv failed");
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
