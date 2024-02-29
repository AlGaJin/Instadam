package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Message;
import com.chex.instadam.java.User;
import com.chex.instadam.rv_adapter.ChatMsgAdapter;

import java.util.List;

/**
 * Da funcionalidad a la vista que muestra los mensajes del chat
 */
public class ChatFragment extends Fragment {
    private int chatId;
    private Toolbar toolbar;
    private ImageView imgV;
    private TextView usernameTxtV;
    private RecyclerView rv;
    private EditText msgEditTxt;
    private ImageButton sendBtn;
    private BBDDHelper bdHelper;
    private User otherUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().hide();//Se oculta el toolbar del MainActivity
        ((MainActivity)getActivity()).desactivarBtnNav(); //Se desactiva el BottomNavView

        bdHelper = new BBDDHelper(getContext());

        chatId = getArguments().getInt("chatId");//Se recoge el id del chat que se pasa por argumentos siempre que se va a iniciar el fragmento
        otherUser = bdHelper.getUserById(String.valueOf(getArguments().getInt("userId")));//Se recoge el usuario con ayuda de la base de datos
        //Recuperación de los elementos de la vista
        toolbar = v.findViewById(R.id.chatToolBar);
        imgV = v.findViewById(R.id.chat_toolbarImgV);
        usernameTxtV = v.findViewById(R.id.chat_toolbarTxtV);
        rv = v.findViewById(R.id.chat_rv);
        msgEditTxt = v.findViewById(R.id.msgEditTxt);
        sendBtn = v.findViewById(R.id.sendBtn);

        //Se le añade un icono al toolbar y se le da funcionalidad
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(view -> {
            salir();
        });

        //Se cargan los datos informativos que hay en el toolbar
        ((MainActivity)getActivity()).cargarImagenFireBase(otherUser.getProfilePic(), imgV);
        usernameTxtV.setText(otherUser.getUsername());

        //Se recupera la lista de mensajes asociadas al chat con la ayuda de la base de datos
        List<Message> msgList = bdHelper.getChatMsg(chatId);

        //Configuración del RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ChatMsgAdapter(msgList));
        rv.getLayoutManager().scrollToPosition(msgList.size()-1);//Hace que se muestre el último mensaje enviado

        //Acción al botón para enviar un mensaje
        sendBtn.setOnClickListener(view -> {
            String msg = msgEditTxt.getText().toString().trim();
            if(!msg.isEmpty()){ //Si hay algo escrito
                msgEditTxt.setText("");//Se limpia el campo para escribir un nuevo mensaje
                bdHelper.insertMsg(msg, MainActivity.logedUser.getId(), chatId);//Se almacena el mensaje en la base de datos local
                //Se actualiza el RecyclerView
                List<Message> msgL = bdHelper.getChatMsg(chatId);
                rv.setAdapter(new ChatMsgAdapter(msgL));
                rv.getLayoutManager().scrollToPosition(msgL.size()-1);
            }
        });

        //Modifico la función del botón de Back del móvil
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                salir();
            }
        });
        return v;
    }

    /**
     * Sale del fragmento y recompone los elementos ocultados previamente
     */
    private void salir() {
        ((MainActivity)getActivity()).accionBack();
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).activarBtnNav();
    }
}