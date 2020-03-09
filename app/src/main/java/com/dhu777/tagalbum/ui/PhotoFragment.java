package com.dhu777.tagalbum.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.util.PaletteHelper;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoFragment extends Fragment {
    private final static String TAG = "PhotoFragment";
    private View.OnClickListener listener;
    private AlbumItem item;
    private PhotoViewPageActivity.colorChangeListener  colorChangeListener;

    public PhotoFragment setColorChangeListener(PhotoViewPageActivity.colorChangeListener colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
        return this;
    }

    private void setColor(int color) {
        item.setColor(color);
        colorChangeListener.onColorChange(color);
    }

    public PhotoFragment(AlbumItem item,
                         View.OnClickListener listener) {
        this.item = item;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.frag_photo,container,false);
        PhotoView photoView = v.findViewById(R.id.photo_view);
        Glide.with(this)
                .asBitmap()
                .load(item.getPath())
                .apply(item.getGlideRequestOptions(photoView.getContext()))
                .into(photoView);
        //into(BitmapTarget) to get color
        if(photoView!=null && listener!=null)
            photoView.setOnClickListener(listener);
        return v;
    }


    class BitmapTarget extends CustomTarget<Bitmap>{
        PhotoView photoView;
        BitmapTarget(PhotoView photoView){
            this.photoView = photoView;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            photoView.setImageBitmap(resource);
            PaletteHelper.getDomin(resource, new PaletteHelper.getDominCallback() {
                @Override
                public void onLoaded(@NonNull Palette.Swatch swatch) {
                    Log.d(TAG, "onLoaded:photo:"+swatch.getRgb());
                    setColor(swatch.getRgb());
                }
            });
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

        }
    }
}
