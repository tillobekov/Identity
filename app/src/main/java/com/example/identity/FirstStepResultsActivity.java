package com.example.identity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.identity.model.DocumentReaderResponse;


public class FirstStepResultsActivity extends AppCompatActivity {

//    private static final int REQUEST_IMAGE_CAPTURE = 2;
//    private Bitmap imageBitmap;
//    private boolean match = false;
//
//    private FaceSDKService faceSDKService;
//
//    ImageView imageView;
//    TextView textViewMatch;
//    TextView textViewSimilarity;
//    Button buttonCheck;
//    Button buttonClear;
//    Button buttonNext;

    private boolean validity = false;

    Button buttonBack;
    Button buttonNext;

    ImageView imageView;

    TextView textViewDocType;
//    TextView textViewValidity;
    TextView textViewID;
    TextView textViewIssue;
    TextView textViewDue;
//    TextView textViewMonth;
    TextView textViewOrganization;
    TextView textViewName;
    TextView textViewSurname;
    TextView textViewFatherName;
    TextView textViewCountry;
    TextView textViewCountryCode;
    TextView textViewNationality;
    TextView textViewNationalityCode;
//    TextView textViewAge;
    TextView textViewSex;
    TextView textViewBirthDate;
    TextView textViewBirthPlace;
    TextView textViewInn;
    TextView textViewPin;
    TextView textViewAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_step_results);

        buttonBack = findViewById(R.id.step1_button_results_back);
        buttonNext = findViewById(R.id.step1_button_results_next);

        imageView = findViewById(R.id.step1_image_results);

        textViewDocType = findViewById(R.id.step1_text_results_doctype);
//        textViewValidity = findViewById(R.id.step1_text_results_validity);
        textViewID = findViewById(R.id.step1_text_results_docid);
        textViewIssue = findViewById(R.id.step1_text_results_issuedate);
        textViewDue = findViewById(R.id.step1_text_results_duedate);
//        textViewMonth = findViewById(R.id.step1_text_results_months);
        textViewOrganization = findViewById(R.id.step1_text_results_organization);
        textViewName = findViewById(R.id.step1_text_results_name);
        textViewSurname = findViewById(R.id.step1_text_results_surname);
        textViewFatherName = findViewById(R.id.step1_text_results_fathername);
        textViewCountry = findViewById(R.id.step1_text_results_country);
        textViewCountryCode = findViewById(R.id.step1_text_results_countrycode);
        textViewNationality = findViewById(R.id.step1_text_results_nationality);
        textViewNationalityCode = findViewById(R.id.step1_text_results_nationalitycode);
//        textViewAge = findViewById(R.id.step1_text_results_age);
        textViewSex = findViewById(R.id.step1_text_results_sex);
        textViewBirthDate = findViewById(R.id.step1_text_results_birthdate);
        textViewBirthPlace = findViewById(R.id.step1_text_results_birthplace);
        textViewInn = findViewById(R.id.step1_text_results_inn);
        textViewPin = findViewById(R.id.step1_text_results_pin);
        textViewAddress = findViewById(R.id.step1_text_results_address);

        Intent intent = getIntent();

        int type = intent.getIntExtra("type", 0);
        DocumentReaderResponse data = (DocumentReaderResponse) intent.getSerializableExtra("data");

        byte[] portraitBytes = getIntent().getByteArrayExtra("portrait");

        if(type == 1){
            Bitmap portrait = BitmapFactory.decodeByteArray(portraitBytes, 0, portraitBytes.length);
            imageView.setImageBitmap(portrait);
        }


        //Toast.makeText(FirstStepResultsActivity.this, data.toString(), Toast.LENGTH_SHORT).show();

        validity = data.getPassportData().isValid();

        textViewID.setText(data.getPassportData().getDocumentNumber());
        textViewIssue.setText(data.getPassportData().getDateOfIssue());
        textViewDue.setText(data.getPassportData().getDateOfExpire());
//        textViewMonth.setText(data.getPassportData().getMonthsToExpire());
        textViewOrganization.setText(data.getPassportData().getAuthority());
        textViewName.setText(data.getPassportData().getGivenName());
        textViewSurname.setText(data.getPassportData().getSurName());
        textViewFatherName.setText(data.getPassportData().getFathersName());
        textViewCountry.setText(data.getPassportData().getIssuingState());
        textViewCountryCode.setText(data.getPassportData().getIssuingStateCode());
        textViewNationality.setText(data.getPassportData().getNationality());
        textViewNationalityCode.setText(data.getPassportData().getNationalityCode());
//        textViewAge.setText(data.getPassportData().getAge());
        textViewSex.setText(data.getPassportData().getSex());
        textViewBirthDate.setText(data.getPassportData().getDateOfBirth());
        textViewBirthPlace.setText(data.getPassportData().getPlaceOfBirth());
        textViewInn.setText(data.getPassportData().getInn());
        textViewPin.setText(data.getPassportData().getPin());
        textViewAddress.setText(data.getPassportData().getAddress());

//        if(validity){
//            textViewValidity.setText("True");
//            textViewValidity.setTextColor(Color.BLUE);
//        }else{
//            textViewValidity.setText("False");
//            textViewValidity.setTextColor(Color.RED);
//            buttonNext.setEnabled(false);
//            buttonNext.setVisibility(View.INVISIBLE);
//        }

        if(!validity){
            buttonNext.setEnabled(false);
            buttonNext.setVisibility(View.GONE);
        }

        buttonBack.setOnClickListener(v -> {
            finish();
        });

        if(type == 1){
            buttonNext.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, SecondStepActivity.class);
                intent1.putExtra("portrait", portraitBytes);
                intent1.putExtra("type", 1);
                startActivity(intent1);
            });
        }else{
            buttonNext.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, SecondStepActivity.class);
                intent1.putExtra("docFilename", data.getFilename());
                intent1.putExtra("type", 2);
                startActivity(intent1);
            });
        }




//        imageView = findViewById(R.id.step2_image);
//        imageView.getLayoutParams().height = 400;
//
//        textViewMatch = findViewById(R.id.step2_text_match);
//        textViewSimilarity = findViewById(R.id.step2_text_similarity);
//
//        buttonCheck = findViewById(R.id.step2_button_check);
//        buttonClear = findViewById(R.id.step2_button_clear);
//        buttonNext = findViewById(R.id.step2_button_next);
//        buttonNext.setEnabled(false);
//
//        imageView.setOnClickListener(v -> {
//            startFaceCaptureActivity(imageView);
//        });
//
//        buttonCheck.setOnClickListener(v -> {
//            if (imageView.getDrawable() != null) {
//                textViewMatch.setText("Processing...");
//
//                matchFaces(getImageBitmap(imageView));
//                buttonClear.setEnabled(false);
//                buttonNext.setEnabled(false);
//                buttonCheck.setEnabled(false);
//            } else {
//                Toast.makeText(FirstStepResultsActivity.this, "Having the document image compulsory", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        buttonClear.setOnClickListener(v -> {
//            imageView.setImageDrawable(null);
//            textViewMatch.setText("Match: null");
//            textViewSimilarity.setText("Similarity: null");
//
//            buttonNext.setEnabled(false);
//            match = false;
//        });
//
//        buttonNext.setOnClickListener(v -> {
//            if(match){
//                Intent intent = new Intent(this, ThirdStepActivity.class);
//                //intent.putExtra("docFilename", docFilename);
//                startActivity(intent);
//                finish();
//            }
//        });

    }

//    private void startFaceCaptureActivity(ImageView imageView) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        } catch (ActivityNotFoundException e) {
//            // display error state to the user
//        }
//    }
//
//    private Bitmap getImageBitmap(ImageView imageView) {
//        imageView.invalidate();
//        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//
//        return bitmap;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//            imageView.setTag(eInputFaceType.ift_DocumentPrinted);
//            textViewMatch.setText("Match: null");
//            textViewSimilarity.setText("Similarity: null");
//        }
//    }
//
//    private void matchFaces(Bitmap file) {
//
//        faceSDKService = APIUtils.getFaceSDKService();
//        Intent intent  = getIntent();
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        file.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        //file.recycle();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", Util.getNextPassportFilename(), requestBody);
//
//        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), intent.getStringExtra("docFilename"));
//        Map<String, RequestBody> body1 = new HashMap<>();
//        body1.put("docFilename", requestBody1);
//
//        Call<FaceSDKResponse> call = faceSDKService.match(body, body1);
//        call.enqueue(new Callback<FaceSDKResponse>() {
//            @Override
//            public void onResponse(Call<FaceSDKResponse> call, Response<FaceSDKResponse> response) {
//                if(response.isSuccessful()){
//                    Toast.makeText(FirstStepResultsActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                    FaceSDKResponse data = response.body();
//                    textViewMatch.setText("Match: " + data.isMatch());
//                    textViewSimilarity.setText("Similarity: " + data.getSimilarity());
//                    match = data.isMatch();
//                    buttonCheck.setEnabled(true);
//                    buttonClear.setEnabled(true);
//                    buttonNext.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FaceSDKResponse> call, Throwable t) {
//                Toast.makeText(FirstStepResultsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
}