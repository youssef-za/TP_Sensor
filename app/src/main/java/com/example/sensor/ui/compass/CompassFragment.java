package com.example.sensor.ui.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensor.R;

public class CompassFragment extends Fragment implements SensorEventListener {

    private ImageView image;
    private TextView tvHeading;

    private float currentDegree = 0f;

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public CompassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        if (rotationSensor == null) {
            Toast.makeText(getContext(), R.string.message_neg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compass, container, false);
        image = root.findViewById(R.id.imageViewCompass);
        tvHeading = root.findViewById(R.id.tvHeading);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        float azimuth = (float) Math.toDegrees(orientationAngles[0]);
        azimuth = (azimuth + 360) % 360;

        tvHeading.setText("Heading: " + Math.round(azimuth) + "°");

        RotateAnimation rotateAnimation = new RotateAnimation(
                currentDegree,
                -azimuth,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);

        image.startAnimation(rotateAnimation);
        currentDegree = -azimuth;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Optional
    }
}