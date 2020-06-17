package com.dhu777.tagalbum.data.persistent.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

import java.util.List;

/**
 * 定义给ROOM实现的DAO接口.
 */
@Dao
public interface TagViewDao {
    @Query("SELECT * FROM TagView")
    public List<TagView> getAll();

    /**
     * SQL:SELECT * FROM TagView WHERE mediaId = :id
     * @param id
     * @return 根据图片信息id查找图片-标签的关联.返回支持监听数据变换的类型LiveData.
     */
    @Query("SELECT * FROM TagView WHERE mediaId = :id")
    public LiveData<List<TagView>> getByMediaId(long id);

    @Query("SELECT * FROM TagView WHERE tagId = :id")
    public LiveData<List<TagView>> getByTagId(long id);

    /**
     * SQL:SELECT * FROM TagView WHERE tagVal = :value
     * @param value
     * @return 根据标签id查找图片-标签的关联.返回支持监听数据变换的类型LiveData.
     */
    @Query("SELECT * FROM TagView WHERE tagVal = :value")
    public LiveData<List<TagView>> getByTagVal(String value);


    /**
     * 查询符合vallist中所有标签值的图片.
     * <p>
     *     SQL: SELECT * FROM TagView WHERE  tagVal IN (:valList)
     *      * GROUP BY mediaId HAVING COUNT(mediaId) >= (:count)
     * </p>
     * @param valList 标签值列表
     * @param count 标签值的个数
     * @return 返回支持监听数据变换的类型LiveData.
     */
    @Query("SELECT * FROM TagView WHERE  tagVal IN (:valList) " +
            "GROUP BY mediaId HAVING COUNT(mediaId) >= (:count)")
    public LiveData<List<TagView>> getByTagList(List<String> valList,int count);

    /**
     * 查询符合vallist中所有标签值的图片.
     * <p>
     *     SQL: SELECT * FROM TagView WHERE  tagVal IN (:valList)
     *      * GROUP BY mediaId HAVING COUNT(mediaId) >= (:count)
     * </p>
     * @param valList 标签值列表
     * @param count 标签值的个数
     * @return 返回支持监听数据变换的类型LiveData.
     */
    @Query("SELECT * FROM TagView WHERE  tagVal IN (:valList) " +
            "GROUP BY mediaId HAVING COUNT(mediaId) >= (:count)")
    public List<TagView> getByTagListSync(List<String> valList,int count);
}
