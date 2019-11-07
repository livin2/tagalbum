package com.dhu777.tagalbum.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.util.Permission;

import static com.dhu777.tagalbum.ui.AlbumActivity.KEY_ALBUMPOS;
import static com.dhu777.tagalbum.ui.PhotoActivity.KEY_PHOTO;
import static com.dhu777.tagalbum.ui.PhotoActivity.KEY_PHOTOPOS;

public class ViewPageActivity extends BaseActivity {
    private static final String TAG = "ViewPageActivity";
    private AlbumBucket album;
    private AlbumItem photo;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private boolean statusUiVisible = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);
        Permission.checkWriteExternal(this);

        if(!retrieveAlbum(savedInstanceState)||album==null){
            Log.d(TAG, "onCreate: album null");
            return;
        }

        photo = getIntent().getExtras().getParcelable(KEY_PHOTO);
        int pos = getIntent().getExtras().getInt(KEY_PHOTOPOS);
        if(photo == null){
            Log.d(TAG, "onCreate:photo null");
            return;
        }

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(pos);
        setToolbar();
        adaptBarUI();
    }

    @Override
    protected void setToolbar() {
        super.setToolbar();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void adaptBarUI(){
        final ViewGroup rootView = findViewById(R.id.root);
        rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                toolbar.setPadding(toolbar.getPaddingStart() + insets.getSystemWindowInsetLeft(),
                        toolbar.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        toolbar.getPaddingEnd() + insets.getSystemWindowInsetRight(),
                        toolbar.getPaddingBottom());
                rootView.setOnApplyWindowInsetsListener(null);
                return insets.consumeSystemWindowInsets();
            }
        });
    }

    private boolean retrieveAlbum(Bundle savedInstanceState){
        //todo 状态恢复
        int pos = -1;
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_ALBUMPOS)) {
            pos = savedInstanceState.getInt(KEY_ALBUMPOS);
        } else {
            pos = getIntent().getIntExtra(KEY_ALBUMPOS,-1);
        }
        if (pos == -1 || MediaProvider.getAlbums()==null){
            //get error
            return false;
        }
        album = MediaProvider.getAlbums().get(pos);
        Log.d(TAG, "retrieveAlbum:"+album.getAlbumItems().size());
        return true;
    }

    @Override
    public void statusUiVisibility(boolean show) {
        super.statusUiVisibility(show);
        float toolbar_translationY = show ? 0 : -(toolbar.getHeight());
        toolbar.animate()
                .translationY(toolbar_translationY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:onBackPressed();return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ViewPagerAdapter extends FragmentStateAdapter {
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
            });
        }
        @Override
        public int getItemCount() {
            return album.getAlbumItems().size();
        }
    }
}
