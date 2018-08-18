package com.tachyonlabs.xyzreader.ui;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = ArticleDetailFragment.class.getSimpleName();
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private Toolbar mToolbar;
    private boolean mIsTablet;
    private String mTitle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mToolbar = mRootView.findViewById(R.id.tb_detail_fragment);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // These are so the status bar stays transparent over the full-screen image with the tablet layout
        // Many people have posted about this on StackOverflow
        if (mIsTablet) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            final CollapsingToolbarLayout collapsingToolbarLayout = mRootView.findViewById(R.id.collapsing_toolbar_layout_detail);
            AppBarLayout appBarLayout = mRootView.findViewById(R.id.app_bar_layout_detail);
            // Many thanks to https://stackoverflow.com/a/32724422/1212526 for this technique
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = true;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        mToolbar.setTitle(mTitle);
                        isShow = true;
                    } else if (isShow) {
                        mToolbar.setTitle(" ");
                        isShow = false;
                    }
                }
            });
        }

        bindViews();

        return mRootView;
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        ImageView photoView = mRootView.findViewById(R.id.iv_article_photo);
        LinearLayout titleLayout = mRootView.findViewById(R.id.meta_bar);
        TextView titleView = mRootView.findViewById(R.id.article_title);
        TextView bylineView = mRootView.findViewById(R.id.article_byline);
        TextView bodyView = mRootView.findViewById(R.id.article_body);

        if (mCursor != null) {
            String photoUrlString = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
            Picasso.with(getActivity())
                    .load(photoUrlString)
                    .placeholder(R.drawable.empty_detail)
                    .error(R.drawable.empty_detail)
                    .into(photoView,
                            PicassoPalette.with(photoUrlString, photoView)
                                    .use(PicassoPalette.Profile.MUTED_DARK)
                                    .intoBackground(titleLayout)

                                    .use(PicassoPalette.Profile.VIBRANT)
                                    .intoBackground(titleLayout, PicassoPalette.Swatch.RGB)
                    );
            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            titleView.setText(title);
            bylineView.setText(String.format("%s, %s", mCursor.getString(ArticleLoader.Query.AUTHOR), com.tachyonlabs.xyzreader.utils.DateUtils.parsePublishedDate(mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE))));
            String bodyString = mCursor.getString(ArticleLoader.Query.BODY).substring(0, 2000).replaceAll("\r", "").replaceAll("\n{2,}", "<br/><br/>").replaceAll("\n", " ");
            bodyView.setText(Html.fromHtml(bodyString));
            mTitle = title;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.d(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }
}
