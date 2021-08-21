package org.pb.android.ioiofish.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.greenrobot.eventbus.EventBus;
import org.pb.android.ioiofish.event.Events;
import org.pb.android.ioiofish.gyroscope.Gyrometer;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

@EService
public class IOIOControlService extends IOIOService implements Gyrometer.RotationChangeListener {

    private static final String TAG = IOIOControlService.class.getSimpleName();

    public IBinder binder = new LocalBinder();

    @Bean
    Gyrometer gyrometer;

    private boolean rotationDetected = false;

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput led_;

            @Override
            protected void setup() throws ConnectionLostException {
                Log.d(TAG, "setup");
                showVersionInformation();

                led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                if (rotationDetected) {
                    flashLed(led_, 10);
                }
            }

            @Override
            public void disconnected() {
                Log.d(TAG, "disconnected");
            }

            @Override
            public void incompatible() {
                Log.d(TAG, "incompatible");
            }

            private void showVersionInformation() {
                String versionInformation = String.format("%s %s %s %s", ioio_.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                        ioio_.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                        ioio_.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                        ioio_.getImplVersion(IOIO.VersionType.HARDWARE_VER));

                Log.d(TAG, versionInformation);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "created");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @UiThread
    public void flashLed(DigitalOutput led, int waitInMillis) throws ConnectionLostException, InterruptedException {
        led.write(false);
        rotationDetected = false;

        Thread.sleep(waitInMillis);
        led.write(true);
    }

    public void startService() {
        gyrometer.setListener(this);
        gyrometer.start();
    }

    public void stopService() {
        gyrometer.setListener(null);
        gyrometer.stop();
        stopSelf();
    }

    @Override
    public void onRotationChanged(float azimuth, float pitch, float roll) {
        rotationDetected = true;

        if (gyrometer.hasListener()) {
            EventBus.getDefault().post(new Events.RotationChangedEvent(azimuth, pitch, roll));
        }
    }

    public class LocalBinder extends Binder {
        public IOIOControlService getService() {
            return IOIOControlService.this;
        }
    }
}
