package com.virtualfittingroom.mlkitpose.components.posedetector;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtualfittingroom.mlkitpose.components.GraphicOverlay;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.util.ArrayList;

public class Processor extends VisionProcessorBase<Pose>{

    private static final String TAG = "PoseDetectorProcessor";

    private final Context context;
    private final PoseDetector detector;

    private final boolean showInFrameLikelihood;
    private final boolean visualizeZ;
    private final boolean rescaleZForVisualization;
    private final boolean isStreamMode;

    public Processor(
            Context context,
            PoseDetectorOptionsBase options,
            boolean showInFrameLikelihood,
            boolean visualizeZ,
            boolean rescaleZForVisualization,
            boolean isStreamMode) {
        super(context);
        this.context = context;

        detector = PoseDetection.getClient(options);

        this.showInFrameLikelihood = showInFrameLikelihood;
        this.visualizeZ = visualizeZ;
        this.rescaleZForVisualization = rescaleZForVisualization;
        this.isStreamMode = isStreamMode;
    }

    @Override
    protected Task<Pose> detectInImage(InputImage image) {
        return detector
                .process(image);
    }

    @Override
    protected void onSuccess(@NonNull Pose results, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.add(
                new PoseGraphic(
                        graphicOverlay,
                        results,
                        showInFrameLikelihood,
                        visualizeZ,
                        rescaleZForVisualization,
                        new ArrayList<>()
                )
        );
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "onFailure: Pose detection failed", e);
    }
}
