package com.dji.uavapp.bean;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;
import androidx.room.RoomWarnings;

import com.dji.uavapp.bean.Flight;
import com.dji.uavapp.bean.User;

import java.util.List;

public class UserWithFlight {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userID",
            entityColumn = "FlightUserID"
    )
    public List<Flight> FlightList;
}
