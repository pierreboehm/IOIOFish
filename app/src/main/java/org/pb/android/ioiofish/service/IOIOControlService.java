package org.pb.android.ioiofish.service;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

@EService
public class IOIOControlService extends IOIOService {

    private static final String TAG = IOIOControlService.class.getSimpleName();

    @SystemService
    NotificationManager nm;

    @SystemService
    Vibrator vibrator;

    private enum VibrationTypes {
        TINY(50),
        SHORT(100),
        LONG(200);

        int typeNumber;

        VibrationTypes(int typeNumber) {
            this.typeNumber = typeNumber;
        }
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput led_;
            private AnalogInput analogInput_;

            @Override
            protected void setup() throws ConnectionLostException, InterruptedException {
                led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
                //analogInput_ = ioio_.openAnalogInput(Mode);

                Log.d(TAG, "select LED_PIN for DigitalOutput");
                vibrate(VibrationTypes.SHORT);
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
        return null;
    }

    @UiThread
    public void flash(DigitalOutput led, int waitInMillis) throws ConnectionLostException, InterruptedException {
        int halfWait = waitInMillis / 4;

        led.write(true);
        Thread.sleep(halfWait);
        led.write(false);
        Thread.sleep(halfWait);

        Thread.sleep(waitInMillis);
        vibrate(VibrationTypes.TINY);
    }

    @UiThread
    private void vibrate(VibrationTypes type) {
        int typeNumber = type.typeNumber;
        if (Build.VERSION.SDK_INT >= 26) {
            //vibrator.vibrate(VibrationEffect.createOneShot(typeNumber, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //vibrator.vibrate(typeNumber);
        }
    }
}
