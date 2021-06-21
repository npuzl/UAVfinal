package com.dji.uavapp.db.Dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dji.uavapp.bean.UserWithGallery;

import java.util.List;

@Dao
public interface UserWithGalleryDao {
    @Transaction
    @Query("SELECT * FROM USER")
    public List<UserWithGallery> getUserWithGalleryList();

}
