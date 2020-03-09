package com.dhu777.tagalbum.data.persistent.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.dhu777.tagalbum.data.persistent.entity.Tag;

import java.util.List;

/**
 * 定义给ROOM实现的DAO接口.
 */
@Dao
public interface TagDao {
    //(onConflict = OnConflictStrategy.FAIL) catch SQLiteConstraintException
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Long insert(Tag tag);

    @Delete
    public void delete(Tag tag);

    /**
     * SQL:SELECT * FROM Tag WHERE id = :id LIMIT 1
     * @param id
     * @return 根据id查找标签
     */
    @Query("SELECT * FROM Tag WHERE id = :id LIMIT 1")
    public Tag  getById(long id);

    @Query("SELECT * FROM Tag")
    public List<Tag> getAll();

    /**
     * SQL:SELECT * FROM Tag WHERE val = :tagVal LIMIT 1
     * @param tagVal
     * @return 根据标签值查找标签
     */
    @Query("SELECT * FROM Tag WHERE val = :tagVal LIMIT 1")
    public Tag  getByValue(String tagVal);
}
