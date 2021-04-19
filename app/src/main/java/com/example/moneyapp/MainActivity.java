package com.example.moneyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneyapp.SharedFunctions.SharedFunctionClass;
import com.example.moneyapp.imageclsicifiaction.Classifier;
import com.example.moneyapp.imageclsicifiaction.TensorFlowImageClassifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "model.tflite";
    private static final String MODEL_PATH1 = "model1.tflite";
    private static final String MODEL_PATH2 = "model2.tflite";
    private static final String MODEL_PATH3 = "model3.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels.txt";
    private static final String LABEL_PATH1 = "labels1.txt";
    private static final int INPUT_SIZE = 224;
    private Classifier classifier;
    private Classifier classifier2;
    private Executor executor = Executors.newSingleThreadExecutor();
    public ArrayList<Bitmap> byteArrayPicture = new ArrayList<Bitmap>();
    FirebaseStorage storage;
    StorageReference storageReference;
    public Boolean Othertype=false;
    public int OtherImagePosition;
    public Boolean DetectOrNot=false;
    public Boolean DetectOrNotmodel=false;
    public Boolean DetectOrNotmodel2=false;
    public Boolean freeOrNot=false;
    public String urilist="";
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference("UserDetails");
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public int line=0;
    ImageView userpic;
    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String cameraPermission[];
    String storagePermission[];
    Uri imageuri;
    Bitmap bit2;
    TextView click;
    ImageView cam,gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        freeOrNot=false;
        cam = findViewById(R.id.cam);
        gallery = findViewById(R.id.gallery);

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showgalleryPicDialog();
            }
        });
        mDatabase.child("M").child("status").child("value").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String data =(String) dataSnapshot.getValue(String.class);
                        int i=Integer.parseInt(data);
                        if(i<10){
                            if(i+line>=3){
                                mDatabase.child("M").child("status").child("value").setValue("100");
                                SharedFunctionClass.gotoActivityS(getApplicationContext());
                            }
                            else{
                                mDatabase.child("M").child("status").child("value").setValue("100");
                                SharedFunctionClass.gotoActivityF(getApplicationContext());
                            }
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("dd", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }
    private void showImagePicDialog() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
        } else {
            pickFromGallery();
        }
    }
    private void showgalleryPicDialog() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        } else {
            pickFromGallery();
        }
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission, STORAGE_REQUEST);
        }
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission, CAMERA_REQUEST);
        }
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                showDialog();
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    bit2 = bitmap.copy(bitmap.getConfig(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                backgroundCacification newbackGP = new backgroundCacification(bitmap);
                newbackGP.start();
            }
        }
    }
    class backgroundCacification extends Thread {

        Bitmap imageset;

        backgroundCacification(Bitmap imagesend) {
            this.imageset = imagesend;
        }

        @Override
        public void run() {
            //GlobleVariableClass.ClaimNo=Geanrate_Name_Cliame()+Geanrate_Number_Claime();
            imageset = Bitmap.createScaledBitmap(imageset, 2600, 1300, true);
            initTensorFlowAndLoadModel(MODEL_PATH,LABEL_PATH);
            while (true) {
                if (DetectOrNotmodel) {
                    DetectOrNotmodel=false;
                    final Bitmap bmpimageDetected = Bitmap.createScaledBitmap(imageset, INPUT_SIZE, INPUT_SIZE, false);
                    final List<Classifier.Recognition> results = classifier.recognizeImage(bmpimageDetected);
                    if (results.get(0).getTitle().equals("A")) {
                        initTensorFlowAndLoadModel(MODEL_PATH1,LABEL_PATH1);
                        mDatabase.child("M").child("note").setValue("500");
                        uploadImage(imageset);
                        while(true){
                            if (DetectOrNotmodel2) {
                                DetectOrNotmodel2=false;
                                int x = imageset.getWidth();
                                int y = imageset.getHeight();
                                bit2=Bitmap.createScaledBitmap(bit2, 2600, 1300, true);
                                Bitmap output=Bitmap.createBitmap(bit2,0,800,(int)x/5,500);
                                final Bitmap bmpimageDetected1 = Bitmap.createScaledBitmap(output, INPUT_SIZE, INPUT_SIZE, false);
                                final List<Classifier.Recognition> results1 = classifier2.recognizeImage(bmpimageDetected1);
                                if (results1.get(0).getTitle().equals("y")) {
                                    line=1;
                                }
                                if (results1.get(0).getTitle().equals("n")) {
                                    line=0;
                                }
                                break;
                            }
                        }

                    }
                    if (results.get(0).getTitle().equals("B")) {
                        mDatabase.child("M").child("note").setValue("1000");
                        initTensorFlowAndLoadModel(MODEL_PATH2,LABEL_PATH1);
                        uploadImage(imageset);
                        while(true){
                            if (DetectOrNotmodel2) {
                                DetectOrNotmodel2=false;
                                int x = imageset.getWidth();
                                int y = imageset.getHeight();
                                bit2=Bitmap.createScaledBitmap(bit2, 2600, 1300, true);
                                Bitmap output=Bitmap.createBitmap(bit2,0,800,(int)x/5,500);
                                final Bitmap bmpimageDetected1 = Bitmap.createScaledBitmap(output, INPUT_SIZE, INPUT_SIZE, false);
                                final List<Classifier.Recognition> results1 = classifier2.recognizeImage(bmpimageDetected1);
                                if (results1.get(0).getTitle().equals("y")) {
                                    line=1;
                                }
                                if (results1.get(0).getTitle().equals("n")) {
                                    line=0;
                                }
                                break;
                            }
                        }
                    }
                    if (results.get(0).getTitle().equals("C")) {
                        mDatabase.child("M").child("note").setValue("5000");
                        initTensorFlowAndLoadModel(MODEL_PATH3,LABEL_PATH1);
                        uploadImage(imageset);
                        while(true){
                            if (DetectOrNotmodel2) {
                                DetectOrNotmodel2=false;
                                int x = imageset.getWidth();
                                int y = imageset.getHeight();
                                bit2=Bitmap.createScaledBitmap(bit2, 2600, 1300, true);
                                Bitmap output=Bitmap.createBitmap(bit2,0,800,(int)x/5,500);
                                final Bitmap bmpimageDetected1 = Bitmap.createScaledBitmap(output, INPUT_SIZE, INPUT_SIZE, false);
                                final List<Classifier.Recognition> results1 = classifier2.recognizeImage(bmpimageDetected1);
                                if (results1.get(0).getTitle().equals("y")) {
                                    line=1;
                                }
                                if (results1.get(0).getTitle().equals("n")) {
                                    line=0;
                                }
                                break;
                            }
                        }
                    }
                    if (results.get(0).getTitle().equals("D")) {
                        SharedFunctionClass.gotoActivityF(getApplicationContext());
                    }
                    break;
                }}
                while (true) {
                    if (1 == stringToList(urilist).size()) {
                        mDatabase.child("M").child("value").child("value").setValue(urilist);
                        break;
                    }
                }

            }
        }
        public List<String> stringToList(String string) {
            if (string.isEmpty()) {
                return new ArrayList<String>();
            } else {
                String str[] = string.split("  ");
                List<String> al = new ArrayList<String>();
                al = Arrays.asList(str);
                return al;
            }

        }

        public String Geanrate_Name() {
            String DATA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghtjklmnopqrstunvzxyz";
            Random RANDOM = new Random();
            StringBuilder sb = new StringBuilder(10);

            for (int i = 0; i < 10; i++) {
                sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
            }

            return sb.toString();
        }

        private void initTensorFlowAndLoadModel(String MODEL_PATHx,String LABEL_PATHx) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(LABEL_PATHx.contentEquals("labels1.txt")){
                            classifier2 = TensorFlowImageClassifier.create(
                                    getAssets(),
                                    MODEL_PATHx,
                                    LABEL_PATHx,
                                    INPUT_SIZE,
                                    QUANT);
                            DetectOrNotmodel2 = true;
                        }
                        classifier = TensorFlowImageClassifier.create(
                                getAssets(),
                                MODEL_PATHx,
                                LABEL_PATHx,
                                INPUT_SIZE,
                                QUANT);
                        DetectOrNotmodel = true;

                    } catch (final Exception e) {
                        throw new RuntimeException("Error initializing TensorFlow!", e);
                    }
                }
            });
        }


        private void uploadImage(Bitmap converetdImage) {

            if (converetdImage != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                converetdImage.recycle();
                final String randomname = Geanrate_Name();
                StorageReference ref = storageReference.child("uploaded/" + randomname + ".jpeg");
                ref.putBytes(byteArray)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //progressDialog.dismiss();
                                Geturl(randomname);
                                try {
                                    // code runs in a thread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Uploaded Image", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (final Exception ex) {
                                    Log.i("---", "Exception in thread");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                final Exception xe = e;
                                //progressDialog.dismiss();
                                try {
                                    // code runs in a thread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Failed " + xe.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (final Exception ex) {
                                    Log.i("---", "Exception in thread");
                                }

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                /*double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploading Data Please Wait!  "+(int)progress+"%");*/
                            }
                        });
            }
        }

        public void Geturl(String name) {
            storageReference.child("uploaded/" + name + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    urilist = urilist + url + "  ";

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }

        public void showDialog() {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AlertDialogTheme);
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.alart_dialog, null);
            ImageView xx, xxx;
            xx = dialoglayout.findViewById(R.id.xx);
            xxx = dialoglayout.findViewById(R.id.xxx);
            final Animation anim = AnimationUtils.loadAnimation(this, R.anim.animationrotate);
            xx.startAnimation(anim);
            builder.setView(dialoglayout);
            builder.setCancelable(false).show();

        }
    }

