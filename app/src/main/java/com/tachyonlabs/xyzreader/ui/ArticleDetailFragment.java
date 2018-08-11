package com.tachyonlabs.xyzreader.ui;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private static final String TAG = ArticleDetailFragment.class.getSimpleName();

    public static final String ARG_ITEM_ID = "item_id";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;

    private int mTopInset;
    private View mPhotoContainerView;
    private ImageView mPhotoView;
    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

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

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
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
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        Toolbar toolbar = mRootView.findViewById(R.id.tb_detail_fragment);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mPhotoView = mRootView.findViewById(R.id.iv_article_photo);

        bindViews();
//        final View sharedView = mRootView.findViewById(R.id.iv_article_photo);
//        sharedView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
//                getActivity().startPostponedEnterTransition();
//                return true;
//            }
//        });
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
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            bylineView.setText(String.format("%s, %s", mCursor.getString(ArticleLoader.Query.AUTHOR), com.tachyonlabs.xyzreader.utils.DateUtils.parsePublishedDate(mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE))));
            String bodyString = mCursor.getString(ArticleLoader.Query.BODY).substring(0, 2000).replaceAll("\r", "").replaceAll("\n\n", "<br/><br/>").replaceAll("\n", " ");
            bodyView.setText(Html.fromHtml(bodyString));
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

//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        if (!isAdded()) {
//            if (cursor != null) {
//                cursor.close();
//            }
//            return;
//        }
//
//        mCursor = cursor;
//        if (mCursor != null && !mCursor.moveToFirst()) {
//            Log.d(TAG, "Error reading item detail cursor");
//            mCursor.close();
//            mCursor = null;
//        }
//
//        bindViews();
//    }

//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        mCursor = null;
//        bindViews();
//    }

}
