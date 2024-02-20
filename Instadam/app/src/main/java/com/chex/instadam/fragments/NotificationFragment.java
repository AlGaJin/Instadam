package com.chex.instadam.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import com.chex.instadam.java.Notification;
import com.chex.instadam.rv_adapter.NotificationFeedAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView rv;
    private List<Notification> notifications;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        notifications = new ArrayList<>();
        notifications.add(new Notification("Nuevo mensaje", R.drawable.user_img_1));
        notifications.add(new Notification("Ha comenzado a seguirte", R.drawable.user_img_2));
        notifications.add(new Notification("Ha comentado tu publicación", R.drawable.user_img_3));
        rv = v.findViewById(R.id.fgt_noti_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new NotificationFeedAdapter(notifications));
        ((MainActivity) getActivity()).desactivarBtnNav();

        //Cambiar la función del botón Back en el móvil
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).accionBack();
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).activarBtnNav();
    }
}