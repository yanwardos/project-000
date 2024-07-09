package com.virtualfittingroom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.virtualfittingroom.mlkitpose.components.CameraSource;
import com.virtualfittingroom.mlkitpose.components.CameraSourcePreview;
import com.virtualfittingroom.mlkitpose.components.GraphicOverlay;
import com.virtualfittingroom.mlkitpose.components.posedetector.Processor;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.io.IOException;

public class FittingRoomActivityMLKit extends AppCompatActivity {
    public static final String TAG = FittingRoomActivityMLKit.class.getSimpleName();

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    public static final int PERMISSION_REQ = 1;
    private boolean isRequestingPermission = false;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_pose_detection);

        preview = findViewById(R.id.preview_view);
        if(preview == null){
            Log.d(TAG, "onCreate: Preview is null");
        }

        graphicOverlay = findViewById(R.id.graphic_overlay);

        ToggleButton facingSwitch = findViewById(R.id.switch_facing);
        facingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cameraSource != null){
                    if(isChecked){
                        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
                    }else{
                        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
                    }
                }
                preview.stop();
                startCameraSource();
            }
        });

        if(!checkPermission()){
            if(!isRequestingPermission)
                requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQ);
        }
    }

    private boolean checkPermission(){
        boolean result = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            result &=
                    (ContextCompat.checkSelfPermission(this, permission)
                            == PackageManager.PERMISSION_GRANTED);

            }
        return result;
    }

    private void createCameraSource(){
        if(cameraSource == null){
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            AccuratePoseDetectorOptions.Builder optionBuilder =
                    new AccuratePoseDetectorOptions.Builder();
            optionBuilder
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU);
            cameraSource.setMachineLearningFrameProcessor(
                    new Processor(
                            this,
                            optionBuilder.build(),
                            false,
                            true,
                            true,
                            true
                    )
            );
        } catch (RuntimeException e){
            Log.e(TAG, "createCameraSource: Cannot create image processor", e);
            Toast.makeText(
                    this,
                    "Cannot create image processor" + e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void startCameraSource(){
        if(cameraSource != null){
            try {
                if(preview == null){
                    Log.d(TAG, "startCameraSource: resume: Preview is null");
                }
                if(graphicOverlay == null){
                    Log.d(TAG, "startCameraSource: resume: graphoverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e){
                Log.e(TAG, "startCameraSource: Unable to start camera source", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if(!checkPermission()){
            requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQ);
        }
        
        createCameraSource();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraSource != null){
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if(requestCode == PERMISSION_REQ){
            if(permissions.length==0){
                Toast.makeText(this, "Izin kamera tidak diberikan", Toast.LENGTH_SHORT).show();
                finish();
            }
            createCameraSource();
            startCameraSource();
        }
    }
}
