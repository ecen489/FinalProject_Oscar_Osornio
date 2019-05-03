package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TextCapture extends AppCompatActivity {

    private Button cameraButton, selectButton, logoutButton;
    private ImageView imgDisplay;
    private EditText fileName;
    private String nameText;

    private ListView listView;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_capture);

        Intent intent = getIntent();

        fileName = findViewById(R.id.editText);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if(mUser.equals(null)){
            finish();
        }

        cameraButton = findViewById(R.id.camera);
        selectButton = findViewById(R.id.select);
        logoutButton = findViewById(R.id.logout);

        imgDisplay = findViewById(R.id.imageView);

        listView = findViewById(R.id._dynamic);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent snapshot = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(snapshot, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);

        Bitmap mSelectedImage = (Bitmap) intent.getExtras().get("data");
        imgDisplay.setImageBitmap(mSelectedImage);
        imgDisplay.setVisibility(View.VISIBLE);
        //imgDisplay.setRotation(90f);

        nameText = fileName.getText().toString();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                ("gs://final-project-89f8e.appspot.com").child(nameText);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {
                                processCloudTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            Toast.makeText(getApplicationContext(),
                    "No text found", Toast.LENGTH_SHORT).show();
            return;
        }
        arrayList.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    arrayList.add(words.get(l).getText());
                }
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void logoutClick(View view){
        mAuth.signOut();
        mUser = null;
        finish();
    }
}
