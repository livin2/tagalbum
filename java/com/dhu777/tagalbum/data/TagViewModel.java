package com.dhu777.tagalbum.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dhu777.tagalbum.data.persistent.entity.MediaInfo;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.data.persistent.repository.AsyncRepository;
import com.dhu777.tagalbum.data.persistent.repository.TagRepository;

import java.util.List;

/**
 * 配合应用生命周期提供观察者模式实现的组件.
 */
public class TagViewModel extends AndroidViewModel {
    private TagRepository tagRepository;

    public TagViewModel(@NonNull Application application) {
        super(application);
        tagRepository = AsyncRepository.getInstance(application);
    }

    /**
     * 封装为图片添加标签的数据库操作的接口.
     * @param media 目标图片
     * @param tag 标签值
     * @return
     */
    public boolean insertTagForMedia(MediaInfo media,String tag){
        if(media != null && tag!=null){
            tagRepository.insertTagForMedia(media,tag);
            return true;
        }
        return false;
    }

    /**
     * 查找图片所标注的所有标签,当数据库更新时会触发重新查询并更新数据.
     * @param mediaId 目标图片的id
     * @return 返回支持监听数据变换的类型LiveData.
     */
    public LiveData<List<TagView>> getTagsByMedia(Long mediaId) {
        return tagRepository.getTagByMedia(mediaId);
    }

    /**
     * 通过标签值检索图片,当数据库更新时会触发重新查询并更新数据.
     * @param val 目标标签值
     * @return 返回支持监听数据变换的类型LiveData.
     */
    public LiveData<List<TagView>> getTagJoinByTagList(List<String> val){
        return tagRepository.getTagByTagList(val);
    }

    /**
     * 为某图片删除标签.
     * @param mediaId 目标图片的id
     * @param tagId 目标标签的id
     */
    public void deleteTagJoin(Long mediaId,Long tagId){
        tagRepository.deleteTagJoin(new TagJoin(mediaId,tagId));
    }
}
