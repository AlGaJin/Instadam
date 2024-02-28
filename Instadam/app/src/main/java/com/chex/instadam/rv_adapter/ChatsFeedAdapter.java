package com.chex.instadam.rv_adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Chat;
import com.chex.instadam.R;
import com.chex.instadam.java.Message;
import com.chex.instadam.java.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatsFeedAdapter extends RecyclerView.Adapter<ChatsFeedAdapter.ViewHolder> {
    private List<Chat> chats;

    public ChatsFeedAdapter(List<Chat> chats){
        this.chats = chats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linearLayout;
        private ImageView userImgV;
        private TextView usernameTxtView, msgTxtView, timeTxtView;
        private BBDDHelper bdHelper;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            linearLayout = itemView.findViewById(R.id.chat_item_linearLyt);
            userImgV = itemView.findViewById(R.id.chat_item_userImgView);
            usernameTxtView = itemView.findViewById(R.id.chat_item_usernameTxtView);
            msgTxtView = itemView.findViewById(R.id.chat_item_msgTxtView);
            timeTxtView = itemView.findViewById(R.id.chat_item_timeTxtView);
            bdHelper = new BBDDHelper(itemView.getContext());
        }

        public void bind(Chat chat){
            User otherUser = getOtherUser(chat);
            usernameTxtView.setText(otherUser.getUsername());
            ((MainActivity)itemView.getContext()).cargarProfilePic(otherUser.getProfilePic(),userImgV);
            Message msg = bdHelper.getLastMsg(chat.getId());
            if(msg != null){
                if(MainActivity.logedUser.getId() == msg.getUserId()){
                    String youSendIt = itemView.getResources().getString(R.string.you) + ": " + msg.getMsg();
                    msgTxtView.setText(youSendIt);
                }else{
                    msgTxtView.setText(msg.getMsg());
                }

                timeTxtView.setText(msg.getSendTime().toString().split(" ")[1].subSequence(0,5));
            }
            linearLayout.setOnClickListener(view -> ((MainActivity)itemView.getContext()).enviarMensaje(otherUser));
        }

        public User getOtherUser(Chat chat){
            if(chat.getOtherUserId() == MainActivity.logedUser.getId()){
                return bdHelper.getUserById(chat.getUserId()+"");
            }
            return bdHelper.getUserById(chat.getOtherUserId()+"");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_item_feed, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
