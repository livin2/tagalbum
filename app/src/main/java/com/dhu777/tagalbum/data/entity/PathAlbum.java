package com.dhu777.tagalbum.data.entity;

/**
 * 按路径分类的相簿,POJO,继承自{@link AlbumBucket}
 */
public class PathAlbum extends AlbumBucket {
    private String path;
    private boolean excluded;
    public PathAlbum(){
        super();
        excluded = false; //todo exclude album
    }
    public AlbumBucket setPath(String path) {
        this.path = path;
        return this;
    }
    public String getPath() {
        return path;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public PathAlbum setExcluded(boolean excluded) {
        this.excluded = excluded;
        return this;
    }
}
