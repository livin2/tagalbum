package com.dhu777.tagalbum.data.persistent.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;
/**
 * 存入数据库的定义标签与图片关联的实体POJO.通过ROOM做ORM.
 */
@Entity(primaryKeys = {"tagId","mediaId"},
        indices = {@Index(value = {"mediaId"})},
        foreignKeys = {
                @ForeignKey(entity = MediaInfo.class,
                        parentColumns = "id",
                        childColumns = "mediaId",
                        onUpdate = CASCADE,
                        onDelete = CASCADE),
                @ForeignKey(entity = Tag.class,
                        parentColumns = "id",
                        childColumns = "tagId",
                        onUpdate = CASCADE,
                        onDelete = CASCADE )}
)
public class TagJoin {
    @NonNull
    private Long mediaId;
    @NonNull
    private Long tagId;

    public TagJoin(long mediaId, long tagId) {
        this.mediaId = mediaId;
        this.tagId = tagId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
