package com.dhu777.tagalbum.adapter.recyclerView.selection;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.adapter.recyclerView.AlbumAdapter;
import com.dhu777.tagalbum.adapter.viewHolder.AlbumItemHolder;
import com.dhu777.tagalbum.data.entity.AlbumItem;

public class AlbumItemDetailsLookup extends ItemDetailsLookup {
    public class ItemDetail extends ItemDetailsLookup.ItemDetails {
        private final int adapterPosition;
        private final AlbumItem selectionKey;

        public ItemDetail(int adapterPosition, AlbumItem selectionKey) {
            this.adapterPosition = adapterPosition;
            this.selectionKey = selectionKey;
        }

        @Override
        public int getPosition() {
            return adapterPosition;
        }

        @Nullable
        @Override
        public Object getSelectionKey() {
            return selectionKey;
        }
    }

    private final RecyclerView recyclerView;
    private final AlbumAdapter albumAdapter;

    public AlbumItemDetailsLookup(RecyclerView recyclerView, AlbumAdapter albumAdapter) {
        this.recyclerView = recyclerView;
        this.albumAdapter = albumAdapter;
    }

    public ItemDetailsLookup.ItemDetails getItemDetails(RecyclerView.ViewHolder viewHolder) {
        int pos = viewHolder.getAdapterPosition();
        return new AlbumItemDetailsLookup.ItemDetail(pos
                ,albumAdapter.getData().getAlbumItems().get(pos));
    }

    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof AlbumItemHolder) {
                return getItemDetails(viewHolder);
            }
        }
        return null;
    }
}
