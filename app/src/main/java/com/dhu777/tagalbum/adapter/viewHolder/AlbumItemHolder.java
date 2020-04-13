package com.dhu777.tagalbum.adapter.viewHolder;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.recyclerView.selection.AlbumItemDetailsLookup;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.entity.Picture;
import com.dhu777.tagalbum.util.UiUtil;

/**
 * 缩略图视图持有类.
 * 继承自{@link RecyclerView.ViewHolder}
 */
public class AlbumItemHolder extends RecyclerView.ViewHolder {
    private AlbumItem albumItem;

    /**
     * 构造器时需要提供相应的元素视图.
     * @param itemView 调用者渲染好的视图,不能为空.
     */
    public AlbumItemHolder(View itemView) {
        super(itemView);
    }

    /**
     * @return 获取持有类绑定的数据.
     */
    public AlbumItem getAlbumItem() {
        return albumItem;
    }

    /**
     * 为持有类绑定数据,将数据设置进视图中.
     * 根据图片信息对象{@link AlbumItem#getPath()}获取路径并加载进视图中
     * @param albumItem 传入的图片数据
     */
    public AlbumItemHolder setAlbumItem(AlbumItem albumItem) {
        if (this.albumItem != albumItem) {
            this.albumItem = albumItem;
            if(albumItem instanceof Gif)
                loadGif(albumItem);
            else
                loadImage(albumItem);
        }
        return this;
    }

    private void loadImage(final AlbumItem albumItem) {
        //todo load error
        ImageView imageView = itemView.findViewById(R.id.image);
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(albumItem.getPath())
                .apply(albumItem.getGlideRequestOptions(imageView.getContext()))
                .into(imageView);
    }

    private void loadGif(final AlbumItem albumItem) {
        //todo load error
        ImageView imageView = itemView.findViewById(R.id.image);
        Glide.with(imageView.getContext())
                .asGif()
                .load(albumItem.getPath())
                .apply(albumItem.getGlideRequestOptions(imageView.getContext()))
                .into(imageView);
    }
    private boolean selected = false;
    private Drawable selectorOverlay;
    public void setSelected(boolean selected) {
        boolean animate = this.selected != selected;
//        boolean animate = false;
        this.selected = selected;
        if (animate) {
            animateSelected();
        }
    }

    private void animateSelected() {
        final View imageView = itemView.findViewById(R.id.image);

        float scale = selected ? 0.8f : 1.0f;
        imageView.animate()
                .scaleX(scale)
                .scaleY(scale)
                .start();

        if (selectorOverlay == null) {
            selectorOverlay = AppCompatResources.getDrawable(imageView.getContext(),
                    R.drawable.item_select_overlay);
        }
        Log.d("animateSelected", "animateSelected: "+selectorOverlay);
        if (selected) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.getOverlay().remove(selectorOverlay);
                    selectorOverlay.setBounds(0, 0,
                            imageView.getWidth(),
                            imageView.getHeight());
                    imageView.getOverlay().add(selectorOverlay);
                }
            });
        } else {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.getOverlay().remove(selectorOverlay);
                }
            });
        }
    }

}
