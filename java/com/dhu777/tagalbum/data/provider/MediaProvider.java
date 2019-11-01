package com.dhu777.tagalbum.data.provider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dhu777.tagalbum.data.Settings;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.util.Permission;

import java.util.List;

/**
 * 提供按目录分类的图片对象集合
 */
public class MediaProvider {
    public static final String TAG = "MediaProvider";
    private MediaRetriever retriever;
    private static List<AlbumBucket> albums;
    public static boolean isDataChanged;

    public MediaProvider(Context context){
        retriever = new MediaRetriever();
        isDataChanged = true;
    }

    /**
     * 数据加载完成的回调接口
     */
    public interface OnMediaLoadedCallback {
        void onMediaLoaded(List<AlbumBucket> albums);
    }


    /**
     * 检查读写权限,加载图片,如果传入回调不为空值将在图片加载完成后调用回调.
     * @param context 应用上下文
     * @param scanHidden 是否扫描隐藏文件夹
     * @param callback 回调
     */
    public void loadAlbums(final Activity context, final boolean scanHidden,
                            final OnMediaLoadedCallback callback){
        if (!Permission.checkWriteExternal(context)){
            Log.d(TAG, "loadAlbums: need Permission");
            return;
        }
        if (retriever == null && callback!=null){
            callback.onMediaLoaded(null);
        }

        Log.d(TAG, "loadAlbums:retriever.loadAlbums");
        retriever.loadAlbums(context, scanHidden, new OnMediaLoadedCallback() {
            @Override
            public void onMediaLoaded(List<AlbumBucket> albums) {
                //remove null //merge
                final int min = Settings.getInstance(context).getAlbumMininum();
                //todo others title and default value
                AlbumBucket others = new AlbumBucket().setTitle("others");
                for (int i=albums.size()-1;i>=0;i--){
                    AlbumBucket albumI = albums.get(i);
                    if(albumI==null)
                        albums.remove(i);
                    else if(albumI.getAlbumItems().size()<min){
                        Log.d("merge albums", "getAlbum("+i+"):"+albumI.getTitle());
                        others.getAlbumItems().addAll(albumI.getAlbumItems());
                        albums.remove(i);
                    }
                }
                if(others.getAlbumItems().size()>0)
                    albums.add(others);

                //todo sortAlbum && isDataChanged
                setAlbums(albums);
                if (callback != null)
                    callback.onMediaLoaded(albums);
            }
        });
    }

    /**
     * albums集合采用单例模式,缓存{@link #loadAlbums(Activity, boolean, OnMediaLoadedCallback)}加载的
     * 结果,通过{@link #isDataChanged}判断是采用缓存还是重新加载数据.
     * @return 缓存的Albums数据.
     */
    public static List<AlbumBucket> getAlbums() {
        return albums;
    }
    private static void setAlbums(List<AlbumBucket> albums) {
        MediaProvider.albums = albums;
    }

    public void onDestroy() {
        if (retriever != null) {
            retriever.onDestroy();
            retriever = null;
        }
    }

}
