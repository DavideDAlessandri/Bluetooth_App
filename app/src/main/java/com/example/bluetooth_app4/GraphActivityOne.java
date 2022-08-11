package com.example.bluetooth_app4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class GraphActivityOne extends AppCompatActivity implements GraphListener {

    TextView receivedValue;
    private BLEController bleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_graph_1);
        receivedValue = findViewById(R.id.receivedValue);

    }

    @Override
    public void DisplayGraph(String message){
        receivedValue.setText(message);
    }
}
