package com.flipboard.bottomsheet.commons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.weibuddy.R;
import com.weibuddy.util.ViewUtils;
import com.weibuddy.widget.OverlayImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ImagePickerSheetViewCompat extends FrameLayout {

    public interface OnTileSelectedListener {
        void onTileSelected(ImagePickerTile... selectedTile);
    }

    public interface ImageProvider {
        void onProvideImage(ImageView imageView, Uri imageUri, int size);
    }

    public static class ImagePickerTile {

        public static final int IMAGE = 1;
        public static final int CAMERA = 2;
        public static final int PICKER = 3;

        public static final ImagePickerTile _CAMERA = new ImagePickerTile(CAMERA);
        public static final ImagePickerTile _PICKER = new ImagePickerTile(PICKER);

        @IntDef({IMAGE, CAMERA, PICKER})
        public @interface TileType {
        }

        @IntDef({CAMERA, PICKER})
        public @interface SpecialTileType {
        }

        protected final Uri imageUri;
        @TileType
        protected final int tileType;

        protected boolean isSelected = false;

        ImagePickerTile(@SpecialTileType int tileType) {
            this(null, tileType);
        }

        ImagePickerTile(@NonNull Uri imageUri) {
            this(imageUri, IMAGE);
        }

        protected ImagePickerTile(@Nullable Uri imageUri, @TileType int tileType) {
            this.imageUri = imageUri;
            this.tileType = tileType;
        }

        @Nullable
        public Uri getImageUri() {
            return imageUri;
        }

        @TileType
        public int getTileType() {
            return tileType;
        }

        public boolean isImageTile() {
            return tileType == IMAGE;
        }

        public boolean isCameraTile() {
            return tileType == CAMERA;
        }

        public boolean isPickerTile() {
            return tileType == PICKER;
        }

        @Override
        public String toString() {
            if (isImageTile()) {
                return "ImageTile: " + imageUri;
            } else if (isCameraTile()) {
                return "CameraTile";
            } else if (isPickerTile()) {
                return "PickerTile";
            } else {
                return "Invalid item";
            }
        }
    }

    protected final Button buttonView;
    protected final TextView titleView;
    protected final GridView tileGrid;
    protected Adapter adapter;
    protected int thumbnailSize;
    protected final int spacing;
    protected final int originalGridPaddingTop;

    // Values provided by the builder
    protected ImageProvider imageProvider;
    protected boolean showCameraOption = true;
    protected boolean showPickerOption = true;
    protected Drawable cameraDrawable = null;
    protected Drawable pickerDrawable = null;
    protected MimeType[] mimeTypes = new MimeType[0];
    protected boolean isMultipleChoice = false;
    private int columnWidthDp = 100;

    protected ImagePickerSheetViewCompat(final Builder builder) {
        super(builder.context);

        inflate(getContext(), R.layout.grid_sheet_view, this);

        // Set up the grid
        tileGrid = ViewUtils.findViewById(this, R.id.grid);
        spacing = getResources().getDimensionPixelSize(R.dimen.bottomsheet_image_tile_spacing);
        tileGrid.setDrawSelectorOnTop(true);
        tileGrid.setVerticalSpacing(spacing);
        tileGrid.setHorizontalSpacing(spacing);
        tileGrid.setPadding(spacing, 0, spacing, 0);

        // Set up the title
        titleView = ViewUtils.findViewById(this, R.id.title);
        originalGridPaddingTop = tileGrid.getPaddingTop();
        setTitle(builder.title);

        buttonView = ViewUtils.findViewById(this, R.id.ok);
        setButtonTitle(builder.buttonTitle);
        buttonView.setVisibility(builder.isMultipleChoice ? VISIBLE : GONE);

        final OnTileSelectedListener listener = builder.onTileSelectedListener;

        buttonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter == null) {
                    return;
                }
                List<ImagePickerTile> selectedItems = adapter.getSelectedItem();
                if (listener != null) {
                    listener.onTileSelected(selectedItems.toArray(new ImagePickerTile[0]));
                }
            }
        });

        // Hook up the remaining builder fields
        if (listener != null) {
            tileGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
                    if (isMultipleChoice) {
                        ImagePickerTile tile = adapter.getItem(position);
                        tile.isSelected = !tile.isSelected;
                        adapter.notifyDataSetChanged();
                    } else {
                        listener.onTileSelected(adapter.getItem(position));
                    }
                }
            });
        }
        imageProvider = builder.imageProvider;
        showCameraOption = builder.showCameraOption;
        showPickerOption = builder.showPickerOption;
        cameraDrawable = builder.cameraDrawable;
        pickerDrawable = builder.pickerDrawable;
        mimeTypes = builder.mimeTypes;
        isMultipleChoice = builder.isMultipleChoice;

        ViewCompat.setElevation(this, Util.dp2px(getContext(), 16f));
    }

    private void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        } else {
            titleView.setVisibility(GONE);
            // Add some padding to the top to account for the missing title
            tileGrid.setPadding(tileGrid.getPaddingLeft(), originalGridPaddingTop + spacing, tileGrid.getPaddingRight(), tileGrid.getPaddingBottom());
        }
    }

    private void setButtonTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            buttonView.setText(title);
        } else {
            buttonView.setVisibility(GONE);
        }
    }

    public void setColumnWidthDp(int columnWidthDp) {
        this.columnWidthDp = columnWidthDp;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.adapter = new Adapter(getContext());
        tileGrid.setAdapter(this.adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Necessary for showing elevation on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new Util.ShadowOutline(w, h));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float density = getResources().getDisplayMetrics().density;
        final int numColumns = (int) (width / (columnWidthDp * density));
        thumbnailSize = Math.round((width - ((numColumns - 1) * spacing)) / 3.0f);
        tileGrid.setNumColumns(numColumns);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class Adapter extends BaseAdapter {

        private List<ImagePickerTile> tiles = new ArrayList<>();
        final LayoutInflater inflater;
        private final ContentResolver resolver;

        public Adapter(Context context) {
            inflater = LayoutInflater.from(context);

            if (!isMultipleChoice && showCameraOption) {
                tiles.add(ImagePickerTile._CAMERA);
            }
            if (!isMultipleChoice && showPickerOption) {
                tiles.add(ImagePickerTile._PICKER);
            }

            // Add local images, in descending order of date taken
            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
            resolver = context.getContentResolver();

            final StringBuilder selection = new StringBuilder();
            final String[] selectionArgs = new String[mimeTypes.length];
            for (int i = 0; i < mimeTypes.length; i++) {
                MimeType mimeType = mimeTypes[i];
                if (i == 0) {
                    selection.append(MediaStore.Images.ImageColumns.MIME_TYPE)
                            .append(" = ?");
                } else {
                    selection.append(" OR ")
                            .append(MediaStore.Images.ImageColumns.MIME_TYPE)
                            .append(" = ?");
                }
                selectionArgs[i] = mimeType.value;
            }

            final Cursor cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection.toString(),
                    selectionArgs,
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String imageLocation = cursor.getString(1);
                    File imageFile = new File(imageLocation);
                    if (imageFile.exists()) {
                        tiles.add(new ImagePickerTile(Uri.fromFile(imageFile)));
                    }
                }
                cursor.close();
            }
        }

        public List<ImagePickerTile> getSelectedItem() {
            ArrayList<ImagePickerTile> selectedTiles = new ArrayList<>();
            for (ImagePickerTile tile : tiles) {
                if (tile.isSelected) {
                    selectedTiles.add(tile);
                }
            }
            return selectedTiles;
        }

        @Override
        public int getCount() {
            return tiles.size();
        }

        @Override
        public ImagePickerTile getItem(int position) {
            return tiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View recycled, @NonNull ViewGroup parent) {
            FrameLayout layout;
            ViewHolder holder;

            if (recycled == null) {
                layout = (FrameLayout) inflater.inflate(R.layout.sheet_image_grid_item, parent, false);

                holder = new ViewHolder();
                holder.thumb = ViewUtils.findViewById(layout, R.id.thumb);
                holder.checkBox = ViewUtils.findViewById(layout, R.id.checkbox);

                layout.setTag(holder);
            } else {
                layout = (FrameLayout) recycled;
                holder = (ViewHolder) layout.getTag();
            }

            ImagePickerTile tile = tiles.get(position);
            if (isMultipleChoice) {
                holder.checkBox.setVisibility(VISIBLE);
                holder.checkBox.setChecked(tile.isSelected);
            } else {
                holder.checkBox.setVisibility(GONE);
            }

            holder.thumb.setMinimumWidth(thumbnailSize);
            holder.thumb.setMinimumHeight(thumbnailSize);
            holder.thumb.setMaxHeight(thumbnailSize);
            holder.thumb.setMaxWidth(thumbnailSize);
            if (tile.imageUri != null) {
                imageProvider.onProvideImage(holder.thumb, tile.imageUri, thumbnailSize);
            } else {
                holder.thumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if (tile.isCameraTile()) {
                    holder.thumb.setBackgroundResource(android.R.color.black);
                    if (cameraDrawable == null) {
                        holder.thumb.setImageResource(R.drawable.bottomsheet_camera);
                    } else {
                        holder.thumb.setImageDrawable(cameraDrawable);
                    }
                } else if (tile.isPickerTile()) {
                    holder.thumb.setBackgroundResource(android.R.color.darker_gray);
                    if (pickerDrawable == null) {
                        holder.thumb.setImageResource(R.drawable.bottomsheet_collections);
                    } else {
                        holder.thumb.setImageDrawable(pickerDrawable);
                    }
                }
            }

            if (tile.isSelected) {
                holder.thumb.setColorForeground(0x80000000);
            } else {
                holder.thumb.setColorForeground(Color.TRANSPARENT);
            }

            return layout;
        }
    }

    private static class ViewHolder {
        OverlayImageView thumb;
        CheckBox checkBox;
    }

    public enum MimeType {
        JPEG("image/jpeg"),
        GIF("image/gif"),
        PNG("image/png"),
        BMP("image/bmp");
        final String value;

        MimeType(String value) {
            this.value = value;
        }
    }

    public static class Builder {
        Context context;
        String title;
        String buttonTitle;
        OnTileSelectedListener onTileSelectedListener;
        ImageProvider imageProvider;
        boolean showCameraOption = false;
        boolean showPickerOption = false;
        Drawable cameraDrawable = null;
        Drawable pickerDrawable = null;
        MimeType[] mimeTypes = new MimeType[0];
        boolean isMultipleChoice = false;

        public Builder(@NonNull Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new RuntimeException("Missing required READ_EXTERNAL_STORAGE permission. Did you remember to request it first?");
            }
            this.context = context;
        }

        public Builder setTitle(@StringRes int id) {
            return setTitle(context.getString(id));
        }

        public Builder setTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder setButtonTitle(@Nullable int id) {
            return setButtonTitle(context.getString(id));
        }

        public Builder setButtonTitle(@Nullable String title) {
            this.buttonTitle = title;
            return this;
        }

        public Builder setOnTileSelectedListener(OnTileSelectedListener onTileSelectedListener) {
            this.onTileSelectedListener = onTileSelectedListener;
            return this;
        }

        public Builder setIsMultipleChoice(boolean isMultipleChoice) {
            this.isMultipleChoice = isMultipleChoice;
            return this;
        }

        public Builder setImageMimeTypes(@NonNull MimeType... mimeTypes) {
            this.mimeTypes = mimeTypes;
            return this;
        }

        public Builder setImageProvider(ImageProvider imageProvider) {
            this.imageProvider = imageProvider;
            return this;
        }

        public Builder setShowCameraOption(boolean showCameraOption) {
            this.showCameraOption = showCameraOption;
            return this;
        }

        public Builder setShowPickerOption(boolean showPickerOption) {
            this.showPickerOption = showPickerOption;
            return this;
        }

        public Builder setCameraDrawable(@DrawableRes int resId) {
            return setCameraDrawable(ResourcesCompat.getDrawable(context.getResources(), resId, null));
        }

        public Builder setCameraDrawable(@Nullable Drawable cameraDrawable) {
            this.cameraDrawable = cameraDrawable;
            return this;
        }

        public Builder setPickerDrawable(@DrawableRes int resId) {
            return setPickerDrawable(ResourcesCompat.getDrawable(context.getResources(), resId, null));
        }

        public Builder setPickerDrawable(Drawable pickerDrawable) {
            this.pickerDrawable = pickerDrawable;
            return this;
        }

        @CheckResult
        public ImagePickerSheetViewCompat create() {
            if (imageProvider == null) {
                throw new IllegalStateException("Must provide an ImageProvider!");
            }
            return new ImagePickerSheetViewCompat(this);
        }
    }

}

