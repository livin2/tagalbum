package com.dhu777.tagalbum.data.persistent.repository;

import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;

import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SyncRepositoryTest {
    private static final String TAG = "SyncRepositoryTest";
    private TagRepository repository;

    private MediaInfo media0;
    private int mediaSize = 0;
    private int tjSize = 0;
    private final int logItem = 5;
    @Before
    public void setUp() throws Exception {
        repository = SyncRepository.getInstance(ApplicationProvider.getApplicationContext());

        List<MediaInfo> medias = repository.getAllMedia();
        mediaSize = medias.size();
        tjSize = repository.getAllTagJoins().size();
        media0 = medias.get(0);

        Log.d(TAG, "Before Media: "+medias.size());
        int cot = 0;
        for (MediaInfo m :medias){
            Log.d(TAG, m.getId()+":"+m.getName());
            if(++cot >= logItem)break;
        }
    }

    @After
    public void tearDown() throws Exception {
        List<MediaInfo> mediaAfter = repository.getAllMedia();
        int cot = 0;
        Log.d(TAG, "After Media: "+mediaAfter.size());
        for (MediaInfo m :mediaAfter){
            Log.d(TAG, m.getId()+":"+m.getName());
            if(++cot >= logItem)break;
        }
    }

    @Before
    public void logTagJoinBefore(){
        int cot = 0;
        List<TagJoin> tjs = repository.getAllTagJoins();
        Log.d(TAG, "Before TagJoins: "+tjs.size());
        for (TagJoin tj :tjs){
            Log.d(TAG, tj.getMediaId()+":"+tj.getTagId());
            if(++cot >= logItem)break;
        }
    }

    @After
    public void logTagJoinAfter(){
        int cot = 0;
        List<TagJoin> tjAfter = repository.getAllTagJoins();
        Log.d(TAG, "After TagJoins: "+tjAfter.size());
        for (TagJoin tj :tjAfter){
            Log.d(TAG, tj.getMediaId()+":"+tj.getTagId());
            if(++cot >= logItem)break;
        }
    }

//    @Test
    public void insertMediaIgnoreConflict() {
        MediaInfo newInsert = new MediaInfo();
        newInsert.setId(media0.getId());
        newInsert.setName("newnew");

        repository.insertMedia(newInsert);
        List<MediaInfo> newMedias = repository.getAllMedia();

        assertEquals(newMedias.size(),mediaSize);
        assertEquals(newMedias.get(0).getName(),media0.getName());
    }

    @Test
    public void updateTagJoinId() {
        repository.updateTagJoinId(317065,606);
    }

//    @Test
    public void deleteMediaAndTagJoin() {
        repository.deleteMedia(media0);
        assertEquals(repository.getAllMedia().size(),mediaSize-1);
        assertEquals(repository.getAllTagJoins().size(),tjSize-1);

        repository.insertMedia(media0);
    }

//    @Test
    public void getAllMedia() {
    }




}