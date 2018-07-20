package com.tachyonlabs.xyzreader.ui;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;
import com.tachyonlabs.xyzreader.data.ItemsContract;
import com.tachyonlabs.xyzreader.data.UpdaterService;
import com.tachyonlabs.xyzreader.databinding.ActivityArticleListBinding;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleListActivity.class.toString();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ActivityArticleListBinding mBinding;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getContentResolver();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_list);
        mSwipeRefreshLayout = mBinding.swipeRefreshLayout;
        mRecyclerView = mBinding.recyclerView;
        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                    String paletteTextBackgroundColor = vh.textBackground.getTag().toString();
                    intent.putExtra(getString(R.string.palette_color_key), paletteTextBackgroundColor);
                    startActivity(intent);
                }
            });
            return vh;
        }

        private Date parsePublishedDate() {
            try {
                String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
                return dateFormat.parse(date);
            } catch (ParseException ex) {
                Log.e(TAG, ex.getMessage());
                Log.i(TAG, "passing today's date");
                return new Date();
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.subtitleView.setText(Html.fromHtml(
                                mCursor.getString(ArticleLoader.Query.AUTHOR)
                                        + "<br/>" +
                                        DateUtils.getRelativeTimeSpanString(
                                                publishedDate.getTime(),
                                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                                DateUtils.FORMAT_ABBREV_ALL).toString()));
            } else {
                holder.subtitleView.setText(Html.fromHtml(
                        mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "<br/>" + outputFormat.format(publishedDate)));
            }

            // Many thanks to https://medium.com/david-developer/extracting-colors-from-images-integrating-picasso-and-palette-b9ba45c9c418
            // for this technique for getting a palette from within Picasso code rather than having to reload the image.
            Picasso.with(holder.thumbnailView.getContext())
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .resize(300, 300)
                    .centerCrop()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "loaded");
                            holder.thumbnailView.setImageBitmap(bitmap);
                            Palette.from(bitmap)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                            if (textSwatch == null) {
                                                Log.d(TAG, "getVibrantSwatch null");
                                                textSwatch = palette.getDarkVibrantSwatch();
                                            }
                                            if (textSwatch == null) {
                                                Log.d(TAG, "getDarkVibrantSwatch null");
                                                textSwatch = palette.getMutedSwatch();
                                            }
                                            if (textSwatch == null) {
                                                Log.d(TAG, "getMutedSwatch null");
                                                return;
                                            }
                                            int backgroundColor = textSwatch.getRgb();

                                            holder.textBackground.setBackgroundColor(backgroundColor);
                                            holder.textBackground.setTag(String.valueOf(backgroundColor));
//                                            holder.titleView.setTextColor(textSwatch.getBodyTextColor());
//                                            bodyColorText.setTextColor(textSwatch.getBodyTextColor());
                                        }
                                    });
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.d(TAG, "failed");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public LinearLayout textBackground;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            subtitleView = view.findViewById(R.id.article_subtitle);
            textBackground = view.findViewById(R.id.ll_list_item_text_background);
        }
    }
}
