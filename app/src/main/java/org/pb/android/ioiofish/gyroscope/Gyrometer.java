package org.pb.android.ioiofish.gyroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.pb.android.ioiofish.util.LowPassFilter;

@EBean(scope = EBean.Scope.Singleton)
public class Gyrometer implements SensorEventListener {

    private static final String TAG = Gyrometer.class.getSimpleName();

    public interface RotationChangeListener {
        void onRotationChanged(float azimuth, float pitch, float roll);
    }

    @SystemService
    WindowManager windowManager;

    private SensorManager sensorManager;
    private Sensor gyrometerSensor;

    private float[] rotationMatrix = new float[9];
    private float[] rotationData = new float[3];

    private RotationChangeListener listener = null;

    public Gyrometer(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void start() {
        sensorManager.registerListener(this, gyrometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this, gyrometerSensor);
    }

    public void setListener(RotationChangeListener rotationChangeListener) {
        listener = rotationChangeListener;
    }

    public boolean hasListener() {
        return listener != null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                int worldAxisForDeviceAxisX;
                int worldAxisForDeviceAxisY;

                switch (windowManager.getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_90:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                        break;
                    case Surface.ROTATION_180:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                        break;
                    case Surface.ROTATION_270:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                        break;
                    case Surface.ROTATION_0:
                    default:
                        worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                        worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                        break;
                }

                float[] adjustedRotationMatrix = new float[9];
                SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX, worldAxisForDeviceAxisY, adjustedRotationMatrix);

                // Transform rotation matrix into azimuth/pitch/roll
                float[] orientation = new float[3];
                SensorManager.getOrientation(adjustedRotationMatrix, orientation);

                float azimuth = LowPassFilter.filter((float) Math.toDegrees(orientation[0]), rotationData[0], .8f);
                float pitch = LowPassFilter.filter((float) Math.toDegrees(orientation[1]), rotationData[1], .8f);
                float roll = LowPassFilter.filter((float) Math.toDegrees(orientation[2]), rotationData[2], .8f);

                // we're just interested in pitch changes this time
                boolean dataChanged = (int) pitch != (int) rotationData[1];

                if (listener != null && dataChanged) {
                    listener.onRotationChanged(azimuth, pitch, roll);
                }

                rotationData[0] = azimuth;
                rotationData[1] = pitch;
                rotationData[2] = roll;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Orientation sensor accuracy level: " + accuracy);

        if (accuracy != SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            Log.w(TAG, "Sensor needs to be calibrated!");
        }
    }
}
