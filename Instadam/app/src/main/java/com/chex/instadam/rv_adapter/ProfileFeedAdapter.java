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

/**
 * Adaptador para el RecyclerView de las publicaciones de un usuario
 */
public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ViewHolder> {
    private List<Post> posts; //Lista de publicaciones del usuario

    public ProfileFeedAdapter(List<Post> posts){this.posts = posts;}

    /**
     * Aplica los datos de cada publicación en cada elemento del RecyclerView
     */

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView postImgV;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            postImgV = itemView.findViewById(R.id.profile_item_postImgV);
        }

        /**
         * Aplica las imagenes a cada ImageView
         * @param post El post que tiene almacenada la ruta de la imagen.
         */
        public void bind(Post post){
            ((MainActivity)itemView.getContext()).cargarImagenFireBase(post.getFbPostPath(), postImgV);
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
