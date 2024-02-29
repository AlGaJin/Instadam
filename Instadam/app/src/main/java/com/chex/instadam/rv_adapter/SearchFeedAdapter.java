package com.chex.instadam.rv_adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Adaptador para el RecyclerView de los perfiles en el buscador
 */
public class SearchFeedAdapter extends RecyclerView.Adapter<SearchFeedAdapter.ViewHolder> {
    private List<User> users; //Lista de usuarios registrados en la aplicación
    private final User logedUser = MainActivity.logedUser; //Usuario que ha iniciado sesión

    public SearchFeedAdapter(List<User> users){
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout userLinearLyt;
        private ImageView userImV;
        private TextView usernameTxtV;
        private Button button;
        private BBDDHelper bdHelper;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            bdHelper = new BBDDHelper(itemView.getContext());
            //Recuperación de los elementos de la vista
            userLinearLyt = itemView.findViewById(R.id.userLinearLyt);
            userImV = itemView.findViewById(R.id.search_item_userImgV);
            usernameTxtV = itemView.findViewById(R.id.search_item_usernameTxtV);
            button = itemView.findViewById(R.id.search_item_btn);
        }

        /**
         * Aplica los datos a cada perfil
         * @param user usuario que conteine los datos a aplicar
         */
        public void bind(User user){
            //Acción para el layout para que cunado se clicle sobre él se muestre el perfil del usuario
            userLinearLyt.setOnClickListener(view -> {
                ((MainActivity)itemView.getContext()).verPerfil(user.getId());
            });

            usernameTxtV.setText(user.getUsername());
            ((MainActivity)itemView.getContext()).cargarImagenFireBase(user.getProfilePic(), userImV);

            button.setText(getButtonText(user));
            //Acción para el botón de seguir, permite seguir o dejar de seguir a un usuario
            button.setOnClickListener(view -> {
                bdHelper.changeFollow(logedUser, user);
                button.setText(getButtonText(user));
            });
        }

        /**
         * El setText necesita un String, no le agradan R.string
         * @param user usuario que tiene los datos para saber si le sigue o no
         * @return el String que se aplicará en el botón
         */
        public int getButtonText(User user){
            if(bdHelper.isFollowing(logedUser, user)){//Comprueba si le está siguiendo y devuelve un String u otro
                return R.string.siguiendo;
            }else{
                return R.string.seguir;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_feed, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
