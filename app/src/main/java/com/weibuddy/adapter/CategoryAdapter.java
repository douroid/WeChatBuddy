package com.weibuddy.adapter;

import com.weibuddy.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ListRefreshableAdapter<Category, List<Category>> {

    public CategoryAdapter() {
        addDelegate(new CategoryAdapterDelegate());

        setItems(new ArrayList<Category>());
    }

    public Category getItem(int position) {
        return items.get(position);
    }
}
