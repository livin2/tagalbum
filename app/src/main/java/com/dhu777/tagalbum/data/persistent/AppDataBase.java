package com.dhu777.tagalbum.data.persistent;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dhu777.tagalbum.data.persistent.dao.MediaDao;
import com.dhu777.tagalbum.data.persistent.dao.TagDao;
import com.dhu777.tagalbum.data.persistent.dao.TagJoinDao;
import com.dhu777.tagalbum.data.persistent.dao.TagViewDao;
import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

/**
 * 定义了获取ROOM实现的DAO的接口的抽象类,由ROOM框架提供的{@link Room#databaseBuilder(Context, Class, String)}
 * 实现.<p>本类对象采用单例模式,getInstance获取单例的过程用类锁同步.两者结合保证应用的数据库操作同步,从而保证
 * 数据库的数据一致性</p>
 */
@Database(entities = {MediaInfo.class, Tag.class, TagJoin.class},
        views = {TagView.class},
        version = 1,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase INSTANCE;
    private static AppDataBase INMEMORY;
    private static AppDataBase SYNCINSTANCE;
    public static AppDataBase getInstance(@NonNull final Context context){
        if (INSTANCE == null) {
            synchronized ( AppDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "tagjoin").build();
                }
            }
        }
        return INSTANCE;
    }

    private static AppDataBase getSyncInstance(@NonNull final Context context){
        if (SYNCINSTANCE == null) {
            synchronized ( AppDataBase.class){
                if(SYNCINSTANCE == null){
                    SYNCINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "tagjoin").allowMainThreadQueries().build();
                }
            }
        }
        return SYNCINSTANCE;
    }

    public static AppDataBase getInMemoryInstance(@NonNull final Context context){
        if (INMEMORY == null) {
            synchronized (AppDataBase.class){
                if(INMEMORY == null){
                    INMEMORY = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            AppDataBase.class).allowMainThreadQueries().build();
                }
            }
        }
        return INMEMORY;
    }

    public abstract TagViewDao tagViewDao();
    public abstract MediaDao mediaDao();
    public abstract TagDao tagDao();
    public abstract TagJoinDao tagJoinDao();
}
