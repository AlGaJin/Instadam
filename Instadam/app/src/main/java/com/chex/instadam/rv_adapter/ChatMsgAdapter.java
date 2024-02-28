package com.chex.instadam.rv_adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Message;

import java.util.List;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {
    private List<Message> msgList;
    public ChatMsgAdapter(List<Message> msgList){this.msgList = msgList;}
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView msgTxtV, dateTxtV;
        private LinearLayout msgLinearLyt, msgBg;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            msgTxtV = itemView.findViewById(R.id.msgTxtV);
            dateTxtV = itemView.findViewById(R.id.dateTxtV);
            msgLinearLyt = itemView.findViewById(R.id.msgLinearLyt);
            msgBg = itemView.findViewById(R.id.msgBg);
        }

        public void bind(Message msg){
            msgTxtV.setText(msg.getMsg());
            dateTxtV.setText(String.valueOf(msg.getSendTime()).split(" ")[1].subSequence(0,5));
            whoSends(msg.getUserId());
        }

        public void whoSends(int senderId){
            if(senderId == MainActivity.logedUser.getId()){
                msgLinearLyt.setGravity(Gravity.END);
                msgBg.setBackgroundColor(itemView.getResources().getColor(R.color.acentoOscuro, itemView.getContext().getTheme()));
            }else{
                msgLinearLyt.setGravity(Gravity.START);
                msgBg.setBackgroundColor(itemView.getResources().getColor(R.color.primarioClaro, itemView.getContext().getTheme()));
            }
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
        holder.bind(msgList.get(position));
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
