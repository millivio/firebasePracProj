package pk.edu.pucit.firebaseProj;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class addPerson extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST=1;
    private Button buttonUploadImage, buttonAdd;
    private EditText editTextName, editTextEmail;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Task<Uri> mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        buttonUploadImage= findViewById(R.id.button_upload_image);
        buttonAdd=findViewById(R.id.button_add);
        editTextName=findViewById(R.id.edit_text_name);
        editTextEmail=findViewById(R.id.edit_text_email);
        mImageView=findViewById(R.id.image_view);
        mProgressBar=findViewById(R.id.progress_bar);

        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && !mUploadTask.isSuccessful()) {
                    Toast.makeText(addPerson.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
                      addData();
                }
            }
        });

    }
    private void openFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PICK_IMAGE_REQUEST && resultCode == RESULT_OK
         && data != null &&  data.getData() != null)
        {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);

        }
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void addData(){

        if(!editTextEmail.getText().toString().trim().equals("") && !editTextName.getText().toString().trim().equals("") && mImageUri !=null )
        {
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            final StorageReference fileRef= mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
//           mUploadTask= fileRef.putFile(mImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Handler handler =new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//                                }
//                            },500);
//                            Toast.makeText(addPerson.this, "Upload Successful", Toast.LENGTH_SHORT).show();
//                            Upload upload= new Upload(editTextName.getText().toString().trim(), editTextEmail.getText().toString().trim(),
//                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                            String uploadId= mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(upload);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(addPerson.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                 .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                      @Override
//                         public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                     double progress=(100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                mProgressBar.setProgress((int) progress);
//                if((int) progress ==100)
//                {
//                    Intent intent = new Intent(addPerson.this,ProfileActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//            }
//        });
            //-----------METHOD 2----------------------SUCCESSFUL
           mUploadTask=fileRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(addPerson.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            Upload upload= new Upload(editTextName.getText().toString().trim(), editTextEmail.getText().toString().trim(),
                                    downloadUri.toString());
                            String uploadId= mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        Intent intent = new Intent(addPerson.this,ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }) .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addPerson.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
            });

        }else {

         if (editTextName.getText().toString().trim().equals("")) {
             Toast.makeText(this,"No Name Entered", Toast.LENGTH_SHORT).show();
            }
         if (editTextEmail.getText().toString().trim().equals("")) {
             Toast.makeText(this,"No Email Entered", Toast.LENGTH_SHORT).show();
            }
         if (mImageUri == null) {
             Toast.makeText(this,"No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(addPerson.this,ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}