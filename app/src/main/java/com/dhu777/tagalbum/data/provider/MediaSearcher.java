package com.dhu777.tagalbum.data.provider;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.CursorLoader;

import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.persistent.entity.TagView;

import java.util.ArrayList;
import java.util.List;

/**
 * 从媒体库中搜索符合特定条件的图片.
 */
public class MediaSearcher {
    private static final String TAG = "MediaSearcher";
    private static final String VOLUME_EXTERNAL = "external";
    private static final String selectionPre = MediaStore.Files.FileColumns.MEDIA_TYPE
            + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE+" AND "+
            BaseColumns._ID + " IN ";
    private static final String[] projection = new String[]{
            BaseColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATE_TAKEN};

    private MediaProvider.OnMediaLoadedCallback callback;
    /**
     * 异步搜索图片完成后的回调
     * @param callback 回调
     * @return 对象本身this
     */
    public MediaSearcher setCallback(MediaProvider.OnMediaLoadedCallback callback) {
        this.callback = callback;
        return this;
    }

    public void onDestroy() {
        setCallback(null);
    }

    private String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2);
        sb.append("?");
        for (int i = 1; i < len; i++)
            sb.append(",?");
        return sb.toString();
    }

    /**
     * 查找图片id在tagViewList集合中的图片信息.
     * @param context 应用上下文
     * @param tagViewList 满足一定条件的图片id的集合.
     */
    public void loadSearch(final Activity context, List<TagView> tagViewList){
        //todo Setting maximum of search result
        int num = tagViewList.size()>999?999:tagViewList.size();
        String selection = selectionPre + "(" + makePlaceholders(num) + ")";
        String[] selectionArgs = new String[num];
        for (int i = 0; i < num; i++)
            selectionArgs[i] = String.valueOf(tagViewList.get(i).getMediaId());
        Uri uri = MediaStore.Files.getContentUri(VOLUME_EXTERNAL);
        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED);
        final Cursor cursor = cursorLoader.loadInBackground();
        if (cursor == null) {
            Log.d(TAG, "loadAlbums: cursor==null");
            return;
        }
        Asyncload(cursor);
    }

    private void Asyncload(final Cursor cursor){
        final long startTime = System.currentTimeMillis();

        final List<AlbumBucket> albums = new ArrayList<>();
        albums.add(new AlbumBucket());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                if(cursor.moveToFirst()){
                    final int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    final int mimeTypeColumn = cursor
                            .getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);
                    final int nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                    final int idColumn = cursor.getColumnIndex(BaseColumns._ID);
                    final int dateTakenColumn = cursor
                            .getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);

                    String path,mimeType,name;
                    long id,dateTaken;
                    Uri uri;
                    do {
                        mimeType = cursor.getString(mimeTypeColumn);
                        AlbumItem albumItem = AlbumItem.getInstance(mimeType);
                        if(albumItem != null){
                            path = cursor.getString(pathColumn);
                            name = cursor.getString(nameColumn);
                            id = cursor.getLong(idColumn);
                            dateTaken = cursor.getLong(dateTakenColumn);
                            uri = ContentUris.withAppendedId(
                                    MediaStore.Files.getContentUri(VOLUME_EXTERNAL), id);

                            albumItem.setPath(path).setDateTaken(dateTaken)
                                    .setUri(uri);
                            albumItem.setId(id);
                            albumItem.setName(name);

                            albums.get(0).getAlbumItems().add(albumItem);
                        }
                        counter++;
                    }while (cursor.moveToNext());
                }
                cursor.close();

                if (callback != null) {
                    Log.v(TAG, "AsyncTask run callback");
                    callback.onMediaLoaded(albums);
                }

                Log.v(TAG, "Asyncload(): "
                        + counter
                        + " items");
                //todo test Asyncload time
                Log.v(TAG, "Asyncload(): "
                        + String.valueOf(System.currentTimeMillis() - startTime)
                        + " ms");
            }
        });
    }
}
