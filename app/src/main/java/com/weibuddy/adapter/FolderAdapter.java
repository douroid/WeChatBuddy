package com.weibuddy.adapter;

import com.weibuddy.Folder;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends ListRefreshableAdapter<Folder, List<Folder>> {

    public FolderAdapter() {
        addDelegate(new FolderAdapterDelegate());

        setItems(new ArrayList<Folder>());
    }

}
