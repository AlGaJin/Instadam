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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

/**
 * Da funcionalidad al fragmento que permite modificar datos personales del usuario que ha iniciado sesión
 */
public class EditProfileFragment extends Fragment {

    private ImageView profilePicImgV;
    private EditText usernameEditTxt, emailEditTxt, dscEditTxt;
    private BBDDHelper bdHelper;
    private final User clonedUser = MainActivity.logedUser.clone(); //Se crea un clon del usuario que ha iniciado sesión por si sale sin guardar cambios que no se vea afectada la variable principal que almacena sus datos
    private final User logedUser = MainActivity.logedUser;
    //Variables que permiten la conexión con la base de datos en la nube (FireBase)
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().hide();

        bdHelper = new BBDDHelper(getContext());

        //Recuperación de los elementos de la vista
        usernameEditTxt = v.findViewById(R.id.edit_usernameEditTxt);
        emailEditTxt = v.findViewById(R.id.edit_emailEditTxt);
        dscEditTxt = v.findViewById(R.id.edit_dscEditTxt);
        profilePicImgV = v.findViewById(R.id.edit_userImgV);

        cargarDatos();

        //Permite cambiar la imagen al seleccionar una imagen o al hacer una foto con los respectivos intents
        ActivityResultLauncher<Intent> actResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                Uri imgUri = data.getData(); //Esto será nulo cuando la foto sea con la cámara, ya que no se almacena la imagen tomada
                if(imgUri == null){
                    profilePicImgV.setImageBitmap(data.getParcelableExtra("data"));
                }else {
                    profilePicImgV.setImageURI(imgUri); //En el caso de seleccionar una imagen de la galería de imagenes, sí tendrá un Uri para recuperar la imagen y setearla
                }
                uploadImageFB();
            }
        });

        //Acción para el botón que inicia la galería para escoger una imagen
        v.findViewById(R.id.galleryBtn).setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            actResultLauncher.launch(galleryIntent);
        });

        //Acción para el botón que inicia la cámara para hacer una foto
        v.findViewById(R.id.cameraBtn).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            actResultLauncher.launch(cameraIntent);
        });

        //Acción para el botón de guardar los datos
        v.findViewById(R.id.edit_saveBtn).setOnClickListener(view -> saveData());

        return v;
    }

    private void cargarDatos() { //Carga los datos del usuario para que los modifique desde un punto de partida si lo deseara
        usernameEditTxt.setText(logedUser.getUsername());
        emailEditTxt.setText(logedUser.getEmail());
        if(logedUser.getDscp() != null) dscEditTxt.setText(logedUser.getDscp());
        ((MainActivity)getActivity()).cargarImagenFireBase(logedUser.getProfilePic(), profilePicImgV);
    }

    /**
     * Guarda los cambios, si es posible, en la base de datos local
     */
    public void saveData(){
        //Recuperación de los datos introducidos en los campos
        String username = usernameEditTxt.getText().toString().trim();
        String email = emailEditTxt.getText().toString().trim();
        String dsc = dscEditTxt.getText().toString().trim();

        //Se cargan los datos en el usuario clonado
        if(!username.isEmpty() && !username.equals(logedUser.getUsername())){
            clonedUser.setUsername(username);
        }
        if(!email.isEmpty() && !email.equals(logedUser.getEmail())){
            clonedUser.setEmail(email);
        }
        if(!dsc.isEmpty() && !dsc.equals(logedUser.getDscp())){
            clonedUser.setDscp(dsc);
        }

        switch (bdHelper.editUser(clonedUser)){//La base de datos devuelve un número según:
            case 0://Se ha podido editar el usuario con éxito
                eliminarFotoAntigua();
                MainActivity.logedUser = clonedUser; //Ahora la variable que tenía los datos del usurio señalan al usuario clonado
                ((MainActivity)getActivity()).accionBack(); //Se vuelve al perfíl del usuario
                Toast.makeText(requireContext(), getResources().getString(R.string.cambios_guardados), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                usernameEditTxt.setError(getString(R.string.username_error));
                break;
            case 2:
                emailEditTxt.setError(getString(R.string.email_error));
                break;
        }
    }

    /**
     * Almacena en la nube la imagen que se ha seteado en el ImageView
     */
    public void uploadImageFB(){
        //Se crea un path que contendrá un nombre específico para identificar de qué usuario es
        String profilePicPath = "profilePics/" + MainActivity.logedUser.getUsername() + "_" + new Timestamp(System.currentTimeMillis()) + ".jpeg";
        StorageReference profPicRef = stRef.child(profilePicPath);//Se indica dónde va a almacenarse la imagen

        //Se recogen los datos de la imagene en formato Bitmap
        profilePicImgV.setDrawingCacheEnabled(true);
        profilePicImgV.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePicImgV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //Se suben los datos recopilados a FireBase
        UploadTask uploadTask = profPicRef.putBytes(data);
        //Si se produce un error al publicar la imagen se le comunica al usuario
        uploadTask.addOnFailureListener(e -> Toast.makeText(requireContext(), getResources().getString(R.string.firebase_error_upload), Toast.LENGTH_SHORT).show());

        uploadTask.addOnSuccessListener(e ->{
            clonedUser.setProfilePic(profilePicPath);
        });
    }

    /**
     * Elimina la anterior foto que había en FireBase para ahorrar especio, ya que no se va a volver a recuperar
     */
    public void eliminarFotoAntigua(){
        if(!logedUser.getProfilePic().equals("profilePics/DEFAULT.png")){ 
            StorageReference oldProfPicRef = stRef.child(logedUser.getProfilePic());
            oldProfPicRef.delete();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}