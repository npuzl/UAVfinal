package com.dji.uavapp.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Flight {
    @PrimaryKey
    public int FlightID;
    public String FlightUserID;
    public String FlightStartTime;
    public String FlightEndTime;
    public String Path;
    public String PathMedium;
}
