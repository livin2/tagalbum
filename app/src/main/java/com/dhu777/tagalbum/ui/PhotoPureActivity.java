package com.dhu777.tagalbum.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class PhotoPureActivity extends PhotoAbsActivity {
    private static final String TAG = "PhotoPureActivity";
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        chipGroup = findViewById(R.id.chip_group);
        photoView = findViewById(R.id.photo_view);
        setToolbar();
        adaptBarUI();
//        loadTags(photo.getId());
    }

    @Override
    protected void loadMedia(AlbumItem item) {
        if(photo instanceof Gif)
            loadGif(photo);
        else
            loadPhoto(photo);
       photoView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               statusUiVisible = !statusUiVisible;
               statusUiVisibility(statusUiVisible);
           }
       });
    }
    /**
     * 加载图片.图片为静态图片时调用.
     * @param item 图片信息对象
     */
    protected void loadPhoto(AlbumItem item){
        Glide.with(this)
                .asBitmap()
                .load(item.getPath())
                .apply(item.getGlideRequestOptions(photoView.getContext()))
                .into(photoView);
    }

    /**
     * 加载图片.图片为动态图片时调用.
     * @param item 图片信息对象
     */
    protected void loadGif(AlbumItem item){
        Glide.with(this)
                .asGif()
                .load(item.getPath())
                .apply(item.getGlideRequestOptions(photoView.getContext()))
                .into(photoView);
    }
}
