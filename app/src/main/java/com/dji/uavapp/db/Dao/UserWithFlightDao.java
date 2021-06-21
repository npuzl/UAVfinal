package com.dji.uavapp.db.Dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dji.uavapp.bean.UserWithFlight;

import java.util.List;
@Dao
public interface UserWithFlightDao {
    @Transaction
    @Query("SELECT * FROM USER")
    public List<UserWithFlight> getUserWithFlightList();

}
