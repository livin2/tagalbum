package com.dhu777.tagalbum.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.TagViewModel;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.entity.TagJoin;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.data.persistent.repository.AsyncRepository;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.util.Permission;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import static com.dhu777.tagalbum.ui.AlbumActivity.KEY_ALBUMPOS;

public abstract class PhotoAbsActivity extends BaseActivity{
    protected static final String TAG = "PhotoAbsActivity";
    public static final String KEY_PHOTO = "PHOTO";
    public static final String KEY_PHOTOPOS = "PHOTO_POS";

    protected AlbumItem photo;
    protected int photoPos;
    protected AlbumBucket album;
    protected TagViewModel tagViewModel;
    protected  ChipGroup chipGroup;

    protected boolean statusUiVisible = true;
    protected int duration = 200;
    protected Integer tagColor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Permission.checkWriteExternal(this);
        if(!retrieveAlbum(savedInstanceState)||album==null){
            Log.d(TAG, "onCreate:get album:null");
        }

        photo = getIntent().getExtras().getParcelable(KEY_PHOTO);
        photoPos = getIntent().getExtras().getInt(KEY_PHOTOPOS);
        if(photo == null){
            Log.d(TAG, "onCreate:get photo:null");
        }

        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        loadTags(photo.getId());
        setToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(photo==null)
            photo = getIntent().getExtras().getParcelable(KEY_PHOTO);
        if(photo!=null){
            loadMedia(photo);
        }
    }

    protected void loadTags(long mediaId){
        Log.d(TAG, "loadTags:"+tagViewModel);
        tagViewModel.getTagsByMedia(mediaId).observe(this, new Observer<List<TagView>>() {
            @Override
            public void onChanged(List<TagView> tagJoins) {
                updateChips(tagJoins);
            }
        });
    }

    @Override
    protected void setToolbar() {
        super.setToolbar();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (actionBar != null && photo!=null) {
            actionBar.setTitle(photo.getName());
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

    @Override
    public void statusUiVisibility(final boolean show) {
        super.statusUiVisibility(show);
        float toolbar_translationY = show ? 0 : -(toolbar.getHeight());
        toolbar.animate()
                .translationY(toolbar_translationY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        if(chipGroup == null) return;
        float chipGroup_transparent = show ? 1f : 0f;
        chipGroup.animate()
                .alpha(chipGroup_transparent)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!show) chipGroup.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(show) chipGroup.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected boolean retrieveAlbum(Bundle savedInstanceState){
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

    /**
     * 为菜单渲染设置菜单项.
     * @param menu 菜单视图对象
     * @return 是否显示菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add_tags:
                showDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(){
        new AddTagsDialogFragment(new AddTagsDialogFragment.Callback() {
            @Override
            public void confirm(String text) {
                addChip(text);
            }
        }).show(getSupportFragmentManager(),"addtag");
    }

    /**
     * 当数据库中的标签数据变动时更新视图中显示的标签.
     * @param tagJoins 图片-标签关系对象的集合
     */
    protected void updateChips(List<TagView> tagJoins) {
        if(chipGroup == null) {
            notifyMsg("updateChips:chipGroup is null");
            return;
        }
        chipGroup.removeAllViews();
        for(TagView tj:tagJoins){
            Chip chip = (Chip) getLayoutInflater()
                    .inflate(R.layout.item_chip,chipGroup,false);
            String val =String.valueOf(tj.getTagVal());
            if(tagColor!=null)
                chip.setChipBackgroundColor(ColorStateList.valueOf(tagColor));
            chip.setText(val);
            chip.setTag(tj);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagView tv = (TagView) v.getTag();
                    if(tv!=null){
//                        tagViewModel.deleteTagJoin(tv.getMediaId(),tv.getTagId());
                        AsyncRepository.getInstance(getApplicationContext())
                                .deleteTagJoin(new TagJoin(tv.getMediaId(),tv.getTagId()));
                    }
                }
            });
            chipGroup.addView(chip);
        }
    }

    /**
     * 添加标签
     * @param text 标签值
     */
    protected void addChip(String text) {
        if(chipExist(text))
            return;
        final Chip chip = (Chip) getLayoutInflater()
                .inflate(R.layout.item_chip,chipGroup,false);
        if(photo==null || chip == null ||chipGroup ==null){
            notifyMsg("addChip:get null object");
            return;
        }
//        tagViewModel.insertTagForMedia(photo,text);
        AsyncRepository.getInstance(getApplicationContext()).insertTagForMedia(photo,text);
    }

    /**
     * 检查标签值是否已经存在
     * @param text 标签值
     * @return 标签值是否已经存在
     */
    protected boolean chipExist(String text){
        if(chipGroup!=null){
            for (int i=0;i<chipGroup.getChildCount();i++){
                Chip chip = (Chip) chipGroup.getChildAt(i);
                TagView tv = (TagView) chip.getTag();
                if(tv!=null && tv.getTagVal().equals(text)){
                    notifyMsg("标签已存在");
                    chip.setChecked(true);
                    return true;
                }
            }
        }
        return false;
    }


    protected abstract void loadMedia(AlbumItem item);
}
