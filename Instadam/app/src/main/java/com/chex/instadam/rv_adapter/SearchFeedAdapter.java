package com.chex.instadam.rv_adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class SearchFeedAdapter extends RecyclerView.Adapter<SearchFeedAdapter.ViewHolder> {
    private List<User> users;
    private final User logedUser = MainActivity.logedUser;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    public SearchFeedAdapter(List<User> users){
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImV;
        private TextView usernameTxtV;
        private Button button;
        private BBDDHelper bdHelper;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            userImV = itemView.findViewById(R.id.search_item_userImgV);
            usernameTxtV = itemView.findViewById(R.id.search_item_usernameTxtV);
            button = itemView.findViewById(R.id.search_item_btn);
            bdHelper = new BBDDHelper(itemView.getContext());
        }

        public void bind(User user){
            usernameTxtV.setText(user.getUsername());
            cargarProfilePic(user, userImV);
            button.setText(getButtonText(user));
            button.setOnClickListener(view -> {
                bdHelper.changeFollow(logedUser, user);
                button.setText(getButtonText(user));
            });
        }

        public int getButtonText(User user){
            if(bdHelper.isFollowing(logedUser, user)){
                return R.string.siguiendo;
            }else{
                return R.string.seguir;
            }
        }

        public void cargarProfilePic(User user, ImageView imgV) {
            String image = "profilePics/DEFAULT.png";
            if(user.getProfilePic() != null){
                image = user.getProfilePic();
            }
            StorageReference imgRef = stRef.child(image);
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
