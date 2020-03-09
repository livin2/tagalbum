package com.dhu777.tagalbum.data.persistent.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 存入数据库的图片数据的实体POJO.通过ROOM做ORM.
 */
@Entity
public class MediaInfo {
    @PrimaryKey
    @NonNull
    private long id; //id from mediaStore
    @NonNull
    private String name;
    @NonNull
    private int mimeType;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getMimeType() {
        return mimeType;
    }
    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }
}
