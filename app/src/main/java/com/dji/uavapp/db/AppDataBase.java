package com.dji.uavapp.db;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dji.uavapp.bean.Flight;
import com.dji.uavapp.bean.Gallery;
import com.dji.uavapp.bean.User;
import com.dji.uavapp.bean.UserWithFlight;
import com.dji.uavapp.constant.Constant;
import com.dji.uavapp.db.Dao.FlightDao;
import com.dji.uavapp.db.Dao.GalleryDao;
import com.dji.uavapp.db.Dao.UserDao;
import com.dji.uavapp.db.Dao.UserWithFlightDao;
import com.dji.uavapp.db.Dao.UserWithGalleryDao;

@Database(entities = {User.class, Gallery.class, Flight.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract FlightDao flightDao();

    public abstract GalleryDao galleryDao();

    public abstract UserWithFlightDao userWithFlightDao();

    public abstract UserWithGalleryDao userWithGalleryDao();

    private static AppDataBase appDataBase;

    public static AppDataBase getInstance(Context context) {
        if (appDataBase == null) {
            synchronized (AppDataBase.class) {
                if (appDataBase == null)
                    appDataBase = Room.databaseBuilder(context, AppDataBase.class, "app.db").build();
            }
        }
        return appDataBase;
    }
}
