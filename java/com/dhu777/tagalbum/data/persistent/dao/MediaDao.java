package com.dhu777.tagalbum.data.persistent.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;

import java.util.List;

/**
 * 定义给ROOM实现的DAO接口.
 */
@Dao
public interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Long insert(MediaInfo mediaInfo);

    //OnConflictStrategy.FAIL catch SQLiteConstraintException
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertList(List<MediaInfo> mediaInfoList);

    @Delete
    public void delete(MediaInfo mediaInfo);

    /**
     * SQL:SELECT * FROM MediaInfo WHERE id = :id LIMIT 1
     * @param id
     * @return 根据id查找图片信息
     */
    @Query("SELECT * FROM MediaInfo WHERE id = :id LIMIT 1")
    public MediaInfo getById(long id);

    /**
     * SQL:SELECT * FROM MediaInfo WHERE id IN (:ids)
     * @param ids
     * @return 根据id列表返回对应图片信息列表
     */
    @Query("SELECT * FROM MediaInfo WHERE id IN (:ids)")
    public List<MediaInfo> getByIds(List<Long> ids);

    @Query("SELECT * FROM MediaInfo")
    public List<MediaInfo> getAll();
}
