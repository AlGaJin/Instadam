package com.chex.instadam.rv_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chex.instadam.java.Post;
import com.chex.instadam.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private List<Post> posts;

    public HomeFeedAdapter(List<Post> posts){
        this.posts = posts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImgV, postImgV;
        private TextView usernameTxtView, nCientificoTxtView, nComunTxtView;
        private List<Post> posts;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            userImgV = itemView.findViewById(R.id.chat_item_userImgView);
            postImgV = itemView.findViewById(R.id.postImgView);
            usernameTxtView = itemView.findViewById(R.id.usernameTxtView);
            nCientificoTxtView = itemView.findViewById(R.id.nombreCientificoTxtView);
            nComunTxtView = itemView.findViewById(R.id.nombreComunTxtView);
        }

        public void bind(Post post){
            userImgV.setImageDrawable(itemView.getResources().getDrawable(post.getProfilePic(), null));
            postImgV.setImageDrawable(itemView.getResources().getDrawable(post.getPostImg(), null));
            usernameTxtView.setText(post.getUsername());
            nCientificoTxtView.setText(post.getSciName());
            nComunTxtView.setText(post.getCommonName());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_feed, parent, false);
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
