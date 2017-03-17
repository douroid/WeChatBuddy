package com.weibuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.weibuddy.adapter.ContentAdapter;
import com.weibuddy.dao.ContentDao;
import com.weibuddy.dao.DaoSession;
import com.weibuddy.util.ViewUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

public class ContentActivity extends AppBaseCompatActivity {

    private ContentAdapter mAdapter;
    private ContentDao mContentDao;

    private String mId;
    private String mType;

    public static void start(Context context, String id, String type) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Intent.EXTRA_UID, id);
        intent.putExtra(Intent.EXTRA_TITLE, type);
        context.startActivity(intent);
    }

    @Override
    protected int layout() {
        return R.layout.activity_category;
    }

    @Override
    protected void setUpArguments() {
        final Intent intent = getIntent();
        mId = intent.getStringExtra(Intent.EXTRA_UID);
        mType = intent.getStringExtra(Intent.EXTRA_TITLE);
    }

    @Override
    protected void setUpDaoSession() {
        DaoSession daoSession = ((WeiBuddyApp) getApplication()).getDaoSession();
        mContentDao = daoSession.getContentDao();
    }

    @Override
    protected void setUpViews() {
        String typeName = mType;
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(mType);
            typeName = categoryEnum.value;
        } catch (Exception e) {
            //ignore
        }
        setTitle(typeName);

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
        mAdapter = new ContentAdapter(this);
        recyclerView.setAdapter(mAdapter);

        ViewUtils.addOnGlobalLayoutListener(recyclerView, new Runnable() {
            @Override
            public void run() {
                syncLoad();
            }
        });
    }

    private void syncLoad() {
        List<Content> contents = mContentDao.queryBuilder()
                .where(ContentDao.Properties.Fid.eq(mId), ContentDao.Properties.Cname.eq(mType))
                .build()
                .list();
        mAdapter.refresh(contents);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.onDestroy();
    }
}
