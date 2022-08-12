package com.example.bluetooth_app4;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BLEControllerListener {
    private TextView logView;
    private Button connectButton;
    private Button disconnectButton;
    private Button switchLEDButton;
    private Button pageChanger;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ImageView eyeR, eyeRs, eyeL, eyeLs;
    private Button eyeView;
    Boolean notEyeBool = true;

    private BLEController bleController;
    private RemoteControl remoteControl;
    private String deviceAddress;

    private boolean isLEDOn = false;

    private boolean isAlive = false;
    private Thread heartBeatThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView2);
        eyeR = findViewById(R.id.eyeR);
        eyeRs = findViewById(R.id.eyeRs);
        eyeL = findViewById(R.id.eyeL);
        eyeLs = findViewById(R.id.eyeLs);
        eyeView = findViewById(R.id.eyeView);

        eyeR.setVisibility(View.GONE);
        eyeRs.setVisibility(View.GONE);
        eyeL.setVisibility(View.GONE);
        eyeLs.setVisibility(View.GONE);

        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                switchLEDButton.setVisibility(View.VISIBLE);
                pageChanger.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                eyeView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                notEyeBool = true;
            }
        });

        eyeR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                switchLEDButton.setVisibility(View.VISIBLE);
                pageChanger.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                eyeView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                eyeR.setVisibility(View.GONE);
                eyeL.setVisibility(View.GONE);
                eyeRs.setVisibility(View.GONE);
                eyeLs.setVisibility(View.GONE);
                notEyeBool = true;
            }
        });

        eyeL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.VISIBLE);
                switchLEDButton.setVisibility(View.VISIBLE);
                pageChanger.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                eyeView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                eyeR.setVisibility(View.GONE);
                eyeL.setVisibility(View.GONE);
                eyeRs.setVisibility(View.GONE);
                eyeLs.setVisibility(View.GONE);
                notEyeBool = true;
            }
        });

        eyeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setVisibility(View.GONE);
                disconnectButton.setVisibility(View.GONE);
                switchLEDButton.setVisibility(View.GONE);
                pageChanger.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                eyeView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                eyeR.setVisibility(View.VISIBLE);
                eyeL.setVisibility(View.VISIBLE);
                notEyeBool = false;
            }
        });


        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        this.logView = findViewById(R.id.logView);
        this.logView.setMovementMethod(new ScrollingMovementMethod());

        initConnectButton();
        initDisconnectButton();
        initSwitchButton();
        initPageChangerButton();

        checkBLESupport();
        checkPermissions();

        disableButtons();
    }

    public void startHeartBeat() {
        this.isAlive = true;
        this.heartBeatThread = createHeartBeatThread();
        this.heartBeatThread.start();
    }

    public void stopHeartBeat() {
        if (this.isAlive) {
            this.isAlive = false;
            this.heartBeatThread.interrupt();
        }
    }

    private Thread createHeartBeatThread() {
        return new Thread() {
            @Override
            public void run() {
                while (MainActivity.this.isAlive) {
                    heartBeat();
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException ie) {
                        return;
                    }
                }
            }
        };
    }

    private void heartBeat() {
        this.remoteControl.heartbeat();
    }

    private void initConnectButton() {
        this.connectButton = findViewById(R.id.connectButton);
        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectButton.setEnabled(false);
                log("Connecting...");
                bleController.connectToDevice(deviceAddress);
            }
        });
    }

    private void initDisconnectButton() {
        this.disconnectButton = findViewById(R.id.disconnectButton);
        this.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectButton.setEnabled(false);
                log("Disconnecting...");
                bleController.disconnect();
            }
        });
    }

    private void initSwitchButton() {
        this.switchLEDButton = findViewById(R.id.switchButton);
        this.switchLEDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isLEDOn = !isLEDOn;
                remoteControl.switchButton(); //isLEDOn
                //log("LED switched " + (isLEDOn ? "On" : "Off"));
                pageChanger.setEnabled(true);
                eyeView.setEnabled(true);
            }
        });
    }

    private void initPageChangerButton(){
        this.pageChanger =findViewById(R.id.changePage);
        this.pageChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changeActivityTwo();
                connectButton.setVisibility(View.GONE);
                disconnectButton.setVisibility(View.GONE);
                switchLEDButton.setVisibility(View.GONE);
                pageChanger.setVisibility(View.GONE);
                eyeView.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
            }
        });
    }

    private void disableButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(false);
                switchLEDButton.setEnabled(false);
                pageChanger.setEnabled(false);
                eyeView.setEnabled(false);
            }
        });
    }

    private void changeActivityTwo(){                                                               //Change activity

        Intent intent = new Intent(this,GraphActivityOne.class);
        startActivity(intent);
    }

    private void log(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.setText(logView.getText() + "\n" + text);
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            log("\"Access Fine Location\" permission not granted yet!");
            log("Whitout this permission Blutooth devices cannot be searched!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

    private void checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);


            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Location permission has not been granted.
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 2);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            startActivityForResult(enableBTIntent, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.deviceAddress = null;
        this.bleController = BLEController.getInstance(this);
        this.bleController.addBLEControllerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            log("[BLE]\tSearching device...");
            this.bleController.init();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.bleController.removeBLEControllerListener(this);
        stopHeartBeat();
    }

    @Override
    public void BLEControllerConnected() {
        log("[BLE]\tConnected");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnectButton.setEnabled(true);
                switchLEDButton.setEnabled(true);
            }
        });
        //startHeartBeat();
    }

    @Override
    public void BLEControllerDisconnected() {
        log("[BLE]\tDisconnected");
        disableButtons();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(true);
            }
        });
        this.isLEDOn = false;
        stopHeartBeat();
    }

    @Override
    public void BLEDeviceFound(String name, String address) {
        log("Device " + name + " found with address " + address);
        this.deviceAddress = address;
        this.connectButton.setEnabled(true);
    }

    @Override
    public void MessageReceived(String message){

        final String messageCorrected = message.replaceAll("[\\(\\)\\[\\]\\{\\}]","");

        //log("Message received " + messageCorrected);

        runOnUiThread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                int number = Integer.parseInt(messageCorrected);
                number = 100 - number;

                if(notEyeBool){
                    progressBar.setProgress(number);
                    if(number>=66){
                        progressBar.setProgressDrawable(getDrawable(R.drawable.custom_progress_bg_red));
                    }else if(number>=33){
                        progressBar.setProgressDrawable(getDrawable(R.drawable.custom_progress_bg_yellow));
                    }else if(number>=0){
                        progressBar.setProgressDrawable(getDrawable(R.drawable.custom_progress_bg_green));
                    }
                }else{
                    if(number < 90){
                        eyeL.setVisibility(View.VISIBLE);
                        eyeR.setVisibility(View.VISIBLE);
                        eyeLs.setVisibility(View.GONE);
                        eyeRs.setVisibility(View.GONE);
                    }else{
                        eyeL.setVisibility(View.GONE);
                        eyeR.setVisibility(View.GONE);
                        eyeLs.setVisibility(View.VISIBLE);
                        eyeRs.setVisibility(View.VISIBLE);
                    }
                }

            }
        });



    }
}