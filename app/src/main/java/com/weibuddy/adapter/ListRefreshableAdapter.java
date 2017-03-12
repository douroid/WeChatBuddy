package com.weibuddy.adapter;

import com.hannesdorfmann.adapterdelegates3.AbsDelegationAdapter;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

import java.util.List;

public class ListRefreshableAdapter<D, T extends List<D>> extends AbsDelegationAdapter<T> {

    public ListRefreshableAdapter() {
        setFallbackDelegate(new FallbackDelegate<T>());
    }

    protected void addDelegate(AdapterDelegate<T> delegate) {
        this.delegatesManager.addDelegate(delegate);
    }

    protected void setFallbackDelegate(AdapterDelegate<T> delegate) {
        this.delegatesManager.setFallbackDelegate(delegate);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public D getItem(int index) {
        return items.get(index);
    }

    public void refresh(T items) {
        setItems(items);
        notifyDataSetChanged();
    }
}
