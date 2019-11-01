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
import com.dhu777.tagalbum.data.entity.PathAlbum;

import java.io.File;
import java.util.ArrayList;

/**
 * MediaRetriever通过系统MediaStore.Files的Api,扫描图片文件并封装成数据结构提供给MediaProvider。
 */
public class MediaRetriever{
    private static final String TAG = "MediaRetriever";
    private static final String VOLUME_EXTERNAL = "external";
    private static final String selection = MediaStore.Files.FileColumns.MEDIA_TYPE
            + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    private static final String[] projection = new String[]{
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            BaseColumns._ID};

    private MediaProvider.OnMediaLoadedCallback callback;

    /**
     * 异步扫描图片完成后的回调
     * @param callback 回调
     * @return 对象本身this
     */
    private MediaRetriever setCallback(MediaProvider.OnMediaLoadedCallback callback) {
        this.callback = callback;
        return this;
    }

    public void onDestroy() {
        setCallback(null);
    }


    /**
     * 从MediaStore获取信息，并执行一个异步任务，在后台读取文件并封装成数据结构
     * {@link AlbumItem}
     * @param context Android Activity上下文
     * @param scanHidden 是否扫描包含.nomedia文件的文件夹
     * @param callback  加载媒体回调
     */
    public void loadAlbums(final Activity context, final boolean scanHidden,
                           final MediaProvider.OnMediaLoadedCallback callback) {
        setCallback(callback);
        Uri uri = MediaStore.Files.getContentUri(VOLUME_EXTERNAL);
        CursorLoader cursorLoader = new CursorLoader(
                context,
                uri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED);
        final Cursor cursor = cursorLoader.loadInBackground();
        if (cursor == null) {
            Log.d(TAG, "loadAlbums: cursor==null");
            return;
        }

        if (!scanHidden) {
            //todo 去掉nomedia文件夹
        }
        Asyncload(cursor);
    }

    /**
     * 将MediaStore检索出的图片信息映射为model，异步进行
     * @param cursor MediaStore检索返回结果
     */
    private void Asyncload(final Cursor cursor){
        final long startTime = System.currentTimeMillis();
        final ArrayList<AlbumBucket> albums = new ArrayList<>();
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
                    final int bucketIdColumn = cursor
                            .getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID);

                    String path,mimeType,name;
                    long id,bucketId,dateTaken;
                    Uri uri;
                    do {
                        mimeType = cursor.getString(mimeTypeColumn);
                        AlbumItem albumItem = AlbumItem.getInstance(mimeType);
                        if(albumItem != null){
                            path = cursor.getString(pathColumn);
                            name = cursor.getString(nameColumn);
                            id = cursor.getLong(idColumn);
                            dateTaken = cursor.getLong(dateTakenColumn);
                            bucketId = cursor.getLong(bucketIdColumn);
                            uri = ContentUris.withAppendedId(
                                    MediaStore.Files.getContentUri(VOLUME_EXTERNAL), id);

                            albumItem.setPath(path).setBucketId(bucketId).setDateTaken(dateTaken)
                                    .setUri(uri);
                            albumItem.setId(id);
                            albumItem.setName(name);

                            addToPathAlbums(albumItem,albums);
                        }
                        counter++;
                    }while (cursor.moveToNext());
                }
                cursor.close();

                if (callback != null) {
                    Log.d(TAG, "AsyncTask run callback");
                    callback.onMediaLoaded(albums);
                }

                Log.d(TAG, "Asyncload(): "
                        + counter
                        + " items");
                //todo test Asyncload time
                Log.d(TAG, "Asyncload(): "
                        + String.valueOf(System.currentTimeMillis() - startTime)
                        + " ms");
            }
        });

    }

    /**
     * 将图片放入按文件夹分类的相册集合中
     * @param albumItem 图片
     * @param albums 相册集合
     */
    private void addToPathAlbums(AlbumItem albumItem,ArrayList<AlbumBucket> albums){
        boolean bucketExist = false;
        for (AlbumBucket buk:albums){
            if(buk.getBucketId()==albumItem.getBucketId()){
                buk.getAlbumItems().add(0,albumItem);
                bucketExist = true;
                break;
            }
        }
        if (!bucketExist){
            String parentPath = new File(albumItem.getPath()).getParent();
            String parentName = new File(parentPath).getName();
            albums.add(new PathAlbum().setPath(parentPath)
                    .setTitle(parentName).setBucketId(albumItem.getBucketId()));
            albums.get(albums.size() - 1).getAlbumItems().add(0, albumItem);
        }
    }
}
