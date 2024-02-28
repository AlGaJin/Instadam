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
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.Chat;
import com.chex.instadam.rv_adapter.ChatsFeedAdapter;

import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView rv;
    private BBDDHelper bdHelper;

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
        View v = inflater.inflate(R.layout.fragment_chats, container, false);

        bdHelper = new BBDDHelper(getContext());

        List<Chat> chats = bdHelper.getChats(MainActivity.logedUser.getId());

        rv = v.findViewById(R.id.chat_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ChatsFeedAdapter(chats));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}