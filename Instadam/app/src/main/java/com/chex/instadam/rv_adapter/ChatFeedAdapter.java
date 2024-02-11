package com.chex.instadam.rv_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chex.instadam.java.Chat;
import com.chex.instadam.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatFeedAdapter extends RecyclerView.Adapter<ChatFeedAdapter.ViewHolder> {
    private List<Chat> chats;

    public ChatFeedAdapter(List<Chat> chats){
        this.chats = chats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImgV;
        private TextView usernameTxtView, msgTxtView, timeTxtView;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            userImgV = itemView.findViewById(R.id.chat_item_userImgView);
            usernameTxtView = itemView.findViewById(R.id.chat_item_usernameTxtView);
            msgTxtView = itemView.findViewById(R.id.chat_item_msgTxtView);
            timeTxtView = itemView.findViewById(R.id.chat_item_timeTxtView);
        }

        public void bind(Chat chat){
            userImgV.setImageDrawable(itemView.getResources().getDrawable(chat.getUserImg(), null));
            usernameTxtView.setText(chat.getUsername());
            msgTxtView.setText(chat.getTmpMsg());
            timeTxtView.setText(chat.getTime());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_feed, parent, false);
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
