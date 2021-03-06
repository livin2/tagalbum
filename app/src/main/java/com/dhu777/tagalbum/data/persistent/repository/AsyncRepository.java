package com.dhu777.tagalbum.data.persistent.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.dhu777.tagalbum.data.persistent.AppDataBase;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

import java.util.List;

/**
 * 该类实现的所有的数据库操作都是异步的.该类使用单例模式.
 */
public class AsyncRepository  extends TagRepository{
    private static final String TAG = "AsyncRepository";
    private static TagRepository INSTANCE;
    private AppDataBase DB;

    private AsyncRepository(){}
    public static TagRepository getInstance(@NonNull final Context context) {
        if (INSTANCE==null){
            INSTANCE = new AsyncRepository();
            //todo AppDataBase.getInMemoryInstance
            ((AsyncRepository) INSTANCE).DB = AppDataBase.getInstance(context);
        }
        Log.d(TAG, "getInstance: "+INSTANCE);
        return INSTANCE;
    }

    @Override
    public boolean insertTag(final Tag tag) {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                DB.tagDao().insert(tag);
                Log.d(TAG, "insertTag:"+tag.getVal());
            }
        }).execute();
        return true;
    }

    @Override
    public boolean insertMedia(final MediaInfo media) {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                DB.mediaDao().insert(media);
//                Log.d(TAG, "insertMedia:"+media.getName());
            }
        }).execute();
        return true;
    }

    @Override
    public boolean deleteMedia(MediaInfo media) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteMedia(long mediaid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean insertTagJoin(final TagJoin tagJoin) {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                DB.tagJoinDao().insert(tagJoin);
                Log.d(TAG, "insertTagJoin");
            }
        }).execute();
        return true;
    }

    public boolean insertTagForMediaSync(final MediaInfo media, final String tag){
        if(DB.mediaDao().getById(media.getId())==null){
            Log.d(TAG, "insert:"+media.getId());
            DB.mediaDao().insert(media);
        }
        Tag tagSet = new Tag(tag);
        DB.tagDao().insert(tagSet);
        Tag tagGet = DB.tagDao().getByValue(tag);
        if(tagGet!=null){
            Log.d(TAG, media.getId()+" add tag:"+tag);
            TagJoin tg = new TagJoin(media.getId(),tagGet.getId());
            DB.tagJoinDao().insert(tg);
        }
        return true;
    }

    @Override
    public boolean insertTagForMedia(final MediaInfo media, final String tag) {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                if(DB.mediaDao().getById(media.getId())==null){
                    Log.d(TAG, "insert new media");
                    DB.mediaDao().insert(media);
                }
                Tag tagSet = new Tag(tag);
                DB.tagDao().insert(tagSet);
                Tag tagGet = DB.tagDao().getByValue(tag);
                if(tagGet!=null){
                    Log.d(TAG, "insert tagjoin");
                    TagJoin tg = new TagJoin(media.getId(),tagGet.getId());
                    DB.tagJoinDao().insert(tg);
                }
            }
        }).execute();
        return true;
    }

    @Override
    public boolean deleteTagJoin(final TagJoin tagJoin) {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                if(tagJoin == null
                        ||tagJoin.getTagId()==null||tagJoin.getMediaId()==null){
                    Log.d(TAG, "delete fail");
                    return;
                }

                Long tid = tagJoin.getTagId();
                DB.tagJoinDao().delete(tagJoin);

                List<TagJoin> tjlist = DB.tagJoinDao().getByTagId(tid);
                if(tjlist==null||tjlist.size()==0)
                    DB.tagDao().delete(new Tag(tagJoin.getTagId(),null));
                Log.d(TAG, "deleted");
            }
        }).execute();
        return true;
    }

    @Override
    public boolean updateTagJoinId(long oldId, long newId) {
        throw new UnsupportedOperationException();
    }


    @Override
    public List<TagView> getTagByMedia(long mediaId) {
//        return DB.tagViewDao().getByMediaId(mediaId);
        throw new UnsupportedOperationException();
    }


//    public LiveData<List<TagView>> getTagByTagVal(String value) {
//        return DB.tagViewDao().getByTagVal(value);
//    }

    @Override
    public List<TagView> getTagByTagList(List<String> value) {
//        return DB.tagViewDao().getByTagList(value,value.size());
        throw new UnsupportedOperationException();
    }

    //todo unimplement
    @Override
    public List<Tag> getAllTags() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MediaInfo> getAllMedia() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TagJoin> getAllTagJoins() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logAll() {
        new AsyncOperation(new AsyncOperation.Opt() {
            @Override
            public void doInBackground() {
                List<MediaInfo> mlist = DB.mediaDao().getAll();
                Log.d(TAG, "media:"+mlist.size());
                for (MediaInfo val:mlist){
                    Log.d(TAG, val.getId()+":"+val.getName());
                }

                List<Tag> tlist = DB.tagDao().getAll();
                Log.d(TAG, "tag:"+tlist.size());
                for (Tag val:tlist){
                    Log.d(TAG, val.getId()+":"+val.getVal());
                }

                List<TagJoin> tjlist = DB.tagJoinDao().getAll();
                Log.d(TAG, "tagjoin:"+tjlist.size());
                for (TagJoin val:tjlist){
                    Log.d(TAG, val.getMediaId()+":"+val.getTagId());
                }
            }
        }).execute();
    }

    private static class AsyncOperation extends AsyncTask<Void, Void, Void> {
        public interface Opt{
            void doInBackground();
        }

        private Opt opt;
        public AsyncOperation(Opt opt) {
            this.opt = opt;
        }

        @Override
        protected Void doInBackground(Void... ts) {
            opt.doInBackground();
            opt = null;
            return null;
        }
    }
}
