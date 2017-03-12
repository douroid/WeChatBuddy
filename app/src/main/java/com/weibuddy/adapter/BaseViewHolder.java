package com.weibuddy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class BaseViewHolder extends RecyclerView.ViewHolder {

    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
