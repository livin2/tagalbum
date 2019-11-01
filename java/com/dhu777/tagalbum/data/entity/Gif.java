package com.dhu777.tagalbum.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.dhu777.tagalbum.util.AlbumItemInfo;

/**
 * 抽象类{@link AlbumItem}的实现之一,该类型的元素在加载入视图时作为动图加载.
 */
public class Gif extends AlbumItem implements Parcelable {
    protected Gif(){
        setMimeType(AlbumItemInfo.MIME_TYPE_GIF);
    }
    Gif(Parcel parcel) {
        super(parcel);
        setMimeType(AlbumItemInfo.MIME_TYPE_GIF);
    }
}
