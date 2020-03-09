package com.dhu777.tagalbum.adapter.recyclerView;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.adapter.viewHolder.AlbumHolder;
import com.dhu777.tagalbum.adapter.viewHolder.AlbumItemHolder;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.ui.AlbumActivity;
import com.dhu777.tagalbum.ui.style.Style;

import java.util.List;

/**
 * MainActivity的{@link RecyclerView}适配器.<br/>
 * 继承自{@link BaseAdapter},间接继承自{@link RecyclerView.Adapter}.
 */
public class MainAdapter extends BaseAdapter<List<AlbumBucket>> {
    private Style style;

    /**
     * 构造器会初始化RecycleView的样式
     * @param context Activitys上下文
     */
    public MainAdapter(Context context){
//        int styleV = Settings.getInstance(context).getStyle();
        style = Style.createStyleInstance(Style.LIST);
    }

    /**
     * 绘制容纳相册条目的元素视图,创建一个新的{@link AlbumHolder}
     * @param viewGroup 父视图ViewGroup,ViewHolder与数据项绑定后,其中的子视图会添加到父视图中
     * @param viewType 新创建的元素视图的类型
     * @return 返回一个持有新元素视图的 ViewHolder
     * @see #onCreateViewHolder(ViewGroup, int)
     * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return style.createViewHolder(viewGroup);
    }

    /**
     * 根据相应的position获取对应的相簿数据并将其绑定到viewHolder上,之后将按{@link BaseAdapter#getData}
     * 的数据在{@link RecyclerView}中渲染相簿视图条目.
     * <p>此方法还会为各个元素视图绑定点击事件监听器</p>
     * @param viewHolder 该viewHolder持有的子视图绘在位置position显示
     * @param i 子视图显示的位置
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final AlbumBucket album = getData().get(i);
        final AlbumHolder holder = (AlbumHolder)viewHolder;
        holder.setAlbum(album);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context =  holder.itemView.getContext();
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra(AlbumActivity.KEY_ALBUMPOS,i);
                context.startActivity(intent);
            }
        });
    }

    /**
     * @return Adapter数据集合中元素的数量
     */
    @Override
    public int getItemCount() {
        if(getData()!= null)
            return getData().size();
        return 0;
    }
}
