package com.dji.uavapp.bean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    // 账号id
    @NonNull
    @PrimaryKey
    public String userID;
    // 昵称

    public  String nickName;
    // 飞行总里程

    public  int FlightSumDistance;
    // 飞行总时间

    public  int FlightSumTime;

    public User() {
    }

    public User(String id, String nn, int fsd, int fst) {
        this.userID = id;
        this.nickName = nn;
        this.FlightSumDistance = fsd;
        this.FlightSumTime = fst;

    }

    public int getFlightSumDistance() {
        return FlightSumDistance;
    }

    public int getFlightSumTime() {
        return FlightSumTime;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserID() {
        return userID;
    }

    public void setFlightSumDistance(int flightSumDistance) {
        FlightSumDistance = flightSumDistance;
    }

    public void setFlightSumTime(int flightSumTime) {
        FlightSumTime = flightSumTime;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
