package com.chex.instadam.rv_adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Post;
import com.chex.instadam.java.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {
    private List<Post> posts;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    public ProfileFeedAdapter(List<Post> posts){this.posts = posts;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView postImgV;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            postImgV = itemView.findViewById(R.id.profile_item_postImgV);
        }

        public void bind(Post post){
            cargarPostPic(post);
        }

        public void cargarPostPic(Post post) {
            String image = post.getFbPostPath();
            StorageReference imgRef = stRef.child(image);
            final long EIGHT_MEGABYTE = 1024*1024*8;
            imgRef.getBytes(EIGHT_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                postImgV.setImageBitmap(bitmap);
            });
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
