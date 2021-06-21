package com.dji.uavapp.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Gallery {
    @PrimaryKey
    public int GalleryId;
    public String GalleryUserID;
    public String type;
    public int size;
    public String location;
    public String time;
}
