package com.dhu777.tagalbum.ui.style;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhu777.tagalbum.adapter.viewHolder.AlbumHolder;

/**
 * 定义主页展示相簿的视图{@link RecyclerView}的样式的抽象类.
 */
public abstract class Style {
    /**样式类型:列表*/
    public static final int LIST = 0;

    /**
     * 抽象方法.不同的子类实现返回不同的资源.
     * @return 返回样式的视图布局资源文件.
     */
    public abstract int getItemViewLayoutRes();

    /**
     * 静态工厂方法,子类的构造器都是protected的,所以都要都过本方法实例化.
     * @param style 样式类型
     * @return 样式实例
     */
    public static Style createStyleInstance(int style){
        switch (style){
            case LIST:return new ListStyle();
            default:return null;
        }
    }

    /**
     * 调用子类的{@link #getItemViewLayoutRes()}方法获取布局资源,按该布局绘制视图,返回该视图的持有类.
     * @param parent 绘制的视图将要放置其中的父布局
     * @return 持有类实例
     */
    public RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent) {
        View v =LayoutInflater.from(parent.getContext())
                .inflate(getItemViewLayoutRes(), parent, false);
        return new AlbumHolder(v);
    }

}
