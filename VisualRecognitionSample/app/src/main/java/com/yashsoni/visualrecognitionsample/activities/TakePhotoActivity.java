package com.yashsoni.visualrecognitionsample.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import com.yashsoni.visualrecognitionsample.R;
import com.yashsoni.visualrecognitionsample.models.VisualRecognitionResponseModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TakePhotoActivity extends AppCompatActivity {

    private final String API_KEY = "INSERT KEY HERE";
    Single<ClassifiedImages> observable;
    private float threshold = (float) 0.6;
    Button btnFetchResults;
    ProgressBar progressBar;
    View content;
    File file;
    Uri uri;
    private Context context;
    private Activity activity;
    private Button btnCapture;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = TakePhotoActivity.this;

        setContentView(R.layout.activity_take_photo);
        initializeViews();
        btnCapture =(Button)findViewById(R.id.btnTakePicture);
        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                uri = FileProvider.getUriForFile(activity,  ".provider", file);
                captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureIntent,Image_Capture_Code);
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
               // Bitmap bp = (Bitmap) data.getExtras().get("data");
                //imgCapture.setImageBitmap(bp);
                String imagefile = file.toURI().toString();
                imgCapture.setImageURI(android.net.Uri.parse(imagefile));

                // IBM WATSON CALL
                observable = Single.create((SingleOnSubscribe<ClassifiedImages>) emitter -> {
                    IamOptions options = new IamOptions.Builder()
                            .apiKey(API_KEY)
                            .build();

                    VisualRecognition visualRecognition = new VisualRecognition("2018-03-19", options);
                    ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                            .imagesFile(file)
                            .classifierIds(Collections.singletonList("default"))
                            .threshold(threshold)
                            .owners(Collections.singletonList("me"))
                            .build();
                    ClassifiedImages classifiedImages = visualRecognition.classify(classifyOptions).execute();
                    emitter.onSuccess(classifiedImages);

                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void initializeViews() {
        //etUrl = findViewById(R.id.et_url);
        btnFetchResults = findViewById(R.id.btn_fetch_results);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        content = findViewById(R.id.ll_content);

        btnFetchResults.setOnClickListener(v -> {
                content.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                observable.subscribe(new SingleObserver<ClassifiedImages>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ClassifiedImages classifiedImages) {
                        System.out.println(classifiedImages.toString());
                        List<ClassResult> resultList = classifiedImages.getImages().get(0).getClassifiers().get(0).getClasses();
                        goToNext(file, resultList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }
                });

        });
    }

    private void goToNext(File file , List<ClassResult> resultList) {
        progressBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        // Checking if image has a class named "explicit". If yes, then reject and show an error msg as a Toast
        for (ClassResult result : resultList) {
            if(result.getClassName().equals("explicit")) {
                Toast.makeText(this, "NOT ALLOWED TO UPLOAD EXPLICIT CONTENT", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Toast.makeText(this, "IMAGE DOESN'T CONTAIN ANY EXPLICIT CONTENT. OK TO PROCEED!", Toast.LENGTH_LONG).show();

        // No Explicit content found, go ahead with processing results and moving to Results Activity
        ArrayList<VisualRecognitionResponseModel> classes = new ArrayList<>();
        for (ClassResult result : resultList) {
            VisualRecognitionResponseModel model = new VisualRecognitionResponseModel();
            model.setClassName(result.getClassName());
            model.setScore(result.getScore());
            classes.add(model);
        }
        Intent i = new Intent(TakePhotoActivity.this, ResultsActivity.class);
        i.putParcelableArrayListExtra("classes", classes);
        startActivity(i);
    }
}
