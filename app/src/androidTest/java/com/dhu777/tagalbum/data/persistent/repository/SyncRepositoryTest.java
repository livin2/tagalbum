package com.dhu777.tagalbum.data.persistent.repository;

import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;

import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SyncRepositoryTest {
    private static final String TAG = "SyncRepositoryTest";
    private TagRepository repository;
    @Before
    public void setUp() throws Exception {
        repository = SyncRepository.getInstance(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void deleteMediaAndTagJoin() {
        List<MediaInfo> medias = repository.getAllMedia();
        Log.d(TAG, "Before Media: "+medias.size());
        for (MediaInfo m :medias){
            Log.d(TAG, m.getId()+":"+m.getName());
        }


        List<TagJoin> tjs = repository.getAllTagJoins();
        Log.d(TAG, "Before TagJoins: "+tjs.size());
        for (TagJoin tj :tjs){
            Log.d(TAG, tj.getMediaId()+":"+tj.getTagId());
        }

        repository.deleteMedia(medias.get(0));

        List<MediaInfo> mediaAfter = repository.getAllMedia();
        Log.d(TAG, "Before Media: "+mediaAfter.size());
        for (MediaInfo m :mediaAfter){
            Log.d(TAG, m.getId()+":"+m.getName());
        }


        List<TagJoin> tjAfter = repository.getAllTagJoins();
        Log.d(TAG, "Before TagJoins: "+tjAfter.size());
        for (TagJoin tj :tjAfter){
            Log.d(TAG, tj.getMediaId()+":"+tj.getTagId());
        }

        assertEquals(mediaAfter.size(),medias.size()-1);
        assertEquals(tjAfter.size(),tjs.size()-1);
    }

    @Test
    public void getAllMedia() {
//        System.out.println("getAllMedia: "+medias.size());
    }


}