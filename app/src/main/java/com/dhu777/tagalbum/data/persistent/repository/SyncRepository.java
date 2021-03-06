package com.dhu777.tagalbum.data.persistent.repository;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.dhu777.tagalbum.data.persistent.AppDataBase;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

import java.util.List;

/**
 *
 * 该类实现的所有的数据库操作都是同步的.这意味着数据库操作将阻塞UI线程,
 * 如果数据库操作时间较长将导致UI活动未响应.一般应当使用{@link AsyncRepository}进行数据库操作.
 */
public class SyncRepository extends TagRepository {
    private static final String TAG = "SyncRepository";
    private static TagRepository INSTANCE;
    private AppDataBase DB;

    private SyncRepository(){}

    public static TagRepository getInstance(Context application) {
        if (INSTANCE==null){
            INSTANCE = new SyncRepository();
            ((SyncRepository) INSTANCE).DB = AppDataBase.getInstance(application);
        }
        return INSTANCE;
    }

    @Override
    public boolean insertTag(Tag tag) {
        try{
            return DB.tagDao().insert(tag)!=null;
        }catch (SQLiteConstraintException e){
            return false;
        }
    }

    @Override
    public boolean insertMedia(MediaInfo media) {
        try{
            return DB.mediaDao().insert(media)!=null;
        }catch (SQLiteConstraintException e){
            return false;
        }
    }

    @Override
    public boolean deleteMedia(MediaInfo media) {
        try{
            DB.mediaDao().delete(media);
            Log.d(TAG, "deleteMedia: success");
            return true;
        }catch (SQLiteConstraintException e){
            Log.e(TAG, "deleteMedia: fail");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMedia(long mediaid) {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setId(mediaid);
        return deleteMedia(mediaInfo);
    }

    @Override
    public boolean insertTagJoin(TagJoin tagJoin) {
        try{
            return DB.tagJoinDao().insert(tagJoin)!=null;
        }catch (SQLiteConstraintException e){
            return false;
        }
    }

    @Override
    public List<Tag> getAllTags() {
        return DB.tagDao().getAll();
    }

    @Override
    public List<MediaInfo> getAllMedia() {
        return DB.mediaDao().getAll();
    }

    @Override
    public List<TagJoin> getAllTagJoins() {
        return DB.tagJoinDao().getAll();
    }

    @Override
    public List<TagView> getTagByMedia(long mediaId) {
        throw new UnsupportedOperationException();
    }
    //todo unimplement

    @Override
    public boolean insertTagForMedia(MediaInfo media, String tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteTagJoin(TagJoin tagJoin) {
        if(tagJoin == null
                ||tagJoin.getTagId()==null||tagJoin.getMediaId()==null){ ;
            return false;
        }

        Long tid = tagJoin.getTagId();
        DB.tagJoinDao().delete(tagJoin);

        List<TagJoin> tjlist = DB.tagJoinDao().getByTagId(tid);
        if(tjlist==null||tjlist.size()==0)
            DB.tagDao().delete(new Tag(tagJoin.getTagId(),null));
        return true;
    }

    @Override
    public boolean updateTagJoinId(long oldId, long newId) {
        //todo DB.runInTransaction
        MediaInfo media = DB.mediaDao().getById(oldId);
        if(media == null)return false;
        media.setId(newId);
        boolean addRes = insertMedia(media);
        if (!addRes) return false;

        List<TagJoin> tjlist = DB.tagJoinDao().getByMediaIdOnce(oldId);
        if(tjlist.size()>0){
            for (TagJoin tj:tjlist)
                tj.setMediaId(newId);
            DB.tagJoinDao().insertList(tjlist);
        }
        boolean delRes = deleteMedia(oldId);
        if (!delRes) return false;
        return true;
    }

    @Override
    public List<TagView> getTagByTagList(List<String> value) {
        return DB.tagViewDao().getByTagListSync(value,value.size());
    }

    @Override
    public void logAll() {
        throw new UnsupportedOperationException();
    }
}
