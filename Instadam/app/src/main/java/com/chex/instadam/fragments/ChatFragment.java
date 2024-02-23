package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Chat;
import com.chex.instadam.java.Post;
import com.chex.instadam.rv_adapter.ChatFeedAdapter;
import com.chex.instadam.rv_adapter.HomeFeedAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView rv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.app_name));
        ab.setDisplayHomeAsUpEnabled(false);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.home_tb_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        List<Chat> chats = new ArrayList<>();
        chats.add(new Chat(0, R.drawable.user_img_1, "Ey, cuánto tiempo", "ViciOso", "13:08"));
        chats.add(new Chat(0, R.drawable.user_img_2, "Mi juego, Luna de Plutón", "Kojima", "12:15"));
        chats.add(new Chat(0, R.drawable.user_img_3, "Acabo de encontrarme a un Uppua Epops", "María", "12:08"));

        rv = v.findViewById(R.id.chat_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ChatFeedAdapter(chats));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}