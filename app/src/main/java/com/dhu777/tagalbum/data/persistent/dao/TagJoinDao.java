package com.dhu777.tagalbum.data.persistent.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dhu777.tagalbum.data.persistent.entity.TagJoin;

import java.util.List;

/**
 * 定义给ROOM实现的DAO接口.
 */
@Dao
public interface TagJoinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Long insert(TagJoin tagJoin);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public List<Long> insertList(List<TagJoin> tagJoins);

    @Delete
    public void delete(TagJoin tagJoin);

    /**
     * SQL:SELECT * FROM TagJoin WHERE mediaId = :id
     * @param id
     * @return 根据图片信息id查找图片-标签的关联
     */
    @Query("SELECT * FROM TagJoin WHERE mediaId = :id")
    public LiveData<List<TagJoin>> getByMediaId(long id);

    /**
     * SQL:SELECT * FROM TagJoin WHERE mediaId = :id
     * @param id
     * @return 根据图片信息id查找图片-标签的关联
     */
    @Query("SELECT * FROM TagJoin WHERE mediaId = :id")
    public List<TagJoin> getByMediaIdOnce(long id);


    /**
     * SQL:SELECT * FROM TagJoin WHERE tagId = :id
     * @param id
     * @return 根据标签id查找图片-标签的关联
     */
    @Query("SELECT * FROM TagJoin WHERE tagId = :id")
    public List<TagJoin> getByTagId(long id);

    @Query("SELECT * FROM TagJoin")
    public List<TagJoin> getAll();
}
