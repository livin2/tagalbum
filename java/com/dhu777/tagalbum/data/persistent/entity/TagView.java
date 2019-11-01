package com.dhu777.tagalbum.data.persistent.entity;

import androidx.room.DatabaseView;

/**
 * {@link Tag}与{@link TagJoin}连接视图的实体POJO.通过ROOM做ORM.
 */
@DatabaseView("SELECT TagJoin.mediaId, TagJoin.tagId," +
        "Tag.val AS tagVal FROM TagJoin " +
        "INNER JOIN Tag ON TagJoin.tagId = Tag.id")
public class TagView {
    private Long mediaId;
    private Long tagId;
    private String tagVal;

    public TagView(Long mediaId, Long tagId, String tagVal) {
        this.mediaId = mediaId;
        this.tagId = tagId;
        this.tagVal = tagVal;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public Long getTagId() {
        return tagId;
    }

    public String getTagVal() {
        return tagVal;
    }
}
