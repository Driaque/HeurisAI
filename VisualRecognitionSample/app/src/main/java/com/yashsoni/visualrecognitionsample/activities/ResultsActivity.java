package com.yashsoni.visualrecognitionsample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yashsoni.visualrecognitionsample.R;
import com.yashsoni.visualrecognitionsample.adapters.VisualRecognitionResultAdapter;
import com.yashsoni.visualrecognitionsample.models.VisualRecognitionResponseModel;

import java.io.File;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    String url = "";
    File file;
    ArrayList<VisualRecognitionResponseModel> classes = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //TextView responseTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("url");
            classes = getIntent().getExtras().getParcelableArrayList("classes");
        }

        initializeViews();
    }

    private void initializeViews() {
        TextView responseTextView = (TextView) findViewById(R.id.textView3);
        if(!classes.isEmpty()){//check if the list contains the element
            //classes.get(classes.indexOf(1)).getClassName();//get the element by passing the index of the element

            String response =  classes.get(0).getClassName().toString();
            responseTextView.setText("Object identified : "+response);
        }
        if(classes.isEmpty()){
            String response =  "I'm having an issue identifying this image please try taking it from a different angle ;)";
            responseTextView.setText(response);
        }

        ImageView imageView = findViewById(R.id.image);
        if(url != null) {
            Glide.with(this).load(url).into(imageView);
        }if(file != null) {
            Glide.with(this).load(file).into(imageView);
        }
        mRecyclerView = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new VisualRecognitionResultAdapter(classes);
        mRecyclerView.setAdapter(mAdapter);
    }
}
