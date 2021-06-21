package com.dji.uavapp.bean;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;
import androidx.room.RoomWarnings;

import java.util.List;

public class UserWithGallery {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userID",
            entityColumn = "GalleryUserID"
    )
    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
    public List<Gallery> GalleryList;
}
