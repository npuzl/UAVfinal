package com.dji.uavapp.db.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dji.uavapp.bean.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * from user")
    List<User> getAll();

    @Query("SELECT * from user where user.userID==:id")
    User getInfo(String id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
