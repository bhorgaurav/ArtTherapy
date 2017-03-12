package edu.csulb.android.arttherapy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CanvasView canvasView;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private final UnlockListener listener = new UnlockListener();
    private final SensorEventListener sensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {

            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 12) {
                canvasView.eraseEverything();
                try {
                    MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.eraser);
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Snackbar.make(findViewById(R.id.coordinator_layout), "Erased! Go for it!", Snackbar.LENGTH_SHORT).show();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = (CanvasView) findViewById(R.id.canvas_view);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(sensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        Toast.makeText(getApplicationContext(), "Shake to erase!", Toast.LENGTH_SHORT).show();

        registerReceiver(listener, new IntentFilter(Intent.ACTION_USER_PRESENT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        registerReceiver(listener, new IntentFilter(Intent.ACTION_USER_PRESENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorListener);
        unregisterReceiver(listener);
    }
}