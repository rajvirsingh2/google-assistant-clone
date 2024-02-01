package com.example.googleassistantclone.functions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.googleassistantclone.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class GoogleLensActivity extends AppCompatActivity {

        private static final int CAMERA_REQUEST_PERMISSION = 200;
        private static final int STORAGE_REQUEST_PERMISSION = 400;
        private static final int IMAGE_PICK__GALLERY_REQUEST_PERMISSION = 1000;
        private static final int IMAGE_PICK_CAMERA_REQUEST_PERMISSION = 200;
        String[] cameraPermissions;
        String[] storagePermission;
        EditText mResulted;
        ImageView mPreviewIm;
        Uri image_uri;
        Button search;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_google_lens);
                ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setSubtitle("Insert or Click Pictures");

                mPreviewIm = findViewById(R.id.image_preview);
                mResulted = findViewById(R.id.result_edit_text);
                search = findViewById(R.id.search_button);
                cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                search.setOnClickListener(v -> {
                        String textoSearch = mResulted.getText().toString();
                        if(!textoSearch.isEmpty()){
                                Uri uri = Uri.parse("https://www.google.com/search?q=" +textoSearch);
                                Intent gSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(gSearchIntent);
                        }else {
                                Toast.makeText(GoogleLensActivity.this, "Add an Image with text", Toast.LENGTH_SHORT).show();
                        }
                });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.addimgbtn){
                        showImageInptDialog();
                }
                return super.onOptionsItemSelected(item);
        }

        private void showImageInptDialog() {
                String[] items = {"Camera", "Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("SET IMAGE");
                dialog.setItems(items, (dialog1, which) -> {
                        if(which == 0){
                                if(!checkCameraPermission()){
                                        requestCameraPermission();
                                }else{
                                        pickCamera();
                                }
                        }
                        if(which == 1){
                                if(!checkStoragePermission()){
                                        requestStoragePermission();
                                }else{
                                        pickGallery();
                                }
                        }
                });
                dialog.create().show();
        }

        private void pickGallery() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_PICK__GALLERY_REQUEST_PERMISSION);
        }

        private void requestStoragePermission() {
                ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_PERMISSION);
        }

        private boolean checkStoragePermission() {
                return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        }

        private void pickCamera() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.TITLE, "New Pic");
                contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Text");
                image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Intent cameraImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraImage.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                startActivityForResult(cameraImage, IMAGE_PICK_CAMERA_REQUEST_PERMISSION);
        }

        private void requestCameraPermission() {
                ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_PERMISSION);
        }

        private boolean checkCameraPermission() {
                boolean resultCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PERMISSION_GRANTED;
                boolean resultStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
                return resultCamera && resultStorage;

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                switch (requestCode) {
                        case CAMERA_REQUEST_PERMISSION -> {
                                if (grantResults.length > 0) {
                                        boolean cameraAccepted = grantResults[0] == PERMISSION_GRANTED;
                                        if (cameraAccepted) {
                                                pickCamera();
                                        } else {
                                                Toast.makeText(this, "Permission is Denied", Toast.LENGTH_SHORT).show();
                                        }
                                }
                        }
                        case STORAGE_REQUEST_PERMISSION -> {
                                if (grantResults.length > 0) {
                                        boolean writestorageAccepted = grantResults[0] == PERMISSION_GRANTED;
                                        if (writestorageAccepted) {
                                                pickGallery();
                                        } else {
                                                Toast.makeText(this, "Permission is Denied", Toast.LENGTH_SHORT).show();
                                        }
                                }
                        }
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(resultCode == RESULT_OK){
                        if(requestCode==IMAGE_PICK__GALLERY_REQUEST_PERMISSION){
                                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                        }

                        if(requestCode == IMAGE_PICK__GALLERY_REQUEST_PERMISSION){
                                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                        }
                }
                if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        if(resultCode == RESULT_OK){
                                assert result != null;
                                Uri resultUri = result.getUri();
                                mPreviewIm.setImageURI(resultUri);
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIm.getDrawable();
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                                if(recognizer.isOperational()){
                                        Toast.makeText(this,"Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                                        SparseArray<TextBlock> items = recognizer.detect(frame);
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for(int i = 0; i < items.size(); i++){
                                                TextBlock myItems = items.valueAt(i);
                                                stringBuilder.append(myItems.getValue());
                                                stringBuilder.append("\n");
                                        }
                                        mResulted.setText(stringBuilder.toString());
                                }
                        }
                }
        }
}