package com.virtualfittingroom;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.virtualfittingroom.databinding.ActivityFittingRoomBinding;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FittingRoomActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final String TAG = FittingRoomActivity.class.getSimpleName();
    private ActivityFittingRoomBinding binding;
    //    permission
    public static final String[] VUFORIA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private boolean permissionRequesting = false;

    //    vuforia states
    private boolean isVuInitialized = false;
    private boolean isVuforiaStarted = false;
    private boolean isSurfaceChanged;
    private int windowDisplayRotation = Surface.ROTATION_0;

    // gl
    private GLSurfaceView mGLView;
    private int renderWidth = 0;
    private int renderHeight = 0;

    //    thread
    Handler mainHandler;

    // Native Methods
    private native void initAR(Activity activity, AssetManager assetManager, int target);
    private native void deinitAR();

    private native boolean startAR();
    private native void stopAR();

    native void cameraPerformAutoFocus();
    native void cameraRestoreAutoFocus();

    private native boolean configureRendering(int width, int height, int orientation, int rotation);
    private native void initRendering();
    private native boolean renderFrame();
    private native void deinitRendering();
    private native void setTextures(int aWidth, int aHeight, ByteBuffer aBytes,
                                    int lWidth, int lHeight, ByteBuffer lBytes);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFittingRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainHandler = new Handler(getMainLooper());

        // setup GLView
        mGLView = binding.glMain;
        mGLView.setEGLContextClientVersion(3);
        // tambahkan callback
        mGLView.getHolder().addCallback(this);
        // tambahkan renderer
        mGLView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                initRendering();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                Log.i(TAG, "onSurfaceChanged: ");
                renderWidth = width;
                renderHeight = height;

//        todo: texture

                isSurfaceChanged = true;
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                Log.i(TAG, "onDrawFrame: ONDRAW");
                if(isVuforiaStarted){
                    Log.i(TAG, "onDrawFrame: VUFORIA STARTED AND DRAWING FRAME");
                    if(isSurfaceChanged || windowDisplayRotation != getWindowManager().getDefaultDisplay().getRotation()){
                        isSurfaceChanged = false;
                        windowDisplayRotation = getWindowManager().getDefaultDisplay().getRotation();

                        configureRendering(renderWidth, renderHeight, getResources().getConfiguration().orientation, windowDisplayRotation);
                    }

                    boolean didRender = renderFrame();
                    if(didRender){
                        Log.i(TAG, "onDrawFrame: RENDER");
                    }
                }
            }
        });

//        vuforia initial state
        isSurfaceChanged = false;
        isVuforiaStarted = false;

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // define observers

        // periksa izin dan inisialisasi vuforia
        if(isPermissionsGranted()){
            initializeVuforia().run();
        }

//        mGestureDetector = GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener);
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        Log.i(TAG, "onResume: PERMISSION STATE: " + isPermissionsGranted());

        if(!permissionRequesting && isPermissionsGranted()){
            if(startAR()){
                Log.i(TAG, "onResume: AR STARTED");
            }
        }else{
            ActivityCompat.requestPermissions(this, VUFORIA_PERMISSIONS, 0);
            permissionRequesting = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        todo: stopAR
        stopAR();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionRequesting = false;

        if(permissions.length==0){
            Toast.makeText(this, "Izin kamera tidak diberikan.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(isPermissionsGranted()){
            initializeVuforia().run();
        }else{
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isPermissionsGranted(){
        boolean result = true;
        for (String permission: VUFORIA_PERMISSIONS){
            result = result
                    &&
                    (ContextCompat.checkSelfPermission(this, permission)
                            ==
                            PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    /**
     * Vuforia Functions
     * @return
     */
    private Runnable initializeVuforia(){
        return (new Runnable(){
            @Override
            public void run() {
                initAR(FittingRoomActivity.this, FittingRoomActivity.this.getAssets(), 0);
            }
        });
    }

    @SuppressWarnings("unused")
    public void presentError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(this.getString(R.string.error_dialog_title));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopAR();
                deinitAR();
                FittingRoomActivity.this.finish();
            }
        });

        // This is called from another coroutine not on the Main thread
        // Showing the UI needs to be on the main thread
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }

    @SuppressWarnings("unused")
    public void vuInitDone() {
        isVuforiaStarted = startAR();
        if (isVuforiaStarted) {
            Log.i(TAG, "vuInitDone: VUFORIA STARTED");
        }
        // Show the GLView
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mGLView.setVisibility(View.VISIBLE);
                Log.i(TAG, "run: MGL VISIBLE");
            }
        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        deinitRendering();
    }

    static {
        System.loadLibrary("VirtualFittingRoom");
    }
}