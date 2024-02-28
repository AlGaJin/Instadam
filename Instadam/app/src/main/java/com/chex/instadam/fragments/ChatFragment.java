package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        ((MainActivity)getActivity()).desactivarBtnNav();

        bdHelper = new BBDDHelper(getContext());

        chatId = getArguments().getInt("chatId");
        otherUser = bdHelper.getUserById(String.valueOf(getArguments().getInt("userId")));
        toolbar = v.findViewById(R.id.chatToolBar);
        imgV = v.findViewById(R.id.chat_toolbarImgV);
        usernameTxtV = v.findViewById(R.id.chat_toolbarTxtV);
        rv = v.findViewById(R.id.chat_rv);
        msgEditTxt = v.findViewById(R.id.msgEditTxt);
        sendBtn = v.findViewById(R.id.sendBtn);

        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(view -> {
            salir();
        });

        ((MainActivity)getActivity()).cargarProfilePic(otherUser.getProfilePic(), imgV);
        usernameTxtV.setText(otherUser.getUsername());

        List<Message> msgList = bdHelper.getChatMsg(chatId);
        msgList.sort((msg1, msg2) -> {
            if(msg1.getSendTime().before(msg2.getSendTime())){
                return -1;
            }else if(msg1.getSendTime().after(msg2.getSendTime())){
                return 1;
            }else{
                return 0;
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ChatMsgAdapter(msgList));
        rv.getLayoutManager().scrollToPosition(msgList.size()-1);
        sendBtn.setOnClickListener(view -> {
            String msg = msgEditTxt.getText().toString().trim();
            if(!msg.isEmpty()){
                msgEditTxt.setText("");
                bdHelper.insertMsg(msg, MainActivity.logedUser.getId(), chatId);
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

    private void salir() {
        ((MainActivity)getActivity()).accionBack();
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).activarBtnNav();
    }
}