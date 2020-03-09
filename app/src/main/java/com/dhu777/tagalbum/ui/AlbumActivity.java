package com.dhu777.tagalbum.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.recyclerView.AlbumAdapter;
import com.dhu777.tagalbum.data.Settings;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.util.Permission;

/**
 * 相册Activity页面组件.本页面显示某个相簿所有的缩略图.
 */
public class AlbumActivity extends BaseActivity {
    public static final String KEY_ALBUMPOS = "ALBUMPOS";
    public static final String KEY_ALBUM = "ALBUM";
    private static final String TAG = "AlbumActivity";
    private AlbumBucket album;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AlbumAdapter recyclerViewAdapter;
    private int columnCount;
    private int albumPos;

    /**
     * 在该活动被创建时会被调用.
     * @param savedInstanceState 之前保存的页面状态.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Permission.checkWriteExternal(this);
        loadSettings();
        if(!retrieveAlbum(savedInstanceState)||album==null)
            return;
        setToolbar(album.getTitle());
        setRecyclerView();
        recyclerViewAdapter.setData(album);
    }

    private boolean handleSearch(){
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
            return true;
//            doMySearch(query);
        }
        return false;
    }

    private boolean retrieveAlbum(Bundle savedInstanceState){
        //todo AlbumActivity状态恢复
        albumPos=-1;
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_ALBUMPOS)) {
            albumPos = savedInstanceState.getInt(KEY_ALBUMPOS);
        } else {
            albumPos = getIntent().getIntExtra(KEY_ALBUMPOS,-1);
        }
        if (albumPos==-1||MediaProvider.getAlbums()==null){
            getAlbumError();
            return false;
        }
        album = MediaProvider.getAlbums().get(albumPos);
        return true;
    }

    /**
     * 退出Activity时候保存Activity实例的状态.
     * @param outState 用以保存状态的Bundle
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //todo AlbumActivity状态恢复
        //开发者模式-后台进程限制-不允许后台进程
        //从后台恢复Activity是通过相同的Intent/Bundle来进行的
        //这时MediaProvider.album的是需要重新调用loadAlbum获取的
    }

    private void getAlbumError(){
        //todo get album error

    }

    /**
     * 从设置加载参数.
     */
    protected void loadSettings(){
        columnCount = Settings.getInstance(this).getColumnCount();
    }

    /**
     * 配置{@link RecyclerView}滚动布局.
     */
    protected void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new AlbumAdapter(this,albumPos);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
    }

    /**
     * 配置标题栏.
     */
    protected void setToolbar(String title){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 处理菜单项的选择操作.主页处理左上角的回退操作.
     * @param item 被选择的菜单项
     * @return 选择操作是否被处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置{@link RecyclerView}布局的列数.
     * @param columnCount 列数.
     */
    public void setColumnCount(int columnCount) {
        if(columnCount<=0) //todo check columnCount maximum
            return;
        this.columnCount = columnCount;
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
    }
}
