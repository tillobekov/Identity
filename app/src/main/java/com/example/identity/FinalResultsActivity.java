package com.example.identity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FinalResultsActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textViewStatus;
    TextView textViewSimilarity;
    TextView textViewPass;
    LinearLayout similarityLayout;
    FrameLayout retryLayout;
    FrameLayout finishLayout;
    Button buttonFinish;
    Button buttonRetry;

    boolean success;
    boolean live;
    double similarity;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_results);

        Intent intent = getIntent();

        imageView = findViewById(R.id.results_image);
        textViewStatus = findViewById(R.id.results_text_status);
        textViewSimilarity = findViewById(R.id.results_text_similarity);
        textViewPass = findViewById(R.id.results_text_pass);
        similarityLayout = findViewById(R.id.results_text_similarity_layout);
        retryLayout = findViewById(R.id.results_layout_retry);
        finishLayout = findViewById(R.id.results_layout_finish);
        buttonFinish = findViewById(R.id.results_button_finish);
        buttonRetry = findViewById(R.id.results_button_retry);

        success = intent.getBooleanExtra("success", false);
        live = intent.getBooleanExtra("live", false);
        similarity = intent.getDoubleExtra("similarity", 0.0) * 100;
        type = intent.getIntExtra("type", 0);


        if(success){
            textViewSimilarity.setText(similarity + "");
            retryLayout.setVisibility(View.GONE);
            finishLayout.setVisibility(View.VISIBLE);
        }else{
            imageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/icon_fail", null, getPackageName())));
            textViewStatus.setText("Проверка провалена :(");
            retryLayout.setVisibility(View.VISIBLE);
            finishLayout.setVisibility(View.GONE);
            if(live){
                textViewSimilarity.setText(similarity + "");
                textViewSimilarity.setBackgroundColor(Color.RED);
                textViewPass.setVisibility(View.GONE);
            }else{
                textViewPass.setText("Тест на Liveness не прошел");
                similarityLayout.setVisibility(View.GONE);
            }

        }


        buttonFinish.setOnClickListener(v -> {
            Intent intent1;
            if(type == 1){
                intent1 = new Intent(getApplicationContext(), MainActivity.class);
            }else{
                intent1 = new Intent(getApplicationContext(), FirstStepActivity.class);
            }
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent1.putExtra("EXIT", true);
            startActivity(intent1);
            finish();
        });

        buttonRetry.setOnClickListener(v -> {
            finish();
        });




        //Toast.makeText(FinalResultsActivity.this, intent.getBooleanExtra("success", false) + "", Toast.LENGTH_SHORT).show();
    }
}