package com.dhu777.tagalbum.data;
import android.content.Context;

import com.dhu777.tagalbum.ui.style.Style;

/**
 * 应用各种设置的对象映射类.
 */
public class Settings {
    private static Settings instance;
    private Settings(Context context){ }//todo init val
    public static Settings getInstance(Context context){
        if (instance == null)
            instance = new Settings(context);

        //todo Settings
        return instance;
    }

    private int albumMininum = 2;
    private int columnCount = 3;
    private int style = Style.LIST;
    private boolean scanHidden = false;

    public int getStyle(){
        return style;
    }

    public int getAlbumMininum() {
        return albumMininum;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean isScanHidden() {
        return scanHidden;
    }
}
