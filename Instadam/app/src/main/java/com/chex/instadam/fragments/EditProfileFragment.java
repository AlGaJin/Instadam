package com.chex.instadam.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.chex.instadam.R;
import com.chex.instadam.activities.MainActivity;

public class EditProfileFragment extends Fragment {

    private ImageView profilePicImgV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().hide();

        profilePicImgV = v.findViewById(R.id.edit_userImgV);

        ActivityResultLauncher<Intent> actResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                Uri imgUri = data.getData();
                if(imgUri == null){
                    profilePicImgV.setImageBitmap(data.getParcelableExtra("data"));
                }else{
                    profilePicImgV.setImageURI(data.getData());
                }

            }
        });

        //Acción para el botón que inicia la galería para escoger una imágen
        v.findViewById(R.id.galleryBtn).setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            actResultLauncher.launch(galleryIntent);
        });

        //Acción para el botón que inicia la cámara para hacer una foto
        v.findViewById(R.id.cameraBtn).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            actResultLauncher.launch(cameraIntent);
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}