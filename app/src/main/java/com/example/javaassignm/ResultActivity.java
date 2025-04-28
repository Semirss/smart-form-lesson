package com.example.javaassignm;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ListView listView;
    private List<FormItem> formItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        listView = findViewById(R.id.listView);

        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");
        String imageString = getIntent().getStringExtra("image");

        Uri imageUri = null;
        if (imageString != null) {
            imageUri = Uri.parse(imageString);
        }

        formItemList = new ArrayList<>();
        formItemList.add(new FormItem(name, date, imageUri));

        FormAdapter adapter = new FormAdapter(this, formItemList);
        listView.setAdapter(adapter);
    }
}
