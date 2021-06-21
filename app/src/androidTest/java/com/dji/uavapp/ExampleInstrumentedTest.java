package com.dji.uavapp;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dji.uavapp.bean.User;
import com.dji.uavapp.db.AppDataBase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.Core;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.dji.uavapp", appContext.getPackageName());

        Log.e("Core", Core.VERSION);
    }

    @Test
    public void dbTest() {
        AppDataBase.getInstance(null).userDao().addUser(new User("1", "2", 3, 4));
    }
}