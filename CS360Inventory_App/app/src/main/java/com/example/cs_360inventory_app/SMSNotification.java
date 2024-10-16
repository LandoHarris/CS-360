package com.example.cs_360inventory_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SMSNotification extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 1;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch smsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_notification);

        smsSwitch = findViewById(R.id.SMS_setting);
        Button backButton = findViewById(R.id.back_button);

        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestSmsPermissions();
            } else {
                guideToAppSettings();
                smsSwitch.setChecked(false);
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SMSNotification.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: call finish() if you don't want to keep SMSNotification in the back stack
        });

        // Initialize switch state
        updateSwitchState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Continuously update switch state
        updateSwitchState();
    }

    private void updateSwitchState() {
        smsSwitch.setChecked(isSmsPermissionGranted());
    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermissions() {
        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, REQUEST_SMS_PERMISSION);
        } else {
            Toast.makeText(this, "SMS Permission Already Granted", Toast.LENGTH_SHORT).show();
            smsSwitch.setChecked(true);
        }
    }

    private void guideToAppSettings() {
        Toast.makeText(this, "Please revoke SMS permissions manually from app settings.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
                smsSwitch.setChecked(true);
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
                smsSwitch.setChecked(false);
            }
        }
    }

}
