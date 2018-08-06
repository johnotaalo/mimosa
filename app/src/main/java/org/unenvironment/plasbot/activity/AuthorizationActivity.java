package org.unenvironment.plasbot.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.unenvironment.plasbot.R;

import java.util.List;

public class AuthorizationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRequestAllPermissions;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        prefs = PreferenceManager.getDefaultSharedPreferences(AuthorizationActivity.this);
        editor = prefs.edit();
        btnRequestAllPermissions = findViewById(R.id.request_all_permissions);

        getSupportActionBar().setTitle("Welcome to PlasBot");

        btnRequestAllPermissions.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.request_all_permissions:
                requestAppPermissions();
                break;
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizationActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void requestAppPermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            editor.putBoolean("authorised", true);
                            editor.commit();
                            Toast.makeText(AuthorizationActivity.this, "Thanks for providing permissions! Enjoy the app :)", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AuthorizationActivity.this, MainActivity.class));
                            finish();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()){
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestAppPermissions();
    }
}
