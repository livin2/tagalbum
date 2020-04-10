package com.dhu777.tagalbum.adapter.recyclerView.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.dhu777.tagalbum.data.entity.AlbumItem;

import java.util.List;

public class AlbumItemKeyProvider extends ItemKeyProvider {

    private final List<AlbumItem> itemList;
    public AlbumItemKeyProvider(int scope, List<AlbumItem> itemList) {
        super(scope);
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return itemList.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return itemList.indexOf(key);
    }
}
