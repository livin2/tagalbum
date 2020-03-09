package com.dhu777.tagalbum.data.persistent.entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
/**
 * 存入数据库的标签数据的实体POJO.通过ROOM做ORM.
 */
@Entity(indices = {@Index(value = "val",unique = true)})
public class Tag {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    @NonNull
    private String val;

    public Tag(@NonNull Long id, String val) {
        this.id = id;
        this.val = val;
    }

    public Tag(String text) {
        this.val = text;
        this.id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
