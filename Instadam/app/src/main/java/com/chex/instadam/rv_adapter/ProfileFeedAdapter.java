package com.chex.instadam.rv_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chex.instadam.R;
import com.chex.instadam.java.Post;

import java.util.List;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {
    private List<Post> posts;
    public ProfileFeedAdapter(List<Post> posts){this.posts = posts;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView postImgV;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            postImgV = itemView.findViewById(R.id.profile_item_postImgV);
        }

        public void bind(Post post){
            postImgV.setImageDrawable(itemView.getResources().getDrawable(post.getPostImg(), null));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_feed, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
