package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.weibuddy.adapter.CategoryAdapter;
import com.weibuddy.dao.CategoryDao;
import com.weibuddy.dao.DaoSession;
import com.weibuddy.util.ViewUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

public class CategoryActivity extends AppBaseCompatActivity
        implements OnItemTouchListener.OnItemClickListener {

    private CategoryAdapter mAdapter = new CategoryAdapter();
    private CategoryDao mCategoryDao;

    private String mId;
    private String mTitle;

    public static void start(Context context, String id, String name) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(Intent.EXTRA_UID, id);
        intent.putExtra(Intent.EXTRA_TITLE, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setUpArguments();
        setUpDaoSession();
        setUpViews();
        syncLoad();
    }

    private void setUpArguments() {
        final Intent intent = getIntent();
        mId = intent.getStringExtra(Intent.EXTRA_UID);
        mTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
    }

    private void setUpDaoSession() {
        DaoSession daoSession = ((WeiBuddyApp) getApplication()).getDaoSession();
        mCategoryDao = daoSession.getCategoryDao();
    }

    private void setUpViews() {
        Toolbar toolbar = ViewUtils.findViewById(this, R.id.toolbar);
        TextView title = ViewUtils.findViewById(this, R.id.title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(mTitle);

        RecyclerView recyclerView = ViewUtils.findViewById(this, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .colorResId(R.color.divider_color)
                        .size(1)
                        .marginResId(R.dimen.left_distance, R.dimen.right_distance)
                        .build()
        );
        recyclerView.setAdapter(mAdapter);
        new OnItemTouchListener()
                .setOnItemClickListener(this)
                .attachToRecyclerView(recyclerView);
    }

    private void syncLoad() {
        List<Category> categories = mCategoryDao.queryBuilder()
                .where(CategoryDao.Properties.Fid.eq(mId))
                .build()
                .list();

        mAdapter.refresh(categories);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position) {
        final Category category = mAdapter.getItem(position);
        if (category.getChildCount() > 0) {
            ContentActivity.start(this, category.getFid(), category.getName());

            category.setFresh(Config.STATE_NORMAL);
            mCategoryDao.updateInTx(category);
            mAdapter.notifyItemChanged(position);
        }
    }
}
