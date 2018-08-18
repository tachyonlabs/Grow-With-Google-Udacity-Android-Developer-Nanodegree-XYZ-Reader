package com.tachyonlabs.xyzreader.ui;

import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.adapters.MyPagerAdapter;
import com.tachyonlabs.xyzreader.data.ArticleLoader;
import com.tachyonlabs.xyzreader.data.ItemsContract;
import com.tachyonlabs.xyzreader.databinding.ActivityArticleDetailBinding;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private Cursor mCursor;
    private long mStartId;
    private int mCurrentPage;
    private ActivityArticleDetailBinding mBinding;

    private long mSelectedItemId;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail);
//        supportPostponeEnterTransition();

        // These are so the status bar stays transparent over the full-screen image with the tablet layout
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);
        if (mIsTablet) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                Log.d(TAG, "mStartId is " + mStartId);
                mSelectedItemId = mStartId;
                Bundle bundle = getIntent().getExtras();
                mCurrentPage = bundle.getInt(getString(R.string.current_page_key));
                Log.d(TAG,"Pos clicked " + mCurrentPage);
            }
        } else {
            mCurrentPage = savedInstanceState.getInt(getString(R.string.current_page_key));
        }

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = mBinding.pager;
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCurrentPage = position;
                    mCursor.moveToPosition(position);
                }
                mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
            }
        });

        mBinding.shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabShare();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.current_page_key), mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        mCursor = cursor;
        mPagerAdapter.swapCursor(mCursor);

//        // Select the start ID
//        if (mStartId > 0) {
//            mCursor.moveToFirst();
//            // TODO: optimize
////            Toast.makeText(this, "Start ID = " + mStartId, Toast.LENGTH_LONG).show();
//            while (!mCursor.isAfterLast()) {
//                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
//                    final int position = mCursor.getPosition();
//                    Log.d(TAG, "Setting " + position + " as current item in MyPagerAdapter");
//                    mPager.setCurrentItem(position, false);
//                    break;
//                }
//                mCursor.moveToNext();
//            }
//            mStartId = 0;
//        }
        mCursor.moveToPosition(mCurrentPage);
        mPager.setCurrentItem(mCurrentPage, false);
//        FloatingActionButton fab = findViewById(R.id.share_fab);
//        fab.set
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.swapCursor(mCursor);
    }

    private void fabShare() {
        mCursor.moveToPosition(mPager.getCurrentItem());
        // The body text of two of the articles was long enough to trigger android.os.TransactionTooLargeException
        // crashes when sharing, so I'm restricting it to 50,000 chars here.
        String bodyText = mCursor.getString(ArticleLoader.Query.BODY);
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setSubject(String.format("%s, by %s, %s",
                        mCursor.getString(ArticleLoader.Query.TITLE),
                        mCursor.getString(ArticleLoader.Query.AUTHOR),
                        mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE)))
                .setText(bodyText.substring(0, Math.min(50000, bodyText.length())))
                .getIntent(), getString(R.string.action_share)));
    }

}
