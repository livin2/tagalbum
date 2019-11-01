package com.dhu777.tagalbum.data.entity;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.util.AlbumItemInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 相簿元素的信息抽象类,涵盖相簿元素基本要有的属性以及相应的getter与setter.
 * <p>实现了Parcelable接口便于该对象序列化并通过{@link android.content.Intent}在不同{@link android.app.Activity}传递</p>
 */
public abstract class AlbumItem extends MediaInfo implements Parcelable {
    public static final String TAG = "AlbumItem";
    
    private String path;
    private Uri uri;
    private long bucketId;
    private long dateTaken;

    public AlbumItem() {
    }

    /**
     * 静态工厂方法,子类的构造器都是protected的,所以都要都过本方法实例化.
     * @param mime_type 需要构造的类型
     * @return AlbumItem的具体实现的对象
     * @see Gif
     * @see Picture
     */
    public static AlbumItem getInstance(String mime_type) {
        AlbumItem albumItem = null;
        final int mimeType = AlbumItemInfo.getMimeType(mime_type);
        if(mimeType==AlbumItemInfo.MIME_TYPE_PICTURE)
            albumItem =  new Picture();
        else if (mimeType==AlbumItemInfo.MIME_TYPE_GIF)
            albumItem = new Gif();
        return albumItem;
    }
    public static AlbumItem getInstance(long id, String path,String mime_type,String name
            ,Uri uri,long dateTaken){
        AlbumItem albumItem = getInstance(mime_type);
        if (albumItem != null) {
            albumItem.setPath(path).setDateTaken(dateTaken).setUri(uri);
            albumItem.setId(id);
            albumItem.setName(name);
        }
        return albumItem;
    }


    /**
     * Glide选项
     */
    public Key getGlideSignature() {
        File file = new File(getPath());
        String lastModified = String.valueOf(file.lastModified());
        return new ObjectKey(lastModified);
    }

    public RequestOptions getGlideRequestOptions(Context context) {
        return new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(getGlideSignature());
        //         .error(getErrorPlaceholder(context))
//        todo error
    }

    /**
     * Parcelable 接口实现
     */
    protected AlbumItem(Parcel in) {
        //createFromParcel() have read mimeType
        setId(in.readLong());
        setName(in.readString());
        path = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        dateTaken = in.readLong();
    }

    public static final Creator<AlbumItem> CREATOR = new Creator<AlbumItem>() {
        @Override
        public AlbumItem createFromParcel(Parcel in) {
            final int mimeType = in.readInt();
            if (mimeType == AlbumItemInfo.MIME_TYPE_GIF)
                return new Gif(in);
            return new Picture(in);
        }

        @Override
        public AlbumItem[] newArray(int size) {
            return new AlbumItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel: ");
        dest.writeInt(getMimeType());
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeString(path);
//        Log.d(TAG, "writeToParcel: uri.toString "+uri.toString());
//        dest.writeString(uri.toString());
        dest.writeParcelable(uri,flags);
        dest.writeLong(dateTaken);
    }




    public String getPath() {
        return path;
    }

    public AlbumItem setPath(String path) {
        this.path = path;
        return this;
    }

    public Uri getUri() {
        return uri;
    }

    public AlbumItem setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public AlbumItem setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
        return this;
    }

    public long getBucketId() {
        return bucketId;
    }

    public AlbumItem setBucketId(long bucketId) {
        this.bucketId = bucketId;
        return this;
    }
}
