package com.dhu777.tagalbum.adapter.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.viewHolder.AlbumItemHolder;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.entity.Picture;
import com.dhu777.tagalbum.ui.AlbumActivity;
import com.dhu777.tagalbum.ui.PhotoActivity;
import com.dhu777.tagalbum.ui.PhotoPureActivity;
import com.dhu777.tagalbum.ui.PhotoViewPageActivity;
import com.dhu777.tagalbum.ui.ViewPageActivity;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 缩略图相册列表视图的适配器
 * <p>
 *  将单个缩略图视图集合装配进相册中.继承自{@link BaseAdapter},间接继承自{@link RecyclerView}.
 * </p>
 */
public class AlbumAdapter extends BaseAdapter<AlbumBucket> {

    /** 相册视图中的缩略图元素的类型: 其它类型.*/
    public static final int VIEW_TYPE_NONE = -1;
    /** 相册视图中的缩略图元素的类型: 静态图片.*/
    public static final int VIEW_TYPE_PICTURE = 0;
    /** 相册视图中的缩略图元素的类型: 动态图片.*/
    public static final int VIEW_TYPE_GIF = 1;

    private int albumPos;
    public AlbumAdapter(Context context,int albumpos){
        this.albumPos =albumpos;
    }

    private boolean isViewPage = true;
    public AlbumAdapter setViewPage(boolean viewPage) {
        isViewPage = viewPage;
        return this;
    }


    /**
     * 返回本适配器相册数据在position位置的元素的类型.
     * @param position 要查询的数据元素的下标位置
     * @return 元素的类型
     * @see #VIEW_TYPE_PICTURE
     * @see RecyclerView.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        AlbumItem albumItem = getData().getAlbumItems().get(position);
        if (albumItem instanceof Picture)
            return VIEW_TYPE_PICTURE;
        else if(albumItem instanceof Gif)
            return VIEW_TYPE_GIF;
        return VIEW_TYPE_NONE;
    }

    /**
     * 绘制容纳缩略图的元素视图并放入一个新建的{@link AlbumItemHolder}中,返回该{@link AlbumItemHolder}.
     * <p>当充当相册的{@link RecyclerView}需要一个{@link androidx.recyclerview.widget.RecyclerView.ViewHolder}
     * 去持有一个缩略图的信息与显示该缩略图的视图时本方法会被调用来创建一个新的{@link AlbumItemHolder}.</p>
     * @param viewGroup 父视图ViewGroup,ViewHolder与数据项绑定后,其中的子视图会添加到父视图中
     * @param viewType 新创建的元素视图的类型
     * @return 返回一个持有新元素视图的 ViewHolder
     * @see #VIEW_TYPE_PICTURE
     * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_album, viewGroup, false);
        //don't need to
        return new AlbumItemHolder(v);
    }

    /**
     * <p>根据相应的position获取对应的数据并将其绑定到viewHolder上,当充当相册的{@link RecyclerView}会在之后
     * 根据viewHolder中的数据{@link BaseAdapter#getData}渲染viewHolder中视图的内容并将其添加到父视图.</p>
     * <p>此方法还会为各个元素视图绑定点击事件监听器</p>
     * @param viewHolder 该viewHolder持有的子视图绘在位置position显示
     * @param position 子视图显示的位置
     * @see RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final AlbumItem item = getData().getAlbumItems().get(position);
        final AlbumItemHolder holder = (AlbumItemHolder)viewHolder;
        holder.setAlbumItem(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context =  holder.itemView.getContext();
                //todo edit
                Intent intent;
                if(isViewPage){
                    intent = new Intent(context, PhotoViewPageActivity.class);
                    intent.putExtra(AlbumActivity.KEY_ALBUMPOS,albumPos);
                }else
                    intent = new Intent(context, PhotoPureActivity.class);
                intent.putExtra(PhotoActivity.KEY_PHOTO,item);
                intent.putExtra(PhotoActivity.KEY_PHOTOPOS,position);

                context.startActivity(intent);
            }
        });
    }

    /**
     * @return Adapter数据集合中元素的数量
     */
    @Override
    public int getItemCount() {
        return getData().getAlbumItems().size();
    }
}
