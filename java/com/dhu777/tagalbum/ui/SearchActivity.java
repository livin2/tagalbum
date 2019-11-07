package com.dhu777.tagalbum.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dhu777.tagalbum.R;
import com.dhu777.tagalbum.adapter.recyclerView.AlbumAdapter;
import com.dhu777.tagalbum.data.Settings;
import com.dhu777.tagalbum.data.entity.AlbumBucket;
import com.dhu777.tagalbum.data.TagViewModel;
import com.dhu777.tagalbum.data.persistent.entity.TagView;
import com.dhu777.tagalbum.data.provider.MediaProvider;
import com.dhu777.tagalbum.data.provider.MediaSearcher;
import com.dhu777.tagalbum.util.Permission;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理标签搜索的Activity页面组件.
 */
public class SearchActivity extends BaseActivity implements Observer<List<TagView>>{
    private static final String TAG = "SearchActivity";
    private static final int CHIP_INDEX_KEY = 1;
    private List<String> querylist;
    private TagViewModel tagViewModel;
    private MediaSearcher mediaSearcher;
    private RecyclerView recyclerView;
    private AlbumAdapter recyclerViewAdapter;
    private AlbumBucket albumdata;
    private int columnCount;

    /**
     * 在该活动被创建时会被调用.
     * @param savedInstanceState 之前保存的页面状态.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Permission.checkWriteExternal(this);
        loadSettings();

        mediaSearcher = new MediaSearcher();
        querylist = new ArrayList<>();

        setToolbar();
        setRecyclerView();
        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        tagViewModel.getTagJoinByTagList(querylist).observe(this, this);

        if(albumdata==null)
            albumdata = new AlbumBucket().setTitle("搜索中");
        recyclerViewAdapter.setData(albumdata);
    }

    /**
     * ViewModel回调接口的实现.从数据库查询图片-标签关联的结果变动时更新显示的图片.
     * @param tagViews 查询标签的结果
     */
    @Override
    public void onChanged(List<TagView> tagViews) {
        Log.d(TAG, "onChanged: dataSize:"+tagViews.size());
        Log.d(TAG, "onChanged: querylist:"+querylist.size());
        for(String val:querylist)
            Log.d(TAG, "onChanged: queryi:"+val);
        if(tagViews.size()<=0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerViewAdapter.setData(new AlbumBucket());
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        Log.d(TAG, "onChanged: call");
        mediaSearcher.setCallback(new MediaProvider.OnMediaLoadedCallback() {
            @Override
            public void onMediaLoaded(List<AlbumBucket> albums) {
                albumdata = albums.get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.setData(albumdata);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).loadSearch(this,tagViews);
    }

    /**
     * 配置主页的{@link RecyclerView}滚动布局.
     */
    protected void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new AlbumAdapter(this,0).setViewPage(false);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
    }

    /**
     * 从设置加载参数.
     */
    protected void loadSettings(){
        columnCount = Settings.getInstance(this).getColumnCount();
    }

    /**
     * 配置主页的标题栏.
     */
    protected void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    private ViewGroup getSearchPlate(SearchView parent){
        ViewGroup viewGroup = parent.findViewById(R.id.search_plate);
        if(viewGroup != null)
            return viewGroup;

        int id = parent.getContext().getResources()
                .getIdentifier("android:id/search_edit_frame", null, null);
        return parent.findViewById(id);
    }

    /**
     * 标签是否已存在在搜索栏中.
     * @param val 标签值
     * @return 标签是否已存在在搜索栏中
     */
    protected boolean isChipExist(String val){
        for (String q:querylist){
            if(q.equals(val)) return true;
        }
        return false;
    }

    /**
     * 当某个已输入标签从搜索栏移除时调用.减少搜索条件,增加搜索结果.
     * @param chip
     */
    protected void onChipRemove(Chip chip){
        int index = (Integer) chip.getTag();
        Log.d(TAG, "onChipRemove: tag index:"+index);
        String chipVal = chip.getText().toString();
        if(index<querylist.size() && querylist.get(index).equals(chipVal)){
            querylist.remove(index);
        }else {
            for (int i=querylist.size()-1;i>0;i--)
                if(querylist.get(i).equals(chipVal))
                    querylist.remove(i);
        }
        Log.d(TAG, "onChipRemove:size:"+querylist.size());
        //todo remove search result
        tagViewModel.getTagJoinByTagList(querylist);
    }

    /**
     * 当某个标签加入搜索栏时调用.细化搜索条件,移除搜索结果.
     * @param chip
     */
    protected void onChipAdd(Chip chip){
        Integer index = querylist.size();
        chip.setTag(index);
        querylist.add(chip.getText().toString());
        tagViewModel.getTagJoinByTagList(querylist);
        Log.d(TAG, "onChipAdd:size:"+querylist.size());
    }

    /**
     * 设置搜索栏组件布局在其中添加渲染能增删标签的布局组件.
     * @param parent 父布局
     */
    protected void setSearchChipBar(final SearchView parent){
        //todo findViewById checkNotNULL
        final AppCompatImageView btn = parent.findViewById(R.id.search_close_btn);
        final ViewGroup searchPlate = parent.findViewById(R.id.search_plate);
        final SearchView.SearchAutoComplete searchAutoComplete
                = parent.findViewById(R.id.search_src_text);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView)getLayoutInflater()
                .inflate(R.layout.embedded_scroll,searchPlate,false);
        final LinearLayout linearInner = horizontalScrollView.findViewById(R.id.inner_liner);

        searchPlate.removeView(searchAutoComplete);
        linearInner.addView(searchAutoComplete);
        searchPlate.addView(horizontalScrollView,0);

        searchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction: actionId:"+actionId);
                Log.d(TAG, "onEditorAction: KeyEvent:"+event);
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_GO){
                    if(isChipExist(v.getText().toString())){
                        notifyMsg("条件已输入");
                        return true;
                    }

                    final Chip chip = (Chip) getLayoutInflater()
                            .inflate(R.layout.item_chip,linearInner,false);
                    chip.setText(v.getText());
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onChipRemove((Chip)v);
                            linearInner.removeView(v);
//                            chipCounter--;
                        }
                    });
                    linearInner.addView(chip,querylist.size());
                    onChipAdd(chip);
                    v.getEditableText().clear();
                    horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//                    btn.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        searchAutoComplete.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(searchAutoComplete.getText().length()<=0){
                    if(querylist.size()>0 && keyCode == KeyEvent.KEYCODE_DEL &&
                        event.getAction()==KeyEvent.ACTION_DOWN){
                            Log.d(TAG, "onKey:q size:"+querylist.size());
                            Log.d(TAG, "onKey:l size:"+linearInner.getChildCount());

                            Chip chip = (Chip)(linearInner.getChildAt(querylist.size()-1));
                            onChipRemove(chip);
                            linearInner.removeView(chip);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 为菜单渲染设置菜单项.
     * @param menu 菜单视图对象
     * @return 是否显示菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocusFromTouch();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(new ComponentName(SearchActivity.this,AlbumActivity.class)));

        setSearchChipBar(searchView);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaSearcher.onDestroy();
    }
}
