package com.dhu777.tagalbum.data.persistent;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.dao.MediaDao;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppDataBaseTest {
    private MediaDao mediaDao;
    private AppDataBase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
//        Log.d("DBTEST", "createDb: context"+context);
//        Log.e("DBTEST", "createDb:context",new Exception("createDb"));
//        if(context == null)
//            Log.e("DBTEST", "createDb:context null",new Exception("createDb"));
        db = Room.inMemoryDatabaseBuilder(context, AppDataBase.class).build();
//        if(db== null)
//            Log.e("DBTEST", "createDb:db null",new Exception("createDb"));
        mediaDao = db.mediaDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void aaB(){
        assertTrue(true);
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
//        AlbumItem item = AlbumItem.getInstance("image/*");
//        String name = "fuck";
//        item.setId(12);
//        item.setName(name);
//        mediaDao.insert(item);
//        MediaInfo byId = mediaDao.getById(12);
    }

}