package com.example.android.tasheeh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


public class Gallery extends AppCompatActivity {

    ImageView im;
    Button fab;
    Button fab2;
    Integer CAMERA_REQUEST = 1, SELECT_FILE = 0;
    EditText Text;
    String strings = "";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        im = findViewById(R.id.ivImage);

        Text = findViewById(R.id.ed);
        Text.setText(null);

        fab = findViewById(R.id.but);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectImage();
            }
        });
        fab2 = findViewById(R.id.butt);
        if (im.getDrawable() == null)
            fab2.setEnabled(false);
        else fab2.setEnabled(true);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                Bitmap b = drawable.getBitmap();
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Could not get the text", Toast.LENGTH_SHORT).show();

                } else {

                    Frame frame = new Frame.Builder().setBitmap(b).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    Text.setText(sb.toString());
                    strings = strings + sb;
                }

            }
        });

        Button fab4 = findViewById(R.id.butt2);

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i;
                if ((!fab2.isEnabled()) && Text.getText().toString().trim().length() == 0)
                    Toast.makeText(getApplicationContext(), "Please upload an image and extract text", Toast.LENGTH_SHORT).show();
                else {
                    i = new Intent(Gallery.this, VerifyByExtractedText.class);
                    i.putExtra("string", Text.getText().toString());
                    startActivity(i);
                }
            }
        });


    }

    private void SelectImage() {
        final CharSequence[] items = {"Open Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Gallery.this);
        builder.setTitle("");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Open Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                }
                if (items[i].equals("Open Camera")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();

                }
            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_REQUEST) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                ImageView imageview = findViewById(R.id.ivImage);
                imageview.setImageBitmap(image);


            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();
                im.setImageURI(selectedImageUri);
                fab2.setEnabled(true);
            }
        }
    }


}
