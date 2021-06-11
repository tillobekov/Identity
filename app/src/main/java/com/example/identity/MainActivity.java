package com.example.identity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.identity.model.DocumentReaderResponse;
import com.example.identity.model.NIBBDRequest;
import com.example.identity.service.APIUtils;
import com.example.identity.service.DocumentReaderService;
import com.example.identity.util.Util;
import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderInitCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderPrepareCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.enums.Scenario;
import com.regula.documentreader.api.enums.eGraphicFieldType;
import com.regula.documentreader.api.enums.eRFID_Password_Type;
import com.regula.documentreader.api.enums.eVisualFieldType;
import com.regula.documentreader.api.errors.DocumentReaderException;
import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.documentreader.api.results.DocumentReaderScenario;
import com.regula.documentreader.api.results.DocumentReaderTextField;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BROWSE_PICTURE = 11;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 22;
    private static final String MY_SHARED_PREFS = "MySharedPrefs";
    private static final String DO_RFID = "doRfid";

    private Button showScanner;

    private ImageView docImageIv;

    private CheckBox doRfidCb;

    private SharedPreferences sharedPreferences;
    private boolean doRfid;
    private AlertDialog loadingDialog;

    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showScanner = findViewById(R.id.step1_button_camera);

        docImageIv = findViewById(R.id.step1_image);

        doRfidCb = findViewById(R.id.doRfidCb);

        sharedPreferences = getSharedPreferences(MY_SHARED_PREFS, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!DocumentReader.Instance().getDocumentReaderIsReady()) {
            final AlertDialog initDialog = showDialog("Initializing");

            //Reading the license from raw resource file
            try {
                InputStream licInput = getResources().openRawResource(R.raw.regula);
                int available = licInput.available();
                final byte[] license = new byte[available];
                //noinspection ResultOfMethodCallIgnored
                licInput.read(license);

                //preparing database files, it will be downloaded from network only one time and stored on user device
                DocumentReader.Instance().prepareDatabase(MainActivity.this, "Full", new IDocumentReaderPrepareCompletion() {
                    @Override
                    public void onPrepareProgressChanged(int progress) {
                        initDialog.setTitle("Downloading database: " + progress + "%");
                    }

                    @Override
                    public void onPrepareCompleted(boolean status, DocumentReaderException error) {

                        //Initializing the reader
                        DocumentReader.Instance().initializeReader(MainActivity.this, license, new IDocumentReaderInitCompletion() {
                            @Override
                            public void onInitCompleted(boolean success, DocumentReaderException error) {
                                if (initDialog.isShowing()) {
                                    initDialog.dismiss();
                                }

                                DocumentReader.Instance().customization().edit().setShowHelpAnimation(false).apply();

                                //initialization successful
                                if (success) {
                                    showScanner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            clearResults();

                                            //starting video processing
                                            DocumentReader.Instance().processParams().dateFormat = "dd-mm-yyyy";
                                            DocumentReader.Instance().showScanner(MainActivity.this, completion);
                                        }
                                    });


                                    if (DocumentReader.Instance().isRFIDAvailableForUse()) {
                                        //reading shared preferences
                                        doRfid = sharedPreferences.getBoolean(DO_RFID, false);
                                        doRfidCb.setChecked(doRfid);
                                        doRfidCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                                                doRfid = checked;
                                                sharedPreferences.edit().putBoolean(DO_RFID, checked).apply();
                                            }
                                        });
                                    } else {
                                        doRfidCb.setVisibility(View.GONE);
                                    }

                                    //getting current processing scenario and loading available scenarios to ListView
//                                    String currentScenario = DocumentReader.Instance().processParams().scenario;
//                                    ArrayList<String> scenarios = new ArrayList<>();
//                                    for (DocumentReaderScenario scenario : DocumentReader.Instance().availableScenarios) {
//                                        scenarios.add(scenario.name);
//                                    }
//
//                                    //setting default scenario
//                                    if (currentScenario == null || currentScenario.isEmpty()) {
//                                        currentScenario = scenarios.get(0);
//                                        DocumentReader.Instance().processParams().scenario = currentScenario;
//                                    }

                                    DocumentReader.Instance().processParams().scenario = Scenario.SCENARIO_FULL_PROCESS;

//                                    final ScenarioAdapter adapter = new ScenarioAdapter(MainActivity.this, android.R.layout.simple_list_item_1, scenarios);
//                                    selectedPosition = 0;
//                                    try {
//                                        selectedPosition = adapter.getPosition(currentScenario);
//                                    } catch (Exception ex) {
//                                        ex.printStackTrace();
//                                    }
//                                    scenarioLv.setAdapter(adapter);
//
//                                    scenarioLv.setSelection(selectedPosition);
//
//                                    scenarioLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                            //setting selected scenario to DocumentReader params
//                                            DocumentReader.Instance().processParams().scenario = adapter.getItem(i);
//                                            selectedPosition = i;
//                                            adapter.notifyDataSetChanged();
//
//                                        }
//                                    });

                                }
                                //Initialization was not successful
                                else {
                                    Toast.makeText(MainActivity.this, "Init failed:" + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                licInput.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    @Override
    protected void onPause() {
        super.onPause();

        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //Image browsing intent processed successfully
            if (requestCode == REQUEST_BROWSE_PICTURE){
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Bitmap bmp = getBitmap(selectedImage, 1920, 1080);

                    loadingDialog = showDialog("Processing image");

                    DocumentReader.Instance().recognizeImage(bmp, completion);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //access to gallery is allowed
                    createImageBrowsingRequest();
                } else {
                    Toast.makeText(MainActivity.this, "Permission required, to browse images",Toast.LENGTH_LONG).show();
                }
            } break;
        }
    }

    //DocumentReader processing callback
    private IDocumentReaderCompletion completion = new IDocumentReaderCompletion() {
        @Override
        public void onCompleted(int action, DocumentReaderResults results, DocumentReaderException error) {
            //processing is finished, all results are ready
            if (action == DocReaderAction.COMPLETE) {
                if(loadingDialog!=null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                //Checking, if nfc chip reading should be performed
                if (doRfid && results!=null && results.chipPage != 0) {
                    //setting the chip's access key - mrz on car access number
                    String accessKey = null;
                    if ((accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)) != null && !accessKey.isEmpty()) {
                        accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                                .replace("^", "").replace("\n","");
                        DocumentReader.Instance().rfidScenario().setMrz(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_MRZ);
                    } else if ((accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_CARD_ACCESS_NUMBER)) != null && !accessKey.isEmpty()) {
                        DocumentReader.Instance().rfidScenario().setPassword(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_CAN);
                    }

                    //starting chip reading
                    DocumentReader.Instance().startRFIDReader(MainActivity.this, new IDocumentReaderCompletion() {
                        @Override
                        public void onCompleted(int rfidAction, DocumentReaderResults results, DocumentReaderException error) {
                            if (rfidAction == DocReaderAction.COMPLETE || rfidAction == DocReaderAction.CANCEL) {
                                displayResults(results);
                            }
                        }
                    });
                } else {
                    displayResults(results);
                }
            } else {
                //something happened before all results were ready
                if(action==DocReaderAction.CANCEL){
                    Toast.makeText(MainActivity.this, "Scanning was cancelled",Toast.LENGTH_LONG).show();
                } else if(action == DocReaderAction.ERROR){
                    Toast.makeText(MainActivity.this, "Error:" + error, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private AlertDialog showDialog(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.simple_dialog, null);
        dialog.setTitle(msg);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        return dialog.show();
    }

    //show received results on the UI
    private void displayResults(DocumentReaderResults results){
        if(results!=null) {
            String docNumber = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER);
            String birthDate = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH);
            if (docNumber != null && birthDate != null){
                final AlertDialog initDialog = showDialog("Служба NIBBD");

                DocumentReaderService documentReaderService = APIUtils.getDocumentReaderService();

                NIBBDRequest body = new NIBBDRequest(docNumber, birthDate);

                Call<DocumentReaderResponse> call = documentReaderService.nibbd(body);
                call.enqueue(new Callback<DocumentReaderResponse>() {
                    @Override
                    public void onResponse(Call<DocumentReaderResponse> call, Response<DocumentReaderResponse> response) {
                        if(response.isSuccessful()){
                            if (initDialog.isShowing()) {
                                initDialog.dismiss();
                            }

                            DocumentReaderResponse data = response.body();

                            Bitmap portrait = results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT);
                            if(portrait!=null){
                                startResultsActivity(portrait, data);
                            }else{
                                Toast.makeText(MainActivity.this, "Portrait image is not found", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Cannot parse the text", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DocumentReaderResponse> call, Throwable t) {
                        if (initDialog.isShowing()) {
                            initDialog.dismiss();
                        }
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//                Toast.makeText(MainActivity.this, docNumber + "   " + birthDate, Toast.LENGTH_LONG).show();
            }

            // through all text fields
            if(results.textResult != null && results.textResult.fields != null) {
                for (DocumentReaderTextField textField : results.textResult.fields) {
                    String value = results.getTextFieldValueByType(textField.fieldType, textField.lcid);
                    Log.d("MainActivity", value + "\n");
                }
            }

            Bitmap documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE);
            if(documentImage!=null){
                double aspectRatio = (double) documentImage.getWidth() / (double) documentImage.getHeight();
                documentImage = Bitmap.createScaledBitmap(documentImage, (int)(480 * aspectRatio), 480, false);
                docImageIv.setImageBitmap(documentImage);
            }
        }
    }

    private void startResultsActivity(Bitmap portrait, DocumentReaderResponse data){
        Intent intent = new Intent(this, FirstStepResultsActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("data", data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        portrait.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("portrait", byteArray);

        startActivity(intent);
    }

    private String formatDateOfBirth(String date){
        if(date == null){
            return null;
        }
        String[] dates = date.split("/");
        return dates[1] + "/" + dates[0] + "/" + "19" + dates[2];
    }


    private void clearResults(){
        docImageIv.setImageResource(R.drawable.icon_step1_photo);
    }

    // creates and starts image browsing intent
    // results will be handled in onActivityResult method
    private void createImageBrowsingRequest() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_BROWSE_PICTURE);
    }

    // loads bitmap from uri
    private Bitmap getBitmap(Uri selectedImage, int targetWidth, int targetHeight) {
        ContentResolver resolver = MainActivity.this.getContentResolver();
        InputStream is = null;
        try {
            is = resolver.openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        //Re-reading the input stream to move it's pointer to start
        try {
            is = resolver.openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return decodeStream(is, null, options);
    }

    // see https://developer.android.com/topic/performance/graphics/load-bitmap.html
    private int calculateInSampleSize(BitmapFactory.Options options, int bitmapWidth, int bitmapHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > bitmapHeight || width > bitmapWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > bitmapHeight
                    && (halfWidth / inSampleSize) > bitmapWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    class ScenarioAdapter extends ArrayAdapter<String> {

        public ScenarioAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if(position == selectedPosition){
                view.setBackgroundColor(Color.LTGRAY);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }
}