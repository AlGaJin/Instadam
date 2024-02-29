package com.chex.instadam.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.chex.instadam.R;
import com.chex.instadam.SQLite.BBDDHelper;
import com.chex.instadam.activities.MainActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

/**
 * Da funcionalidad a la vista de crear una nueva publicación
 */
public class PostFragment extends Fragment {
    private boolean isImgVUsed = false; //Permite saber si se ha creado una imágen para publicar
    private Spinner postTypeSpinner; //Spinner para seleccionar el reino
    private ImageView postImgV;
    private EditText titleEditTxt, sciNameEditTxt, cmnNameEditTxt, dscEditTxt;
    private String fbPostPicPath; //Almacena el path de la imagen en FireBase
    private View shadowBG; //Vista que oscurece la pantalla mientras se publica la imagen
    private ProgressBar progressBar; //Barra de progreso que se muestra miestras se publica la imagen
    private BBDDHelper bdHelper;
    //Variables que permiten la conexión con la base de datos en la nube (FireBase)
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        bdHelper = new BBDDHelper(getContext()); //Conexión con la base de datos local

        //Recuperación del spinner de la vista y aplicación de un adapter para mostrar las diferentes opciones
        postTypeSpinner = v.findViewById(R.id.post_typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.post_type_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(adapter);
        switch (getArguments().getString("type")){ //Carga el spinner con la opción obtenida al crear el fragmento
            case "FNG":
                postTypeSpinner.setSelection(0);
                break;
            case "PLT":
                postTypeSpinner.setSelection(1);
                break;
            case "ANM":
                postTypeSpinner.setSelection(2);
                break;
        }

        //Recuperación de los elementos de la vista
        postImgV = v.findViewById(R.id.post_postImgV);
        titleEditTxt = v.findViewById(R.id.post_titleEditTxt);
        sciNameEditTxt = v.findViewById(R.id.post_sciNameEditTxt);
        cmnNameEditTxt = v.findViewById(R.id.post_cmnNameEditTxt);
        dscEditTxt = v.findViewById(R.id.post_dscEditTxt);
        shadowBG = v.findViewById(R.id.post_shadow_bg);
        progressBar = v.findViewById(R.id.progressBar);

        //Permite cambiar la imagen al seleccionar una imagen o al hacer una foto con los respectivos intents
        ActivityResultLauncher<Intent> actResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                Uri imgUri = data.getData();
                if(imgUri == null){
                    postImgV.setImageBitmap(data.getParcelableExtra("data"));
                }else{
                    postImgV.setImageURI(imgUri);
                }
                isImgVUsed = true;
            }
        });
        //Acción para el botón que inicia la galería para escoger una imagen
        v.findViewById(R.id.post_galleryBtn).setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            actResultLauncher.launch(galleryIntent);
        });
        //Acción para el botón que inicia la cámara para hacer una foto
        v.findViewById(R.id.post_cameraBtn).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            actResultLauncher.launch(cameraIntent);
        });
        //Acción para el botón que permite publicar la imagen
        v.findViewById(R.id.post_saveBtn).setOnClickListener(view -> {
            publicar();
        });

        return v;
    }

    /**
     * Publica una imagen con almenos el título y lo almacena en la base de datos local
     */
    public void publicar(){
        if(isImgVUsed) {//Comprueba que se haya subido seleccionado una imagen
            String title = titleEditTxt.getText().toString().trim();
            String sciName = sciNameEditTxt.getText().toString().trim();
            String cmnName = cmnNameEditTxt.getText().toString().trim();
            String dsc = dscEditTxt.getText().toString().trim();
            String type = postTypeSpinner.getSelectedItem().toString();

            if(title.isEmpty()){//Se necesita un título para poder publicar
                titleEditTxt.setError(getResources().getString(R.string.title_error));
            }else{
                uploadImageFB(); //Se sube la imagen a la base de datos remota
                bdHelper.insertPost(title, sciName, cmnName, dsc, type, MainActivity.logedUser, fbPostPicPath); //Se almacena la publicación en la base de datos local
            }
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.haz_selecciona_foto), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Almacena en la nube la imagen seteada al ImageView
     */
    public void uploadImageFB(){
        fbPostPicPath = "postPics/" + MainActivity.logedUser.getUsername() + "_" + new Timestamp(System.currentTimeMillis()) + ".jpeg";
        StorageReference profPicRef = stRef.child(fbPostPicPath);

        postImgV.setDrawingCacheEnabled(true);
        postImgV.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) postImgV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profPicRef.putBytes(data);

        shadowBG.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentprogress = (int) progress;
            progressBar.setProgress(currentprogress);
        });
        uploadTask.addOnFailureListener(e -> Toast.makeText(getContext(), getResources().getString(R.string.firebase_error_upload), Toast.LENGTH_SHORT).show());
        uploadTask.addOnSuccessListener(e -> {
            Toast.makeText(getContext(), getResources().getString(R.string.firebase_success_upload), Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).accionBack();
        });
    }
}