package com.chex.instadam.rv_adapter;

import android.content.res.ColorStateList;
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

/**
 * Adaptador para el RecyclerView de los mensajes de un chat
 */
public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {
    private List<Message> msgList; //Lista de mensajes que hay en ese chat
    public ChatMsgAdapter(List<Message> msgList){this.msgList = msgList;}

    /**
     * Aplica los datos de cada mensaje en cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView msgTxtV, dateTxtV;
        private LinearLayout msgLinearLyt, msgBg;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            //Recuperación de los elementos de la vista
            msgTxtV = itemView.findViewById(R.id.msgTxtV);
            dateTxtV = itemView.findViewById(R.id.dateTxtV);
            msgLinearLyt = itemView.findViewById(R.id.msgLinearLyt);
            msgBg = itemView.findViewById(R.id.msgBg);
        }

        /**
         * Aplica los datos a cada mensaje
         * @param msg el mensaje con los datos para aplicar en la vista
         */
        public void bind(Message msg){
            msgTxtV.setText(msg.getMsg());
            dateTxtV.setText(String.valueOf(msg.getSendTime()).split(" ")[1].subSequence(0,5));
            whoSends(msg.getUserId());
        }

        /**
         * Comprueba quién envía el mensaje y aplica un estilo u otro
         * @param senderId id del usuario que ha enviado el mensaje
         */
        public void whoSends(int senderId){
            if(senderId == MainActivity.logedUser.getId()){ //Si es el usuario que ha iniciado sesión
                msgLinearLyt.setGravity(Gravity.END); //Los mensajes se posicionan a la derecha de la pantalla
                //Se le aplica un color más claro al fondo
                msgBg.setBackgroundTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.acentoClaro, itemView.getContext().getTheme())));
            }else{//Si es otro usuario registrado
                msgLinearLyt.setGravity(Gravity.START);//Los mensajes se posicionan a la izquierda de la pantalla
                //Se le aplica un color más oscuro al fondo
                msgBg.setBackgroundTintList(ColorStateList.valueOf(itemView.getResources().getColor(R.color.primarioClaro, itemView.getContext().getTheme())));
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
