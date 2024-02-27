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

public class PostFragment extends Fragment {
    private boolean isImgVUsed = false;
    private Spinner postTypeSpinner;
    private ImageView postImgV;
    private EditText titleEditTxt, sciNameEditTxt, cmnNameEditTxt, dscEditTxt;
    private String fbPostPicPath;
    private View shadowBG;
    private ProgressBar progressBar;
    private BBDDHelper bdHelper;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference stRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        bdHelper = new BBDDHelper(getContext());

        postTypeSpinner = v.findViewById(R.id.post_typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.post_type_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(adapter);
        switch (getArguments().getString("type")){
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

        postImgV = v.findViewById(R.id.post_postImgV);
        titleEditTxt = v.findViewById(R.id.post_titleEditTxt);
        sciNameEditTxt = v.findViewById(R.id.post_sciNameEditTxt);
        cmnNameEditTxt = v.findViewById(R.id.post_cmnNameEditTxt);
        dscEditTxt = v.findViewById(R.id.post_dscEditTxt);

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

        v.findViewById(R.id.post_galleryBtn).setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            actResultLauncher.launch(galleryIntent);
        });

        v.findViewById(R.id.post_cameraBtn).setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            actResultLauncher.launch(cameraIntent);
        });

        v.findViewById(R.id.post_saveBtn).setOnClickListener(view -> {
            publicar();
        });

        shadowBG = v.findViewById(R.id.post_shadow_bg);
        progressBar = v.findViewById(R.id.progressBar);

        return v;
    }

    public void publicar(){
        if(isImgVUsed) {
            String title = titleEditTxt.getText().toString().trim();
            String sciName = sciNameEditTxt.getText().toString().trim();
            String cmnName = cmnNameEditTxt.getText().toString().trim();
            String dsc = dscEditTxt.getText().toString().trim();
            String type = postTypeSpinner.getSelectedItem().toString();

            if(title.isEmpty()){
                titleEditTxt.setError(getResources().getString(R.string.title_error));
            }else{
                uploadImageFB();
                bdHelper.insertPost(title, sciName, cmnName, dsc, type, MainActivity.logedUser, fbPostPicPath);
            }
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.haz_selecciona_foto), Toast.LENGTH_SHORT).show();
        }
    }

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