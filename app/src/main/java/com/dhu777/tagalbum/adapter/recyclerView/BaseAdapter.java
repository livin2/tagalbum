package com.dhu777.tagalbum.adapter.recyclerView;


import androidx.recyclerview.widget.RecyclerView;

/**
 * 继承自{@link RecyclerView.Adapter}
 * 抽象类用来定义应用中的RecyclerView.Adapter都需要的操作
 * @param <T> Adapter数据的类型
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter {
    private T data;

    /**
     * @return 获取Adapter的数据
     */
    public T getData() {
        return data;
    }

    /**
     * 构造器模式的setter,会调用{@link #notifyDataSetChanged()}更新数据
     * @param data 要设置的数据
     * @return 本对象this
     */
    public BaseAdapter<T> setData(T data) {
        this.data = data;
        notifyDataSetChanged();
        return this;
    }
}
