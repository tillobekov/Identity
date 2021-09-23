package com.example.identity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.identity.model.DocumentReaderResponse;
import com.example.identity.service.APIUtils;
import com.example.identity.service.DocumentReaderService;
import com.example.identity.util.Util;
//import com.regula.facesdk.enums.eInputFaceType;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstStepActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri imageUri;
    private Bitmap imageBitmap;

    private boolean validity = false;
    private boolean ready = false;
    private String docFilename = "";

    DocumentReaderService documentReaderService;
    DocumentReaderResponse data = null;

    ImageView imageView;
    TextView textView;
    Button buttonCamera;
    Button buttonGallery;
    Button buttonNext;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_step);

        imageView = findViewById(R.id.step1_image);
        imageView.getLayoutParams().height = 500;

        textView = findViewById(R.id.step1_text_scan);

        buttonCamera = findViewById(R.id.step1_button_camera);
        buttonGallery = findViewById(R.id.step1_button_gallery);
        buttonNext = findViewById(R.id.step1_button_next);

        progressBar = findViewById(R.id.step1_progress);

        buttonCamera.setOnClickListener(v -> {
            startDocumentScanActivity();
        });

        buttonGallery.setOnClickListener(v -> {
            startDocumentSelectActivity();
        });

        buttonNext.setOnClickListener(v -> {

            if(ready){

                progressBar.setVisibility(View.VISIBLE);
                buttonNext.setEnabled(false);
                buttonGallery.setEnabled(false);
                buttonCamera.setEnabled(false);

//                Toast.makeText(FirstStepActivity.this, "processing...", Toast.LENGTH_SHORT).show();
                processDocument(getImageBitmap(imageView));
            }else{
                Toast.makeText(FirstStepActivity.this, "Please select or scan the document", Toast.LENGTH_SHORT).show();
            }


            //data = new DocumentReaderResponse();
//            if(data != null){
//                startResultsActivity(data);
//            }else{
//                Toast.makeText(FirstStepActivity.this, "Data is null", Toast.LENGTH_SHORT).show();
//            }
        });
    }

    //    @SuppressLint("ResourceType")
//    private void showMenu(ImageView imageView, int i) {
//        PopupMenu popupMenu = new PopupMenu(FirstStepActivity.this, imageView);
//        popupMenu.setOnMenuItemClickListener(menuItem -> {
//            switch (menuItem.getItemId()) {
//                case R.id.gallery:
//                    openGallery(i);
//                    return true;
//                case R.id.camera:
//                    startDocumentCaptureActivity(imageView);
//                    return true;
//                default:
//                    return false;
//            }
//        });
//        popupMenu.getMenuInflater().inflate(R.layout.menu, popupMenu.getMenu());
//
//        popupMenu.show();
//    }
//
    private Bitmap getImageBitmap(ImageView imageView) {
        imageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        return bitmap;
    }
    //
    private void startDocumentSelectActivity() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void startDocumentScanActivity() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                //imageView.setTag(eInputFaceType.ift_DocumentPrinted);
//            textViewValidity.setText("Validity: null");
//            textViewInfo.setText("Data: null");
            }else if (requestCode == REQUEST_IMAGE_CAPTURE){
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    imageView.setImageBitmap(thumbnail);
                    Object imageurl = getRealPathFromURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//            imageView.setTag(eInputFaceType.ift_DocumentPrinted);

//            textViewValidity.setText("Validity: null");
//            textViewInfo.setText("Data: null");
            }

            ready = true;
            //processDocument(getImageBitmap(imageView));

        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void processDocument(Bitmap file) {

        documentReaderService = APIUtils.getDocumentReaderService();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //file.recycle();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", Util.getNextPassportFilename(), requestBody);

        Call<DocumentReaderResponse> call = documentReaderService.process(body);
        call.enqueue(new Callback<DocumentReaderResponse>() {
            @Override
            public void onResponse(Call<DocumentReaderResponse> call, Response<DocumentReaderResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(FirstStepActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    data = response.body();
//                    textViewValidity.setText("Validity: " + data.getPassportData().isValid());
                    //textView.setText("Data: " + data.getPassportData());
                    validity = data.getPassportData().isValid();
                    docFilename = data.getFilename();

                    progressBar.setVisibility(View.GONE);
                    buttonNext.setEnabled(true);
                    buttonGallery.setEnabled(true);
                    buttonCamera.setEnabled(true);

                    startResultsActivity(data);
//                    buttonCamera.setEnabled(true);
//                    buttonGallery.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<DocumentReaderResponse> call, Throwable t) {
                Toast.makeText(FirstStepActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                data = null;
            }
        });



        //MatchFacesRequest matchRequest = new MatchFacesRequest();

//        Image firstImage = new Image();
//        firstImage.setImage(document);
//        firstImage.imageType = (Integer) imageView.getTag();
//        //matchRequest.images.add(firstImage);
//
//        Image secondImage = new Image();
//        secondImage.setImage(second);
//        secondImage.imageType = (Integer) imageView2.getTag();
//        matchRequest.images.add(secondImage);
//
//        FaceReaderService.Instance().matchFaces(matchRequest, (i, matchFacesResponse, s) -> {
//            if (matchFacesResponse != null && matchFacesResponse.matchedFaces != null) {
//                double similarity = matchFacesResponse.matchedFaces.get(0).similarity;
//                textViewSimilarity.setText("Similarity: " + String.format("%.2f", similarity * 100) + "%");
//            } else {
//                textViewSimilarity.setText("Similarity: null");
//            }
//
//            buttonMatch.setEnabled(true);
//            buttonLiveness.setEnabled(true);
//            buttonClear.setEnabled(true);
//        });
    }

    private void startResultsActivity(DocumentReaderResponse data){
        Intent intent = new Intent(this, FirstStepResultsActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}