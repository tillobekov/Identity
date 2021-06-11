package com.example.identity;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.identity.model.FaceSDKResponse;
import com.example.identity.service.APIUtils;
import com.example.identity.service.FaceSDKService;
import com.example.identity.util.Util;
import com.regula.facesdk.Face;
import com.regula.facesdk.configuration.LivenessConfiguration;
import com.regula.facesdk.enums.eInputFaceType;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondStepActivity extends AppCompatActivity {

    ImageView imageView;
    Button buttonCamera;

    AlertDialog initDialog;

    private FaceSDKService faceSDKService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_step);

        imageView = findViewById(R.id.step2_image);
        imageView.getLayoutParams().height = 500;
        buttonCamera = findViewById(R.id.step2_button_camera);
//        buttonFinish = findViewById(R.id.step3_button_finish);
//        buttonFinish.setEnabled(false);
//
        buttonCamera.setOnClickListener(v -> startLiveness());
//
//        buttonFinish.setOnClickListener(v -> {
//            finish();
//        });

    }

    private void startLiveness() {

        LivenessConfiguration configuration = new LivenessConfiguration.Builder().setCameraSwitchEnabled(true).build();


        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        byte[] portrait = intent.getByteArrayExtra("portrait");

        //Face.Instance().setServiceUrl("http://10.1.1.12:41101/");
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

                    // textViewLiveness.setText("Liveness: passed");
                } else {
                    Toast.makeText(SecondStepActivity.this, "Liveness: unknown", Toast.LENGTH_SHORT).show();
                    startResultActivity(false, false, 0.0, type);
                }
            } else {
                Toast.makeText(SecondStepActivity.this, "Liveness: null", Toast.LENGTH_SHORT).show();
                startResultActivity(false, false, 0.0, type);
            }

            //textViewSimilarity.setText("Similarity: null");
        });

//        FaceReaderService faceReaderService = FaceReaderService.Instance();
//        faceReaderService.setServiceUrl("http://localhost:41101");
//        Toast.makeText(ThirdStepActivity.this, faceReaderService.getServiceUrl(), Toast.LENGTH_SHORT).show();
//        faceReaderService.startLivenessMatching(ThirdStepActivity.this, livenessResponse -> {
//            if (livenessResponse != null && livenessResponse.getBitmap() != null) {
//                imageView.setImageBitmap(livenessResponse.getBitmap());
//
//                if (livenessResponse.liveness == 0) {
//                    textView.setText("Liveness: passed");
//                    buttonFinish.setEnabled(true);
//                } else {
//                    textView.setText("Liveness: unknown");
//                }
//            } else {
//                textView.setText("Liveness: null");
//            }
//
//        });
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

//                    textViewMatch.setText("Match: " + data.isMatch());
//                    textViewSimilarity.setText("Similarity: " + data.getSimilarity());
//                    match = data.isMatch();
//                    buttonCheck.setEnabled(true);
//                    buttonClear.setEnabled(true);
//                    buttonNext.setEnabled(true);
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

//                    textViewMatch.setText("Match: " + data.isMatch());
//                    textViewSimilarity.setText("Similarity: " + data.getSimilarity());
//                    match = data.isMatch();
//                    buttonCheck.setEnabled(true);
//                    buttonClear.setEnabled(true);
//                    buttonNext.setEnabled(true);
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