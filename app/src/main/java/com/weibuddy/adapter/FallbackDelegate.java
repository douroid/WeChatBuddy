package com.weibuddy.adapter;

import android.support.annotation.NonNull;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.weibuddy.R;

import java.util.List;

class FallbackDelegate<T> extends AdapterDelegate<T> {

    @Override
    protected boolean isForViewType(@NonNull T items, int position) {
        return true;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new FallbackViewHolder(new Space(parent.getContext()));
    }

    @Override
    protected void onBindViewHolder(@NonNull T items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
    }

    private static class FallbackViewHolder extends BaseViewHolder {

        FallbackViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
