package com.example.redditapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redditapp.R;
import com.example.redditapp.model.Post;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Post> mPosts;
    private final PostsAdapterOnClickHandler mClickHandler;


    public interface PostsAdapterOnClickHandler {
        void onClick(Post post);
    }

    public PostsAdapter(Context context, PostsAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutIdForListItem = R.layout.post_list_item;
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        holder.mTitleTextView.setText(post.getTitle());
        holder.mAuthorTextView.setText(post.getAuthor());
        holder.mSubredditTextView.setText(post.getSubreddit());

        if (post.getThumbnail() != null && !post.getThumbnail().equals("")) {
            Picasso.with(mContext).load(post.getThumbnail())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(holder.mPreviewImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mPreviewImageView.setVisibility(View.VISIBLE);
                            if (post.getUrl() != null && !post.getUrl().equals("") && !post.getUrl().equals("null")) {
                                final View.OnClickListener onClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(post.getUrl()));
                                        mContext.startActivity(intent);
                                    }
                                };
                                holder.mPreviewImageView.setOnClickListener(onClickListener);
                            }
                        }

                        @Override
                        public void onError() {
                            holder.mPreviewImageView.setVisibility(View.GONE);
                        }
                    });
        }
    }


    @Override
    public int getItemCount() {
        if (mPosts == null) return 0;
        return mPosts.size();
    }

    public void setPosts(ArrayList<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    public ArrayList<Post> getPosts() {
        return mPosts;
    }

    public void clear() {
        mPosts = null;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTitleTextView;
        final TextView mAuthorTextView;
        final TextView mSubredditTextView;
        final ImageView mPreviewImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title_tv);
            mAuthorTextView = itemView.findViewById(R.id.author_tv);
            mSubredditTextView = itemView.findViewById(R.id.subreddit_tv);
            mPreviewImageView = itemView.findViewById(R.id.preview_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mPosts.get(adapterPosition));
        }
    }
}
