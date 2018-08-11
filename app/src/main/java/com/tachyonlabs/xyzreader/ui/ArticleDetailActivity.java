package com.tachyonlabs.xyzreader.ui;

import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;
import com.tachyonlabs.xyzreader.data.ItemsContract;
import com.tachyonlabs.xyzreader.databinding.ActivityArticleDetailBinding;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private Cursor mCursor;
    private long mStartId;
    private int mWhichWasClicked;
    private ActivityArticleDetailBinding mBinding;

    private long mSelectedItemId;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mWhichWasClicked = bundle.getInt("pos");
        Log.d(TAG,"Pos clicked " + mWhichWasClicked);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail);
        supportPostponeEnterTransition();

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = mBinding.pager;
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
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

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                Log.d(TAG, "mStartId is " + mStartId);
                mSelectedItemId = mStartId;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished");
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

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
        mCursor.moveToPosition(mWhichWasClicked);
        mPager.setCurrentItem(mWhichWasClicked, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

//        @Override
//        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            Log.d(TAG, "Setting " + position + " as primary item in MyPagerAdapter");
//            super.setPrimaryItem(container, position, object);
//            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
//            if (fragment != null) {
////                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
////                updateUpButtonPosition();
//            }
//        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        // for FragmentStatePagerAdapter misbehavior of blanking the current fragment after notifyDataSetChanged
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }

    private void fabShare() {
        mCursor.moveToPosition(mPager.getCurrentItem());
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setSubject(String.format("%s, by %s, %s",
                        mCursor.getString(ArticleLoader.Query.TITLE),
                        mCursor.getString(ArticleLoader.Query.AUTHOR),
                        mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE)))
                .setText(mCursor.getString(ArticleLoader.Query.BODY))
                .getIntent(), getString(R.string.action_share)));
    }

}
