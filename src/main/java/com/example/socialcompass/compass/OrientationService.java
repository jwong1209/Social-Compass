package com.example.socialcompass.compass;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService implements SensorEventListener {
    private static OrientationService instance;

    private final SensorManager sensorManager;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private MutableLiveData<Float> azimuth;
    private Compass compass;
    private CompassLocation user;

    /**
     * Constructor for OrientationService
     * @param activity Context needed to initialize SensorManager
     */
    public OrientationService(Activity activity) {
        this.azimuth = new MutableLiveData<>();
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        // Register sensor listeners
        this.registerSensorListeners();
    }

    private void registerSensorListeners() {
        // Register our listener to the accelerometer and magnetometer (needed to compute orientation)
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static OrientationService singleton(Activity activity) {
        if (instance == null) {
            instance = new OrientationService(activity);
        }
        return instance;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerReading = event.values;
        }
        if (accelerometerReading != null && magnetometerReading != null) {
            // We have both sensors -- we can now compute the orientation.
            onBothSensorDataAvailable();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void onBothSensorDataAvailable() {
        // Discount contract checking
        if (accelerometerReading == null || magnetometerReading == null) {
            throw new IllegalStateException("Both sensors must be available to compute orientation.");
        }

        float[] r = new float[9];
        float[] i = new float[9];
        boolean success = SensorManager.getRotationMatrix(r, i, accelerometerReading, magnetometerReading);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(r, orientation);

            // Orientation order: azimuth, pitch, and roll
            // Azimuth of 0 means that the device is pointed north, pi means that it's pointed south,
            // pi/2 means that it's pointed east, 3pi/2 means that it's pointed west
            this.azimuth.postValue(orientation[0]);
        }
    }

    public void unregisterSensorListeners() {
        sensorManager.unregisterListener(this);
    }

    public LiveData<Float> getOrientation() {
        return this.azimuth;
    }

    public void setMockOrientationSource(MutableLiveData<Float> mockDataSource) {
        unregisterSensorListeners();
        this.azimuth = mockDataSource;
    }
}