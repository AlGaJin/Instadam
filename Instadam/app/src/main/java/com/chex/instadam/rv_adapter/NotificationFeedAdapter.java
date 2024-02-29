package com.chex.instadam.rv_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chex.instadam.R;
import com.chex.instadam.java.Notification;

import java.util.List;

/**
 * Adapador para el RecyclerView de las notificaciones ((W.I.P))
 */
public class NotificationFeedAdapter extends RecyclerView.Adapter<NotificationFeedAdapter.ViewHolder>{
    private List<Notification> notifications;

    public NotificationFeedAdapter(List<Notification> notifications){this.notifications = notifications;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userImgV;
        private TextView notiTxtV;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            userImgV = itemView.findViewById(R.id.noti_item_userImgV);
            notiTxtV = itemView.findViewById(R.id.noti_item_txtV);
        }

        public void bind(Notification noti){
            userImgV.setImageDrawable(itemView.getResources().getDrawable(noti.getOtherUserProfilePic(), null));
            notiTxtV.setText(noti.getNotiMsg());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_feed, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
