package com.dhu777.tagalbum.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.PaletteHelper;
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

        Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
        PaletteHelper.getDomin(bitmap, new PaletteHelper.getDominCallback() {
            @Override
            public void onLoaded(@NonNull Palette.Swatch swatch) {
                toolbar.setBackgroundColor(swatch.getRgb());
                toolbar.getBackground().setAlpha(222);
                String msg = ColorTag.tagOf(swatch);
                Log.d(TAG, "colorTag:"+swatch);
                Log.d(TAG, "colorTag:"+msg);
                notifyMsg(msg);
//                View v = getWindow().getDecorView().findViewById(R.id.photo_view);
//                Snackbar.make(v, "test color", Snackbar.LENGTH_SHORT).setTextColor(swatch.getRgb());
            }
        });
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
