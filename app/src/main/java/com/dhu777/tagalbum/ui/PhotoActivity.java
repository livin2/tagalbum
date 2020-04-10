package com.dhu777.tagalbum.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.TagViewModel;
import com.dhu777.tagalbum.data.entity.Gif;
import com.dhu777.tagalbum.data.persistent.entity.Tag;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.util.ColorTag;
import com.dhu777.tagalbum.util.PaletteHelper;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * 显示大图的Activity页面组件.
 */
@Deprecated
public class PhotoActivity extends BaseActivity {
    private static final String TAG = "PhotoActivity";
    /**用于从Intent中提取序列化的对象的图片信息的KEY*/
    public static final String KEY_PHOTO = "PHOTO";
    public static final String KEY_PHOTOPOS = "PHOTO_POS";
    private DialogFragment dialogFragment;
    protected Toolbar toolbar;
    private AlbumItem photo;
    private ChipGroup chipGroup;
    private TagViewModel tagViewModel;

    /**
     * 在该活动被创建时会被调用.
     * @param savedInstanceState 之前保存的页面状态.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        chipGroup = findViewById(R.id.chip_group);


        photo = getIntent().getExtras().getParcelable(KEY_PHOTO);
        if(photo == null){
            Log.d(TAG, "onCreate: null");
            return;
        }
        Log.d(TAG, "onCreate: called");
        if(photo instanceof Gif)
            loadGif(photo);
        else
            loadPhotoTarget(photo);

        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        tagViewModel.getTagsByMedia(photo.getId()).observe(this, new Observer<List<TagView>>() {
            @Override
            public void onChanged(List<TagView> tagJoins) {
                updateChips(tagJoins);
            }
        });

        dialogFragment = new AddTagsDialogFragment(new AddTagsDialogFragment.Callback() {
            @Override
            public void confirm(String text) {
                addChip(text);
            }
        });
        setToolbar();
    }

    /**
     * 当数据库中的标签数据变动时更新视图中显示的标签.
     * @param tagJoins 图片-标签关系对象的集合
     */
    protected void updateChips(List<TagView> tagJoins){
        chipGroup.removeAllViews();
        for(TagView tj:tagJoins){
            Chip chip = (Chip) getLayoutInflater()
                    .inflate(R.layout.item_chip,chipGroup,false);
            String val =String.valueOf(tj.getTagVal());
            chip.setText(val);
            chip.setTag(tj);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagView tv = (TagView) v.getTag();
                    if(tv!=null){
                        tagViewModel.deleteTagJoin(tv.getMediaId(),tv.getTagId());
                    }
                }
            });
            chipGroup.addView(chip);
        }
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

    /**
     * 添加标签
     * @param text 标签值
     */
    protected void addChip(String text){
        if(chipExist(text))
            return;

        final Chip chip = (Chip) getLayoutInflater()
                .inflate(R.layout.item_chip,chipGroup,false);

        if(photo==null || chip== null ||chipGroup ==null){
            notifyMsg("失败");
            return;
        }

        tagViewModel.insertTagForMedia(photo,text);
//        chip.setText(text);
//        chip.setOnCloseIconClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chipGroup.removeView(chip);
//            }
//        });
//        chipGroup.addView(chip);
    }

    /**
     * 配置标题栏.
     */
    protected void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && photo!=null) {
            actionBar.setTitle(photo.getName());
        }
    }

    /**
     * 加载图片.图片为静态图片时调用.
     * @param item 图片信息对象
     */
    protected void loadPhoto(AlbumItem item){
        PhotoView photoView = findViewById(R.id.photo_view);
        Glide.with(this)
                .asBitmap()
                .load(item.getPath())
                .apply(item.getGlideRequestOptions(photoView.getContext()))
                .into(photoView);
    }

    protected void loadPhotoTarget(AlbumItem item){
//        final PhotoView photoView = findViewById(R.id.photo_view);
//        Glide.with(this)
//                .asBitmap()
//                .load(item.getPath())
//                .apply(item.getGlideRequestOptions(photoView.getContext()))
//                .into(new CustomTarget<Bitmap>(){
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        photoView.setImageBitmap(resource);
//                        PaletteHelper.getDomin(resource, new PaletteHelper.getDominCallback() {
//                            @Override
//                            public void onLoaded(@NonNull Palette.Swatch swatch) {
//                                toolbar.setBackgroundColor(swatch.getRgb());
//                                String msg = ColorTag.tagOf(swatch);
//                                Log.d(TAG, "colorTag:"+swatch);
//                                Log.d(TAG, "colorTag:"+msg);
//                                notifyMsg(msg);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });

        Log.d(TAG, "loadPhotoTarget: called");
        loadPhoto(item);
        Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
        PaletteHelper.getDomin(bitmap, new PaletteHelper.getDominCallback() {
            @Override
            public void onLoaded(@NonNull Palette.Swatch swatch) {
                toolbar.setBackgroundColor(swatch.getRgb());
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
        PhotoView photoView = findViewById(R.id.photo_view);
        Glide.with(this)
                .asGif()
                .load(item.getPath())
                .apply(item.getGlideRequestOptions(photoView.getContext()))
                .into(photoView);
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

    /**
     * 处理菜单项的选择操作.
     * @param item 被选择的菜单项
     * @return 选择操作是否被处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add_tags:
                dialogFragment.show(getSupportFragmentManager(),"miss");
                return true;
            case R.id.test_a:
                return true;

            case R.id.test_b:
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }
}
