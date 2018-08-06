package com.tachyonlabs.xyzreader.ui;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;
import com.tachyonlabs.xyzreader.data.ItemsContract;
import com.tachyonlabs.xyzreader.databinding.ActivityArticleListBinding;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ArticleListActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private ActivityArticleListBinding mBinding;

    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();

    ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getContentResolver();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_list);
        mSwipeRefreshLayout = mBinding.swipeRefreshLayout;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = mBinding.recyclerView;
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mAdapter = new Adapter(null);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

//    private void refresh() {
//        startService(new Intent(this, UpdaterService.class));
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        registerReceiver(mRefreshingReceiver,
//                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unregisterReceiver(mRefreshingReceiver);
//    }
//
//    private boolean mIsRefreshing = false;
//
//    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
//                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                updateRefreshingUI();
//            }
//        }
//    };
//
//    private void updateRefreshingUI() {
//        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(0, null, this);
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
                    intent.putExtra("pos", vh.getAdapterPosition());
                    startActivity(intent);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            Log.d(TAG, "onBindViewHolder position is " + position + " " + mCursor.getString(ArticleLoader.Query.TITLE));
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = com.tachyonlabs.xyzreader.utils.DateUtils.parsePublishedDate(mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE));
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

            String photoUrlString = mCursor.getString(ArticleLoader.Query.THUMB_URL);
            Picasso.with(holder.thumbnailView.getContext())
                    .load(photoUrlString)
                    .placeholder(R.drawable.empty_detail)
                    .error(R.drawable.empty_detail)
                    .into(holder.thumbnailView,
                            PicassoPalette.with(photoUrlString, holder.thumbnailView)
                                    .use(PicassoPalette.Profile.MUTED_DARK)
                                    .intoBackground(holder.textBackground)

                                    .use(PicassoPalette.Profile.VIBRANT)
                                    .intoBackground(holder.textBackground, PicassoPalette.Swatch.RGB)
                    );
        }

        public void swapCursor(Cursor newCursor) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
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
