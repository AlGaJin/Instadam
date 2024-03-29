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

/**
 * Futura implementación, solo está de muestra.
 * Se plantea:
 *  -Poder filtrar las notificaciones
 *  -Crear notificaciones según el tipo
 *  -Según el tipo de notificación que haya una acción:
 *      +Nuevo seguidor, permitir seguir de nuevo
 *      +Nueva publicación, abrir la publicación nueva
 *      +Nuevo mensaje, abrir el chat
 */
public class NotificationFragment extends Fragment {
    private RecyclerView rv;
    private List<Notification> notifications;

    //Permite modificar el toolBar del MainActivity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    //Modificaciones del toolbar del MainActivity. Así se consigue un solo toolbar para todos los fragmentos
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

        //Creación de notificaciones artificiales
        notifications = new ArrayList<>();
        notifications.add(new Notification("Nuevo mensaje", R.drawable.user_img_1));
        notifications.add(new Notification("Ha comenzado a seguirte", R.drawable.user_img_2));
        notifications.add(new Notification("Ha comentado tu publicación", R.drawable.user_img_3));

        //Configuración del RecyclerView
        rv = v.findViewById(R.id.fgt_noti_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new NotificationFeedAdapter(notifications));
        ((MainActivity) getActivity()).desactivarBtnNav();

        return v;
    }
}