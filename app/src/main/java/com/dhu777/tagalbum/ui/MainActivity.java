package com.dhu777.tagalbum.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.recyclerView.MainAdapter;
import com.dhu777.tagalbum.data.Settings;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.task.colorAnalysis;
import com.dhu777.tagalbum.util.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页Activity页面组件.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private List<AlbumBucket> albums;
    private RecyclerView recyclerView;
    private MediaProvider mediaProvider;
    private MainAdapter recyclerViewAdapter;
    private boolean scanHidden = false;

    /**
     * 在该活动被创建时会被调用.
     * @param savedInstanceState 之前保存的页面状态.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Permission.checkWriteExternal(this);
        loadSettings();
        setToolbar();
        setAlbums();
        setRecyclerView();
    }

    /**
     * 在该启动被创建时会被调用.
     */
    @Override
    protected void onStart() {
        super.onStart();
        refreshPhotos();
    }

    /**
     * 从设置加载参数.
     */
    protected void loadSettings(){
        scanHidden = Settings.getInstance(this).isScanHidden();
    }

    /**
     * 配置主页的{@link RecyclerView}滚动布局.
     */
    protected void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new MainAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

    }
    /**
     * 配置主页的标题栏.
     */
    @Override
    protected void setToolbar(){
        super.setToolbar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.toolbar_title));
        }
    }


    /**
     * 为菜单渲染设置菜单项.
     * @param menu 菜单视图对象
     * @return 是否显示菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
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
            case R.id.menus_search:
                startActivity(new Intent(this,SearchActivity.class));
                return true;
            case R.id.test_a:
                new colorAnalysis(this.getApplication()).execute(albums);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlbums(){
        albums = MediaProvider.getAlbums();
        if (albums == null) {
            albums = new ArrayList<>();
        }
    }

    private void destroyMediaProvide(){
        if (mediaProvider != null) {
            mediaProvider.onDestroy();
            mediaProvider = null;
        }
    }

    private void refreshPhotos() {
        destroyMediaProvide();
        final MediaProvider.OnMediaLoadedCallback callback
                = new MediaProvider.OnMediaLoadedCallback() {
            @Override
            public void onMediaLoaded(List<AlbumBucket> albums) {
                Log.d("refreshPhotos", "onMediaLoaded: albums:"+albums.size());
                if (albums != null) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //get the Albums processed by provider
                            List<AlbumBucket> albumsNew = MediaProvider.getAlbums();
                            Log.d("refreshPhotos", "onMediaLoaded: albumsNew:"+albumsNew.size());
                            MainActivity.this.albums = albumsNew;
                            recyclerViewAdapter.setData(albumsNew);

                            Log.d("refreshPhotos", "AdapterData: "+recyclerViewAdapter.getData().size());
                            //destroy object after use
                            //albums is static
                            destroyMediaProvide();
                        }
                    });
                }
            }
        };
        mediaProvider = new MediaProvider(this);
        mediaProvider.loadAlbums(MainActivity.this, scanHidden, callback);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //todo MainActivity状态恢复
    }
}
