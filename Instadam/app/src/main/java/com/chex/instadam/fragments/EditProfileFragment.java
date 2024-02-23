package com.chex.instadam.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.chex.instadam.java.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;

public class EditProfileFragment extends Fragment {

    private ImageView profilePicImgV;
    private EditText usernameEditTxt, emailEditTxt, dscEditTxt;
    private BBDDHelper bdHelper;
    private final User clonedUser = MainActivity.logedUser.clone();
    private final User logedUser = MainActivity.logedUser;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().hide();

        bdHelper = new BBDDHelper(getContext());

        usernameEditTxt = v.findViewById(R.id.edit_usernameEditTxt);
        emailEditTxt = v.findViewById(R.id.edit_emailEditTxt);
        dscEditTxt = v.findViewById(R.id.edit_dscEditTxt);

        profilePicImgV = v.findViewById(R.id.edit_userImgV);

        cargarDatos();

        ActivityResultLauncher<Intent> actResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                Uri imgUri = data.getData();
                if(imgUri == null){
                    profilePicImgV.setImageBitmap(data.getParcelableExtra("data"));
                }else{
                    profilePicImgV.setImageURI(imgUri);
                }
                uploadImageFB();
            }
        });

        //Acción para el botón que inicia la galería para escoger una imágen
        v.findViewById(R.id.galleryBtn).setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            actResultLauncher.launch(galleryIntent);
        });

        //Acción para el botón que inicia la cámara para hacer una foto
        v.findViewById(R.id.cameraBtn).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            actResultLauncher.launch(cameraIntent);
        });

        v.findViewById(R.id.edit_saveBtn).setOnClickListener(view -> saveData());

        return v;
    }

    private void cargarDatos() {
        usernameEditTxt.setText(logedUser.getUsername());
        emailEditTxt.setText(logedUser.getEmail());
        if(logedUser.getDscp() != null) dscEditTxt.setText(logedUser.getDscp());
        ((MainActivity)getActivity()).cargarProfilePic(logedUser, profilePicImgV);
    }

    public void saveData(){
        String username = usernameEditTxt.getText().toString().trim();
        String email = emailEditTxt.getText().toString().trim();
        String dsc = dscEditTxt.getText().toString().trim();

        if(!username.isEmpty() && !username.equals(logedUser.getUsername())){
            clonedUser.setUsername(username);
        }

        if(!email.isEmpty() && !email.equals(logedUser.getEmail())){
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                clonedUser.setEmail(email);
            }else{
                emailEditTxt.setError(getResources().getString(R.string.email_error));
            }
        }

        if(!dsc.isEmpty() && !dsc.equals(logedUser.getDscp())){
            clonedUser.setDscp(dsc);
        }

        switch (bdHelper.editUser(clonedUser)){
            case 0:
                MainActivity.logedUser = clonedUser;
                ((MainActivity)getActivity()).accionBack();
                Toast.makeText(getContext(), getResources().getString(R.string.cambios_guardados), Toast.LENGTH_SHORT).show();
                onDestroy();
                break;
            case 1:
                usernameEditTxt.setError(getString(R.string.username_error));
                break;
            case 2:
                emailEditTxt.setError(getString(R.string.email_error));
                break;
        }
    }

    public void uploadImageFB(){
        String profilePicPath = "profilePics/" + MainActivity.logedUser.getUsername() + "_" + new Timestamp(System.currentTimeMillis()) + ".jpeg";
        StorageReference profPicRef = stRef.child(profilePicPath);

        profilePicImgV.setDrawingCacheEnabled(true);
        profilePicImgV.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) profilePicImgV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profPicRef.putBytes(data);
        uploadTask.addOnFailureListener(e -> Toast.makeText(getContext(), getResources().getString(R.string.firebase_error_upload), Toast.LENGTH_SHORT).show());
        uploadTask.addOnSuccessListener(e ->{
            if(clonedUser.getProfilePic() != null && !clonedUser.getProfilePic().isEmpty()){
                StorageReference oldProfPicRef = stRef.child(clonedUser.getProfilePic());
                oldProfPicRef.delete();
            }
            clonedUser.setProfilePic(profilePicPath);
            Toast.makeText(getContext(), getResources().getString(R.string.firebase_success_upload), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}