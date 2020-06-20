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
import androidx.lifecycle.LiveData;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.ui.animators.TitleFade;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.PaletteHelper;
import com.dhu777.tagalbum.util.Permission;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import static com.dhu777.tagalbum.ui.AlbumActivity.KEY_ALBUMPOS;
import static com.dhu777.tagalbum.ui.PhotoActivity.KEY_PHOTO;
import static com.dhu777.tagalbum.ui.PhotoActivity.KEY_PHOTOPOS;

public class PhotoViewPageActivity extends PhotoAbsActivity {
    private static final String TAG = "ViewPageActivity";
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    protected TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);
        chipGroup = findViewById(R.id.chip_group);

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(photoPos);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                photo = album.getAlbumItems().get(position);
                Log.d(TAG, "onPageSelected:"+photo);
                if(photo!=null){
                    tagColor = photo.getColor();
                    loadTags(photo.getId());
                    fadeTitle(photo.getName());
                }
            }
        });
        setToolbar();
        adaptBarUI();
    }

    protected void fadeChips(final long mediaid){
        if(chipGroup!=null){
            chipGroup.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadTags(mediaid);

                            chipGroup.animate()
                                    .alpha(1f)
                                    .setDuration(duration)
                                    .setStartDelay(1000)
                                    .start();
                        }
                    })
                    .start();

        }
    }

    protected void fadeTitle(final String title){
        TitleFade.process(toolbar,toolbar1 -> {
            toolbar.setTitle(title != null ? title : "");
        });
//        if(toolbar!=null){
//            for (int i = 0; i < toolbar.getChildCount(); i++) {
//                View v = toolbar.getChildAt(i);
//                if (v instanceof TextView) {
//                    titleView = (TextView) v;
//                    break;
//                }
//            }
//        }
//        if(titleView!=null){
//            titleView.setText(title);
//            titleView.animate().cancel();
//            titleView.animate()
//                    .alpha(0f)
//                    .setDuration(duration)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            titleView.setText(title);
//                            titleView.animate()
//                                    .alpha(1f)
//                                    .setDuration(duration)
//                                    .start();
//                        }
//                    })
//                    .start();
//        }
    }

    @Override
    protected void loadMedia(AlbumItem item) {
        //load by Adapter
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ViewPagerAdapter extends FragmentStateAdapter implements colorChangeListener{
        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }
        @Override
        public Fragment createFragment(int position) {
            return new PhotoFragment(album.getAlbumItems().get(position),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            statusUiVisible = !statusUiVisible;
                            statusUiVisibility(statusUiVisible);
                        }
                    });//.setColorChangeListener(this)
        }

        @Override
        public int getItemCount() {
            return album.getAlbumItems().size();
        }

        @Override
        public void onColorChange(Integer color){
            if(chipGroup == null) {
                notifyMsg("onColorChange:chipGroup is null");
                return;
            }
            tagColor = color;
            for(int i=0;i<chipGroup.getChildCount();i++){
                View v = chipGroup.getChildAt(i);
                if(v instanceof Chip && color!=null)
                    ((Chip)v).setChipBackgroundColor
                            (ColorStateList.valueOf(color));
            }
        }
    }
    public interface colorChangeListener{
        public void onColorChange(Integer color);
    }


}
