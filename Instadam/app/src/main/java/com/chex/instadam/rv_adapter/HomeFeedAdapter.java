package com.chex.instadam.rv_adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Post;
import com.chex.instadam.R;
import com.chex.instadam.java.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private List<Post> posts;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    public HomeFeedAdapter(List<Post> posts){
        this.posts = posts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImgV, postImgV;
        private ImageButton likeBtn;
        private BBDDHelper bdHelper;
        private TextView usernameTxtView, sciNameTxtView, cmnNameTxtView, totalLikes, titleTxtV, dscTxtV;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            bdHelper = new BBDDHelper(itemView.getContext());
            userImgV = itemView.findViewById(R.id.chat_item_userImgView);
            postImgV = itemView.findViewById(R.id.postImgView);
            usernameTxtView = itemView.findViewById(R.id.usernameTxtView);
            titleTxtV = itemView.findViewById(R.id.titleTxtV);
            dscTxtV = itemView.findViewById(R.id.postDscTxtV);
            sciNameTxtView = itemView.findViewById(R.id.nombreCientificoTxtView);
            cmnNameTxtView = itemView.findViewById(R.id.nombreComunTxtView);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            totalLikes = itemView.findViewById(R.id.totalLikes);
        }

        public void bind(Post post){
            User publisher = bdHelper.getUserById(post.getId_publisher()+"");

            cargarImagenFB(publisher.getProfilePic(), userImgV);
            cargarImagenFB(post.getFbPostPath(), postImgV);

            usernameTxtView.setText(publisher.getUsername());
            titleTxtV.setText(post.getTitle());
            dscTxtV.setText(post.getDsc());
            sciNameTxtView.setText(post.getSciName());
            cmnNameTxtView.setText(post.getCmnName());

            isLiked(post);

            likeBtn.setOnClickListener(view -> {
                bdHelper.changeLikedPost(MainActivity.logedUser, post);
                isLiked(post);
            });
        }

        public void isLiked(Post post){
            if(bdHelper.isLiked(MainActivity.logedUser, post)){
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.liked, null));
            }else{
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.unliked, null));
            }
            totalLikes.setText(String.valueOf(bdHelper.getTotalLikes(post)));
        }
        public void cargarImagenFB(String path, ImageView imgV) {
            StorageReference imgRef = stRef.child(path);
            final long EIGHT_MEGABYTE = 1024*1024*8;
            imgRef.getBytes(EIGHT_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imgV.setImageBitmap(bitmap);
            });
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
