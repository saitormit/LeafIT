package com.example.leafit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MoistureActivity extends AppCompatActivity {

    private Button btnWater;
    private TextView emptyPlotTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);

        initViews();
    }

    private void initViews() {
        btnWater = findViewById(R.id.waterBtn);
        emptyPlotTxt = findViewById(R.id.emptyPlotTxt);
    }
}