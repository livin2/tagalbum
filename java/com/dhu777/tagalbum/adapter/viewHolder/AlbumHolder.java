package com.dhu777.tagalbum.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;

/**
 * 相簿视图持有类,相簿元素视图由相簿标题，相簿封面组成.
 * 继承自{@link RecyclerView.ViewHolder}
 */
public class AlbumHolder extends RecyclerView.ViewHolder{
    private AlbumBucket album;

    /**
     * 构造器时需要提供相应的元素视图.
     * @param itemView 调用者渲染好的视图,不能为空.
     */
    public AlbumHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * @return 获取持有类绑定的数据.
     */
    public AlbumBucket getAlbum() {
        return album;
    }

    /**
     * 为持有类绑定数据,将数据设置进视图中.
     * 相簿视图的封面为传入{@link AlbumBucket#getAlbumItems()}的第一张图
     * @param album 传入的相簿数据
     */
    public void setAlbum(AlbumBucket album) {
        if (album == null) {
            //todo album is null
            return;
        }
        this.album = album;

        final TextView titleV = itemView.findViewById(R.id.title);
        titleV.setText(album.getTitle());
        titleV.requestLayout();

        final TextView countV = itemView.findViewById(R.id.count);
        String countText = itemView.getContext().getString(R.string.item_count
                ,album.getAlbumItems().size());
        countV.setText(countText);

        final ImageView cover = itemView.findViewById(R.id.cover);
        loadCover(cover);
    }

    private void loadCover(final ImageView image) {
        if (album.getAlbumItems().size() == 0) {
            //todo albumItems == 0
            return;
        }

        final AlbumItem coverImage = album.getAlbumItems().get(0);
        Glide.with(itemView.getContext())
                .asBitmap()
                .load(coverImage.getPath())
                .apply(coverImage.getGlideRequestOptions(itemView.getContext()))
                .into(image);
    }
}
