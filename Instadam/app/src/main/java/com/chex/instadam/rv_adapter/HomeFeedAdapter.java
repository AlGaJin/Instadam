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

/**
 * Adaptador para el RecyclerView de las publicaciones que se muestran en la página principal
 */
public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private List<Post> posts; //Lista de publicaciones a mostrar

    public HomeFeedAdapter(List<Post> posts){
        this.posts = posts;
    }

    /**
     * Aplica los datos de cada publicación en cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImgV, postImgV;
        private ImageButton likeBtn;
        private BBDDHelper bdHelper;
        private TextView usernameTxtView, sciNameTxtView, cmnNameTxtView, totalLikes, titleTxtV, dscTxtV;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            bdHelper = new BBDDHelper(itemView.getContext());

            //Recuperación de los elementos de la vista
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

        /**
         * Aplica los datos a cada publicación
         * @param post la publicación con los datos para aplicar en la vista
         */
        public void bind(Post post){
            User publisher = bdHelper.getUserById(post.getId_publisher()+""); //Se recupera al usuario que ha hecho la publicación

            //Recuperación de las imágenes de FireBase
            ((MainActivity)itemView.getContext()).cargarImagenFireBase(publisher.getProfilePic(), userImgV);
            ((MainActivity)itemView.getContext()).cargarImagenFireBase(post.getFbPostPath(), postImgV);

            //Texto de la publicación
            usernameTxtView.setText(publisher.getUsername());
            titleTxtV.setText(post.getTitle());
            dscTxtV.setText(post.getDsc());
            sciNameTxtView.setText(post.getSciName());
            cmnNameTxtView.setText(post.getCmnName());

            isLiked(post);

            //Acción para darle me gusta a una publicación
            likeBtn.setOnClickListener(view -> {
                bdHelper.changeLikedPost(MainActivity.logedUser, post);
                isLiked(post);
            });
        }

        /**
         * Comprueba si el usuario que ha iniciado sesión le ha dado me gusta
         * @param post publicación necesaria para obtener datos en la base de datos
         */
        public void isLiked(Post post){
            //Cambia el drawable del corazón según esté con me gusta o no
            if(bdHelper.isLiked(MainActivity.logedUser, post)){
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.liked, null));
            }else{
                likeBtn.setImageDrawable(itemView.getResources().getDrawable(R.drawable.unliked, null));
            }
            //Cambia el total de me gustas mostrados
            totalLikes.setText(String.valueOf(bdHelper.getTotalLikes(post)));
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
