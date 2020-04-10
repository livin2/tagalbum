package com.dhu777.tagalbum.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.view.ActionMode;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.recyclerView.AlbumAdapter;
import com.dhu777.tagalbum.adapter.recyclerView.selection.ActionModeController;
import com.dhu777.tagalbum.adapter.recyclerView.selection.AlbumItemDetailsLookup;
import com.dhu777.tagalbum.adapter.recyclerView.selection.AlbumItemKeyProvider;
import com.dhu777.tagalbum.data.Settings;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.entity.AlbumItem;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.util.Permission;

import java.util.Iterator;

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
    private SelectionTracker selectionTracker;
    private ActionMode actionMode;
    private MenuItem selectedItemCount;

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
        setSelectionTracker();
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

    protected void setSelectionTracker(){
        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new AlbumItemKeyProvider(1,recyclerViewAdapter.getData().getAlbumItems()),
                new AlbumItemDetailsLookup(recyclerView,recyclerViewAdapter),
                StorageStrategy.createLongStorage()
        ).withOnDragInitiatedListener(new OnDragInitiatedListener() {
            @Override
            public boolean onDragInitiated(@NonNull MotionEvent e) {
                Log.d(TAG, "onDragInitiated");
                return true;
            }
        }).build();

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = startSupportActionMode(new ActionModeController
                            (AlbumActivity.this, selectionTracker));
                    selectedItemCount.setTitle(""+selectionTracker.getSelection().size());
                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    selectedItemCount.setTitle(""+selectionTracker.getSelection().size());
                }
                Iterator<AlbumItem> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i(TAG, itemIterable.next().getName());
                }
            }
        });
        recyclerViewAdapter.setSelectionTracker(selectionTracker);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        selectedItemCount = menu.findItem(R.id.action_item_count);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_clear:
                selectionTracker.clearSelection();
                break;
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
