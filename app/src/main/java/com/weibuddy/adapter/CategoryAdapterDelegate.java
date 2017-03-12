package com.weibuddy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.weibuddy.Category;
import com.weibuddy.CategoryEnum;
import com.weibuddy.R;
import com.weibuddy.util.ViewUtils;

import java.util.List;

class CategoryAdapterDelegate extends AdapterDelegate<List<Category>> {

    @Override
    protected boolean isForViewType(@NonNull List<Category> items, int position) {
        return true;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Category> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (holder instanceof FolderViewHolder) {
            final Category category = items.get(position);
            final FolderViewHolder vh = FolderViewHolder.class.cast(holder);

            String name = category.getName();
            try {
                CategoryEnum categoryEnum = CategoryEnum.valueOf(category.getName());
                name = categoryEnum.value;
            } catch (Exception e) {
                //ignore
            }
            vh.name.setText(name);
            vh.fresh.setVisibility(category.getFresh() == 1 ? View.VISIBLE : View.GONE);
            vh.counter.setText(vh.getContext().getString(R.string.child_count, category.getChildCount()));
        }
    }

    private static class FolderViewHolder extends BaseViewHolder {

        final TextView name;
        final ImageView fresh;
        final TextView counter;

        FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            name = ViewUtils.findViewById(itemView, R.id.name);
            fresh = ViewUtils.findViewById(itemView, R.id.fresh);
            counter = ViewUtils.findViewById(itemView, R.id.counter);
        }
    }
}
