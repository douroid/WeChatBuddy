package com.weibuddy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetViewCompat;
import com.google.gson.internal.LinkedTreeMap;
import com.soundcloud.android.crop.Crop;
import com.weibuddy.adapter.FolderAdapter;
import com.weibuddy.dao.CategoryDao;
import com.weibuddy.dao.ContentDao;
import com.weibuddy.dao.DaoSession;
import com.weibuddy.dao.FolderDao;
import com.weibuddy.util.SharedPreferencesCompat;
import com.weibuddy.util.ViewUtils;
import com.weibuddy.util.glide.RoundTransformation;
import com.weibuddy.util.http.AsyncCallback;
import com.weibuddy.util.http.AsyncOkHttpClient;
import com.weibuddy.util.http.RequestException;
import com.weibuddy.widget.ScrollSwipeRefreshLayout;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.mikael.urlbuilder.UrlBuilder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppBaseCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,
        AppBottomDialogFragment.Callback,
        OnItemTouchListener.OnItemClickListener {

    private static final int REQUEST_CODE_IMAGE_CROP = 0xFF;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 0xEE;

    private BottomSheetLayout mBottomSheetLayout;
    private ScrollSwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mAvatar;

    private FolderAdapter mAdapter = new FolderAdapter();

    private FolderDao mFolderDao;
    private CategoryDao mCategoryDao;
    private ContentDao mContentDao;

    private File mAvatarTempFile = new File(Environment.getExternalStorageDirectory(), ".avatar.jpg");

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUpDaoSession() {
        DaoSession daoSession = ((WeiBuddyApp) getApplication()).getDaoSession();
        mFolderDao = daoSession.getFolderDao();
        mCategoryDao = daoSession.getCategoryDao();
        mContentDao = daoSession.getContentDao();
    }

    @Override
    protected void setUpViews() {
        setTitle(R.string.title_main);

        mBottomSheetLayout = ViewUtils.findViewById(this, R.id.bottom_sheet_layout);
        mSwipeRefreshLayout = ViewUtils.findViewById(this, R.id.swipe_refresh_layout);
        mAvatar = ViewUtils.findViewById(this, R.id.avatar);
        TextView welcome = ViewUtils.findViewById(this, R.id.welcome);
        TextView date = ViewUtils.findViewById(this, R.id.date);
        RecyclerView recyclerView = ViewUtils.findViewById(this, R.id.recycler_view);

        setUpAvatar();
        welcome.setText(getString(R.string.welcome, SharedPreferencesCompat.with(this).getNickName()));
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date), Locale.getDefault());
        date.setText(format.format(new Date()));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setScrollUpChild(recyclerView);

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

        ViewUtils.addOnGlobalLayoutListener(mSwipeRefreshLayout, new Runnable() {
            @Override
            public void run() {
                syncLoad();
            }
        });
    }

    private void setUpAvatar() {
        if (Config.hasAvatarFile()) {
            loadAvatar();
        } else {
            mAvatar.setImageResource(R.drawable.default_avatar);
        }

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBottomDialogFragment.show(getSupportFragmentManager(), MainActivity.this);
            }
        });
    }

    private void loadAvatar() {
        Glide.with(this)
                .load(Config.getAvatarFile())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .dontAnimate()
                .centerCrop()
                .transform(new RoundTransformation(MainActivity.this, 5f))
                .into(mAvatar);
    }

    private void crop(Uri source, Uri destination, int requestCode) {
        Crop.of(source, destination)
                .asSquare()
                .start(this, requestCode);
    }

    @Override
    protected boolean navigationEnabled() {
        return false;
    }

    @Override
    protected boolean settingsEnabled() {
        return true;
    }

    @Override
    protected void onSettings() {
        SettingsActivity.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CROP: {
                mBottomSheetLayout.dismissSheet();
                loadAvatar();
                break;
            }
            case REQUEST_CODE_IMAGE_CAPTURE: {
                crop(Uri.fromFile(mAvatarTempFile), Uri.fromFile(Config.getAvatarFile()), REQUEST_CODE_IMAGE_CROP);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onRefresh() {
        asyncLoad();
    }


    @Override
    public void onShowPicker() {
        MainActivityPermissionsDispatcher.showPickerWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showPicker() {
        ImagePickerSheetViewCompat sheetView = new ImagePickerSheetViewCompat.Builder(this)
                .setImageMimeTypes(ImagePickerSheetViewCompat.MimeType.JPEG, ImagePickerSheetViewCompat.MimeType.PNG)
                .setImageProvider(new ImagePickerSheetViewCompat.ImageProvider() {
                    @Override
                    public void onProvideImage(ImageView imageView, Uri imageUri, int size) {
                        Glide.with(MainActivity.this)
                                .load(imageUri)
                                .centerCrop()
                                .crossFade()
                                .into(imageView);
                    }
                })
                .setOnTileSelectedListener(new ImagePickerSheetViewCompat.OnTileSelectedListener() {
                    @Override
                    public void onTileSelected(ImagePickerSheetViewCompat.ImagePickerTile... selectedTiles) {
                        if (selectedTiles != null && selectedTiles.length > 0) {
                            for (ImagePickerSheetViewCompat.ImagePickerTile tile : selectedTiles) {
                                Uri uri = tile.getImageUri();
                                if (uri != null) {
                                    crop(uri, Uri.fromFile(Config.getAvatarFile()), REQUEST_CODE_IMAGE_CROP);
                                }
                            }
                        }
                    }
                })
                .setTitle(R.string.picker_title)
                .setButtonTitle(android.R.string.ok)
                .create();

        mBottomSheetLayout.setPeekSheetTranslation(mBottomSheetLayout.getHeight());
        mBottomSheetLayout.showWithSheetView(sheetView);
    }


    @Override
    public void onShowImageCapture() {
        MainActivityPermissionsDispatcher.showImageCaptureWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showImageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mAvatarTempFile));
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForExternalStorage(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showDeniedOrNeverAskForExternalStorage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_request_title)
                .setMessage(getString(R.string.permission_request_external_storage_message, getString(R.string.app_name)))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                })
                .show();
    }

    private void syncLoad() {
        List<Folder> folders = mFolderDao.queryBuilder().build().list();
        if (folders.isEmpty()) {
            asyncLoad();
        } else {
            mAdapter.refresh(folders);
        }
    }

    private void asyncLoad() {
        SharedPreferencesCompat sharedPrefs = SharedPreferencesCompat.with(this);

        final String url = UrlBuilder.fromString(Config.API)
                .addParameter(Config.KEY_METHOD, Config.VALUE_METHOD_LIST)
                .addParameter(Config.KEY_USER_ID, sharedPrefs.getUserId())
                .addParameter(Config.KEY_RAND_CODE, sharedPrefs.getRandCode())
                .toString();

        AsyncOkHttpClient.newInstance().get(url, new AsyncCallback<JsonFolder>(JsonFolder.class) {
            @Override
            public void onStart() {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(JsonFolder jsonFolder) {
                List<Folder> folders = new ArrayList<>();
                Set<Map.Entry<String, String>> ids = jsonFolder.ids.entrySet();
                for (Map.Entry<String, String> id : ids) {
                    String folderIndex = id.getKey();
                    String folderId = id.getValue();
                    int folderVersion = jsonFolder.versions.get(folderIndex);
                    String folderName = jsonFolder.names.get(folderIndex);
                    LinkedTreeMap<String, LinkedTreeMap<String, Object>> folderContents = jsonFolder.contents.get(folderIndex);
                    Set<String> contentTypes = folderContents.keySet();
                    for (String type : contentTypes) {
                        LinkedTreeMap<String, Object> folderContent = folderContents.get(type);

                        Category remoteCategory = new Category();
                        remoteCategory.setId(folderId.concat("_").concat(type));
                        remoteCategory.setFid(folderId);
                        remoteCategory.setName(type);

                        LinkedTreeMap<String, String> fileVersions = (LinkedTreeMap<String, String>) folderContent.get(Config.JSON_KEY_FILE_VERSION);
                        Set<Map.Entry<String, String>> fileVersionEntries = fileVersions.entrySet();
                        for (Map.Entry<String, String> fileVersionEntry : fileVersionEntries) {
                            int versionCode = Integer.valueOf(fileVersionEntry.getValue());
                            remoteCategory.setVersion(versionCode);

                            Category localCategory = mCategoryDao.queryBuilder()
                                    .where(CategoryDao.Properties.Fid.eq(folderId), CategoryDao.Properties.Name.eq(type))
                                    .build()
                                    .unique();

                            boolean isFresh = localCategory != null && localCategory.getVersion() < versionCode;
                            if (localCategory == null || isFresh) {
                                remoteCategory.setFresh(Config.STATE_FRESH);
                            } else {
                                remoteCategory.setFresh(localCategory.getFresh());
                            }
                        }

                        ArrayList<String> fileIds = (ArrayList<String>) folderContent.get(Config.JSON_KEY_FILE_ID);
                        remoteCategory.setChildCount(fileIds.size());

                        mCategoryDao.insertOrReplaceInTx(remoteCategory);

                        ArrayList<String> fileNames = (ArrayList<String>) folderContent.get(Config.JSON_KEY_FILE_NAME);
                        ArrayList<String> fileContents = (ArrayList<String>) folderContent.get(Config.JSON_KEY_FILE_CONTENT);
                        ArrayList<String> videoPics = (ArrayList<String>) folderContent.get(Config.JSON_KEY_VIDEO_PIC);

                        ArrayList<Content> contents = new ArrayList<>();
                        int length = fileIds.size();
                        for (int i = 0; i < length; i++) {
                            Content content = new Content();

                            content.setFid(folderId);
                            content.setCname(type);
                            content.setId(fileIds.get(i));
                            content.setContent(fileContents.get(i));

                            String contentName = "";
                            if (fileNames != null && !fileNames.isEmpty()) {
                                contentName = fileNames.get(i);
                            }
                            content.setName(contentName);

                            String videoPic = "";
                            if (videoPics != null && !videoPics.isEmpty()) {
                                videoPic = videoPics.get(i);
                            }
                            content.setVideoPic(videoPic);

                            contents.add(content);
                        }

                        mContentDao.insertOrReplaceInTx(contents, true);
                    }

                    Folder localFolder = mFolderDao.queryBuilder()
                            .where(FolderDao.Properties.Id.eq(folderId))
                            .build()
                            .unique();

                    Folder remoteFolder = new Folder();
                    remoteFolder.setId(folderId);
                    remoteFolder.setName(folderName);
                    remoteFolder.setVersion(folderVersion);

                    boolean isFresh = localFolder != null && localFolder.getVersion() < folderVersion;
                    if (localFolder == null || isFresh) {
                        remoteFolder.setFresh(Config.STATE_FRESH);
                    } else {
                        remoteFolder.setFresh(localFolder.getFresh());
                    }

                    folders.add(remoteFolder);
                }

                mAdapter.refresh(folders);
                mFolderDao.insertOrReplaceInTx(folders);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof RequestException) {
                    Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (throwable instanceof IOException) {
                    Toast.makeText(MainActivity.this, R.string.error_io, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position) {
        final Folder folder = mAdapter.getItem(position);
        CategoryActivity.start(this, folder.getId(), folder.getName());

        folder.setFresh(Config.STATE_NORMAL);
        mFolderDao.updateInTx(folder);
        mAdapter.notifyItemChanged(position);
    }
}
