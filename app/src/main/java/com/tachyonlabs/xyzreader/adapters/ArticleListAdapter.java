package com.tachyonlabs.xyzreader.adapters;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;
import com.tachyonlabs.xyzreader.R;
import com.tachyonlabs.xyzreader.data.ArticleLoader;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleListAdapterViewHolder> {
    private final static String TAG = ArticleListAdapter.class.getSimpleName();
    private final ArticleListAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();

    public ArticleListAdapter(Context context, ArticleListAdapterOnClickHandler articleListAdapterOnClickHandler) {
        mClickHandler = articleListAdapterOnClickHandler;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ArticleListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_article;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ArticleListAdapterViewHolder viewHolder = new ArticleListAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListAdapter.ArticleListAdapterViewHolder holder, int position) {
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

    public interface ArticleListAdapterOnClickHandler {
        void onClick(int position, View view);
    }

    public class ArticleListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public LinearLayout textBackground;

        public ArticleListAdapterViewHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.thumbnail);
            titleView = itemView.findViewById(R.id.article_title);
            subtitleView = itemView.findViewById(R.id.article_subtitle);
            textBackground = itemView.findViewById(R.id.ll_list_item_text_background);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(getAdapterPosition(), view);
        }
    }
}
