package com.dhu777.tagalbum.data.persistent.repository;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;

import com.dhu777.tagalbum.data.persistent.AppDataBase;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

import java.util.List;

/**
 * 定义了所有操作数据库的方法的抽象类.
 */
public abstract class TagRepository {
    public abstract boolean insertTag(final Tag tag);
    public abstract boolean insertMedia(final MediaInfo media);
    public abstract boolean insertTagJoin(TagJoin tagJoin);

    public abstract boolean insertTagForMedia(final MediaInfo media,final String tag);

    public abstract boolean deleteTagJoin(final TagJoin tagJoin);

    public abstract List<Tag> getAllTags();
    public abstract List<MediaInfo> getAllMedia();
    public abstract List<TagJoin> getAllTagJoins();
    public abstract LiveData<List<TagView>> getTagByMedia(long mediaId);
//    public abstract LiveData<List<TagView>> getTagByTagVal(String value);
    public abstract LiveData<List<TagView>> getTagByTagList(List<String> value);

    //test
    public abstract void logAll();
}
