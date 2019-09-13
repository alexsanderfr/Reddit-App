package com.example.redditapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redditapp.R;
import com.example.redditapp.model.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Comment> mComments;

    public CommentsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutIdForListItem = R.layout.comment_list_item;
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder holder, int position) {
        holder.mAuthorTextView.setText(mComments.get(position).getAuthor());
        holder.mBodyTextView.setText(mComments.get(position).getBody());
        getRepliesRecursively(holder.mCommentViewGroup, mComments.get(position), 1);
    }

    private void getRepliesRecursively(ViewGroup viewGroup, Comment comment, int depth) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (comment.getReplies() != null && inflater != null) {
            for (Comment item : comment.getReplies()) {
                View view = inflater.inflate(R.layout.comment_list_item, viewGroup, false);
                TextView authorTextView = view.findViewById(R.id.comment_author_tv);
                TextView bodyTextView = view.findViewById(R.id.comment_body_tv);
                authorTextView.setText(item.getAuthor());
                bodyTextView.setText(item.getBody());
                ImageView marginView = view.findViewById(R.id.margin_view);
                marginView.getLayoutParams().width += depth * 8;
                int[] colors = mContext.getResources().getIntArray(R.array.randomColors);
                marginView.setBackgroundColor(colors[depth % colors.length]);
                viewGroup.addView(view);
                getRepliesRecursively(viewGroup, item, depth + 1);
            }
        }
    }

    public void setComments(ArrayList<Comment> comments) {
        mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mComments == null) return 0;
        return mComments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mAuthorTextView;
        final TextView mBodyTextView;
        final ViewGroup mCommentViewGroup;

        ViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = itemView.findViewById(R.id.comment_author_tv);
            mBodyTextView = itemView.findViewById(R.id.comment_body_tv);
            mCommentViewGroup = itemView.findViewById(R.id.comment_ll);
        }
    }
}
