package com.dhu777.tagalbum.data.entity;

import android.os.Parcel;

import com.dhu777.tagalbum.util.AlbumItemInfo;
/**
 * 抽象类{@link AlbumItem}的实现,该类型的元素在加载入视图时作为普通位图加载.
 */
public class Picture extends AlbumItem  {
    protected Picture(){
        setMimeType(AlbumItemInfo.MIME_TYPE_PICTURE);
    }
    Picture(Parcel parcel) {
        super(parcel);
        setMimeType(AlbumItemInfo.MIME_TYPE_PICTURE);
    }
}
