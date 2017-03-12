package com.weibuddy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.weibuddy.Folder;
import com.weibuddy.R;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class FolderAdapterDelegate extends AdapterDelegate<List<Folder>> {

    @Override
    protected boolean isForViewType(@NonNull List<Folder> items, int position) {
        return true;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Folder> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof FolderViewHolder) {
            final Folder folder = items.get(position);
            final FolderViewHolder vh = FolderViewHolder.class.cast(holder);

            vh.name.setText(folder.getName());
            vh.fresh.setVisibility(folder.getFresh() == 1 ? View.VISIBLE : View.GONE);
        }
    }

    private static class FolderViewHolder extends BaseViewHolder {

        final TextView name;
        final ImageView fresh;

        FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            name = ViewUtils.findViewById(itemView, R.id.name);
            fresh = ViewUtils.findViewById(itemView, R.id.fresh);

        }
    }
}
