package com.tachyonlabs.xyzreader.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class ArticleLoader extends CursorLoader {
    public static ArticleLoader newAllArticlesInstance(Context context) {
        return new ArticleLoader(context, com.tachyonlabs.xyzreader.data.ItemsContract.Items.buildDirUri());
    }

    public static ArticleLoader newInstanceForItemId(Context context, long itemId) {
        return new ArticleLoader(context, com.tachyonlabs.xyzreader.data.ItemsContract.Items.buildItemUri(itemId));
    }

    private ArticleLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, com.tachyonlabs.xyzreader.data.ItemsContract.Items.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                com.tachyonlabs.xyzreader.data.ItemsContract.Items._ID,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.TITLE,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.PUBLISHED_DATE,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.AUTHOR,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.THUMB_URL,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.PHOTO_URL,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.ASPECT_RATIO,
                com.tachyonlabs.xyzreader.data.ItemsContract.Items.BODY,
        };

        int _ID = 0;
        int TITLE = 1;
        int PUBLISHED_DATE = 2;
        int AUTHOR = 3;
        int THUMB_URL = 4;
        int PHOTO_URL = 5;
        int ASPECT_RATIO = 6;
        int BODY = 7;
    }
}
