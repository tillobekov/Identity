package com.example.identity;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.identity.model.FaceSDKResponse;
import com.example.identity.model.VisionLabLiveness;
import com.example.identity.model.VisionLabResponse;
import com.example.identity.service.APIUtils;
import com.example.identity.service.FaceSDKService;
import com.example.identity.service.MultipartUtility;
import com.example.identity.service.VisionLabsService;
import com.example.identity.util.Util;
import com.regula.facesdk.Face;
import com.regula.facesdk.configuration.LivenessConfiguration;
import com.regula.facesdk.enums.eInputFaceType;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondStepActivity extends AppCompatActivity {

    ImageView imageView;
    Button buttonCamera;

//    RadioButton checkRegula;
//    RadioButton checkVisionLabs;

    AlertDialog initDialog;

    Uri imageUri;
    String imageUriPath;

    private FaceSDKService faceSDKService;
    private VisionLabsService visionLabsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_step);

        imageView = findViewById(R.id.step2_image);
        imageView.getLayoutParams().height = 500;
        buttonCamera = findViewById(R.id.step2_button_camera);

//        checkRegula = findViewById(R.id.radio_regula);
//        checkVisionLabs = findViewById(R.id.radio_visionlabs);

//        checkRegula.setOnClickListener(v -> {
//            checkRegula.setChecked(true);
//            checkVisionLabs.setChecked(false);
//        });
//
//
//        checkVisionLabs.setOnClickListener(v -> {
//            checkRegula.setChecked(false);
//            checkVisionLabs.setChecked(true);
//        });

//        buttonFinish = findViewById(R.id.step3_button_finish);
//        buttonFinish.setEnabled(false);
//


//        buttonCamera.setOnClickListener(v -> {
//            //buttonCamera.setEnabled(false);
//            startLiveness(checkRegula.isChecked(), checkVisionLabs.isChecked());
//        });

        buttonCamera.setOnClickListener(v -> {
            //buttonCamera.setEnabled(false);
            startLiveness(false,true);
        });
//
//        buttonFinish.setOnClickListener(v -> {
//            finish();
//        });

    }

    private void startLiveness(boolean r, boolean v) {

        if(r && !v){

            LivenessConfiguration configuration = new LivenessConfiguration.Builder().setCameraSwitchEnabled(true).build();

            Intent intent = getIntent();
            int type = intent.getIntExtra("type", 0);
            byte[] portrait = intent.getByteArrayExtra("portrait");

            Face.Instance().startLiveness(SecondStepActivity.this, configuration, livenessResponse -> {
                if (livenessResponse != null && livenessResponse.getBitmap() != null) {
                    imageView.setImageBitmap(livenessResponse.getBitmap());
                    imageView.setTag(eInputFaceType.ift_Live);

                    if (livenessResponse.liveness == 0) {

                        initDialog = showDialog("Face Processing");

                        Toast.makeText(SecondStepActivity.this, "Liveness: passed", Toast.LENGTH_SHORT).show();
                        if(type == 1){
                            matchFaces2(portrait, livenessResponse.getBitmap());
                        }else if(type == 2){
                            matchFaces(livenessResponse.getBitmap());
                        }

                    } else {
                        Toast.makeText(SecondStepActivity.this, "Liveness: unknown", Toast.LENGTH_SHORT).show();
                        startResultActivity(false, false, 0.0, type);
                    }
                } else {
                    Toast.makeText(SecondStepActivity.this, "Liveness: null", Toast.LENGTH_SHORT).show();
                    startResultActivity(false, false, 0.0, type);
                }

            });

        }else if(!r && v){
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            imageUriPath = imageUri.getPath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 111);
        }else{
            Toast.makeText(SecondStepActivity.this, "Check at least one", Toast.LENGTH_SHORT).show();
            //buttonCamera.setEnabled(true);
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        initDialog = showDialog("Liveness Processing");

        if (resultCode == RESULT_OK && requestCode == 111){

            Intent intent = getIntent();
            int type = intent.getIntExtra("type", 0);
            byte[] portrait = intent.getByteArrayExtra("portrait");




            try {

                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                thumbnail = rotateImage(thumbnail, 270);
//
//                ExifInterface ei = new ExifInterface(imageUriPath);
//                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                        ExifInterface.ORIENTATION_UNDEFINED);
//
//                Bitmap rotatedBitmap = null;
//                switch(orientation) {
//
//                    case ExifInterface.ORIENTATION_ROTATE_90:
//                        rotatedBitmap = rotateImage(thumbnail, 90);
//                        break;
//
//                    case ExifInterface.ORIENTATION_ROTATE_180:
//                        rotatedBitmap = rotateImage(thumbnail, 180);
//                        break;
//
//                    case ExifInterface.ORIENTATION_ROTATE_270:
//                        rotatedBitmap = rotateImage(thumbnail, 270);
//                        break;
//
//                    case ExifInterface.ORIENTATION_NORMAL:
//                    default:
//                        rotatedBitmap = thumbnail;
//                }

//                MultipartUtility multipart = new MultipartUtility("http://195.158.30.142:5000/6/liveness", "UTF-8");
//
//                multipart.addFilePart(Util.getNextImageFilename(),
//                        new File(getRealPathFromURI(imageUri)));
//
//                List<String> response = multipart.finish();
//
//                for (String line : response) {
//                    if (initDialog.isShowing()) {
//                        initDialog.dismiss();
//                    }
//                    Toast.makeText(SecondStepActivity.this, line, Toast.LENGTH_SHORT).show();
//                }


//                test(imageUri);
                visionLabLiveness(portrait, thumbnail, type);


//                initDialog = showDialog("Face Processing");
//
//                Toast.makeText(SecondStepActivity.this, "Liveness: passed", Toast.LENGTH_SHORT).show();
//                if(type == 1){
//                    matchFaces2(portrait, thumbnail);
//                }else if(type == 2){
//                    matchFaces(thumbnail);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    private void test(Uri uri){
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("image", getRealPathFromURI(uri),
                            RequestBody.create(MediaType.parse("image/jpeg"),
                                    new File(getRealPathFromURI(uri))))
                    .build();
            Request request = new Request.Builder()
                    .url("http://195.158.30.142:5000/6/liveness")
                    .method("POST", body)
//                    .addHeader("Luna-Account-Id", "00000000-0000-4000-a000-000000000002")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();

            if (initDialog.isShowing()) {
                initDialog.dismiss();
            }

            Toast.makeText(SecondStepActivity.this, response.isSuccessful() + "", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void visionLabLiveness(byte[] portrait, Bitmap thumbnail, int type){

        visionLabsService = APIUtils.getVisionLabsService1();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
//        thumbnail.recycle();
        //file.recycle();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", Util.getNextImageFilename(), requestBody);


        Call<VisionLabResponse> call = visionLabsService.liveness1(body);
        call.enqueue(new Callback<VisionLabResponse>() {
            @Override
            public void onResponse(Call<VisionLabResponse> call, Response<VisionLabResponse> response) {

                //Toast.makeText(SecondStepActivity.this, "OKKKK", Toast.LENGTH_SHORT).show();
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                if(response.isSuccessful()){
                    Toast.makeText(SecondStepActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();

                    VisionLabResponse data = response.body();

//                    Toast.makeText(SecondStepActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                    Log.d("RESULT::::  ", data.toString());


                    if(data.getImages().get(0) == null){
                        Toast.makeText(SecondStepActivity.this, "Failed to read the data", Toast.LENGTH_SHORT).show();
                        startResultActivity(false, false, 0.0, type);
                    }else{
                        VisionLabLiveness liveness = data.getImages().get(0).getLiveness();
                        if(liveness == null){
                            startResultActivity(false, false, 0.0, type);
                        }
                        if(liveness.getPrediction() == 1 && liveness.getEstimations().getProbability() >= 0.75 && liveness.getEstimations().getQuality() >= 0.6){
                            initDialog = showDialog("Face Processing");

                            Toast.makeText(SecondStepActivity.this, "Liveness: passed", Toast.LENGTH_SHORT).show();
                            if(type == 1){
                                matchFaces2(portrait, thumbnail);
                            }else if(type == 2){
                                matchFaces(thumbnail);
                            }
                        }else {
                            Toast.makeText(SecondStepActivity.this, "Liveness failed", Toast.LENGTH_SHORT).show();
                            startResultActivity(false, false, 0.0, type);
                        }
                    }
                }else{
                    try {
                        Toast.makeText(SecondStepActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<VisionLabResponse> call, Throwable t) {
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                Toast.makeText(SecondStepActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                startResultActivity(false, true, 0.0, 2);
            }
        });


       // Toast.makeText(SecondStepActivity.this, "Nothing", Toast.LENGTH_SHORT).show();

    }


    private void matchFaces(Bitmap file) {

        faceSDKService = APIUtils.getFaceSDKService();
        Intent intent  = getIntent();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //file.recycle();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", Util.getNextPassportFilename(), requestBody);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("docFilename"));
        Map<String, RequestBody> body1 = new HashMap<>();
        body1.put("docFilename", requestBody1);

        Call<FaceSDKResponse> call = faceSDKService.match(body, body1);
        call.enqueue(new Callback<FaceSDKResponse>() {
            @Override
            public void onResponse(Call<FaceSDKResponse> call, Response<FaceSDKResponse> response) {
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                if(response.isSuccessful()){
                    Toast.makeText(SecondStepActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    FaceSDKResponse data = response.body();

                    if(data.getSimilarity() >= 0.75){
                        startResultActivity(true, true, data.getSimilarity(), 2);
                    }else{
                        startResultActivity(false, true, data.getSimilarity(), 2);
                    }
                }
            }


            @Override
            public void onFailure(Call<FaceSDKResponse> call, Throwable t) {
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                Toast.makeText(SecondStepActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                startResultActivity(false, true, 0.0, 2);
            }
        });

    }

    private void matchFaces2(byte[] portrait, Bitmap file) {

        faceSDKService = APIUtils.getFaceSDKService();
        Intent intent  = getIntent();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        file.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //file.recycle();


        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), portrait);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file1", Util.getNextPortraitFilename(), requestBody);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
        MultipartBody.Part body1 = MultipartBody.Part.createFormData("file2", Util.getNextPassportFilename(), requestBody1);


        Call<FaceSDKResponse> call = faceSDKService.match2faces(body, body1);
        call.enqueue(new Callback<FaceSDKResponse>() {
            @Override
            public void onResponse(Call<FaceSDKResponse> call, Response<FaceSDKResponse> response) {
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                if(response.isSuccessful()){
                    Toast.makeText(SecondStepActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    FaceSDKResponse data = response.body();

                    if(data.getSimilarity() >= 0.75){
                        startResultActivity(true, true, data.getSimilarity(), 1);
                    }else{
                        startResultActivity(false, true, data.getSimilarity(), 1);
                    }
                }
            }
            @Override
            public void onFailure(Call<FaceSDKResponse> call, Throwable t) {
                if (initDialog.isShowing()) {
                    initDialog.dismiss();
                }
                Toast.makeText(SecondStepActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                startResultActivity(false, true, 0.0, 1);
            }
        });

    }

    private AlertDialog showDialog(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SecondStepActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.simple_dialog, null);
        dialog.setTitle(msg);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        return dialog.show();
    }

    private void startResultActivity(boolean success, boolean live, double similarity, int type){
        Intent intent = new Intent(this, FinalResultsActivity.class);
        intent.putExtra("similarity", similarity);
        intent.putExtra("live", live);
        intent.putExtra("success", success);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}