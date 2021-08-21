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

import ioio.lib.api.AnalogInput;
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

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput led_;
            private AnalogInput analogInput_;

            @Override
            protected void setup() throws ConnectionLostException, InterruptedException {
                led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
                //analogInput_ = ioio_.openAnalogInput(Mode);

            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                flash(led_, 500);
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
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "start result: " + result + " (action: " + intent.getAction() + ", data: " + intent.getDataString() + ")");

        if (intent != null && intent.getAction() != null && intent.getAction().equals("stop")) {
            stopSelf();
        } else {
            // Service starting.
            Log.d(TAG, "service starting");
        }

        return result;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @UiThread
    public void flash(DigitalOutput led, int waitInMillis) throws ConnectionLostException, InterruptedException {
        int halfWait = waitInMillis / 4;

        led.write(true);
        Thread.sleep(halfWait);
        led.write(false);
        Thread.sleep(halfWait);

        Thread.sleep(waitInMillis);
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
