package com.dhu777.tagalbum.data.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 相簿对象POJO,{@link AlbumItem}的容器类.
 */
public class AlbumBucket {
    private static final int HIDDEN_UNKNOWN = -1;
    private static final int HIDDEN_FALSE = 0;
    private static final int HIDDEN_TURE = 1;

    private List<AlbumItem> albumItems;

    private String title;
    private long bucketId;//parent BUCKET_ID OR Tag_ID

    private int hidden = HIDDEN_UNKNOWN;


    public AlbumBucket() {
        albumItems = new ArrayList<>();
    }

    public List<AlbumItem> getAlbumItems() {
        return albumItems;
    }

    public String getTitle() {
        return title;
    }

    public AlbumBucket setTitle(String title) {
        this.title = title;
        return this;
    }
    public long getBucketId() {
        return bucketId;
    }
    public AlbumBucket setBucketId(long bucketId) {
        this.bucketId = bucketId;
        return this;
    }
}
