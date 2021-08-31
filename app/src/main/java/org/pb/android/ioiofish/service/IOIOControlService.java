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
import org.pb.android.ioiofish.flow.FlowManager;
import org.pb.android.ioiofish.gyroscope.Gyrometer;
import org.pb.android.ioiofish.pin.IOIO_Pin;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

@EService
public class IOIOControlService extends IOIOService implements Gyrometer.RotationChangeListener {

    public static final int DEFAULT_SLEEP_IN_MILLIS = 10;
    private static final String TAG = IOIOControlService.class.getSimpleName();

    @Bean
    Gyrometer gyrometer;

    @Bean
    FlowManager flowManager;

    public IBinder binder = new LocalBinder();
    private boolean rotationDetected = false;
    private long servoStep = 0L;

    public class LocalBinder extends Binder {
        public IOIOControlService getService() {
            return IOIOControlService.this;
        }
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

    @Override
    public void onRotationChanged(float azimuth, float pitch, float roll) {
        rotationDetected = true;
        flowManager.updateRotation(azimuth, pitch, roll);

        if (gyrometer.hasListener()) {
            EventBus.getDefault().post(new Events.RotationChangedEvent(azimuth, pitch, roll));
        }
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

    // --- IOIO-board related code
    // TODO: find a way to implement it in an separate file (IOIOLooperWrapper does not work :|)

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput statusLed;
            private PwmOutput leftServo, rightServo;

            @Override
            protected void setup() throws ConnectionLostException {
                EventBus.getDefault().postSticky(new Events.PluggedStateChangedEvent(true));

                Log.d(TAG, "setup");
                showVersionInformation();

                statusLed = ioio_.openDigitalOutput(IOIO.LED_PIN, true);

                // Configuration is already prepared with a set of pins that will be used.
                IOIO_Pin leftServoDefinition = flowManager.getServo(FlowManager.PinConfiguration.LEFT_SERVO);
                IOIO_Pin rightServoDefinition = flowManager.getServo(FlowManager.PinConfiguration.RIGHT_SERVO);

                int leftServoPin = leftServoDefinition == null
                        ? FlowManager.SERVO_LEFT_DEFAULT_PIN
                        : leftServoDefinition.getPinNumber();

                int rightServoPin = rightServoDefinition == null
                        ? FlowManager.SERVO_RIGHT_DEFAULT_PIN
                        : rightServoDefinition.getPinNumber();

                leftServo = ioio_.openPwmOutput(
                        leftServoPin,
                        FlowManager.SERVO_DEFAULT_PULSE_WIDTH);
                rightServo = ioio_.openPwmOutput(
                        rightServoPin,
                        FlowManager.SERVO_DEFAULT_PULSE_WIDTH);
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                if (rotationDetected) {
                    flashLed(statusLed, DEFAULT_SLEEP_IN_MILLIS);
                    runServos(leftServo, rightServo);
                    rotationDetected = false;
                }
            }

            @Override
            public void disconnected() {
                EventBus.getDefault().postSticky(new Events.PluggedStateChangedEvent(false));

                statusLed.close();
                leftServo.close();
                rightServo.close();

                Log.d(TAG, "disconnected");
            }

            @Override
            public void incompatible() {
                Log.d(TAG, "incompatible");
            }

            private void showVersionInformation() {
                String versionInformation = String.format("library: %s, firmware: %s, bootloader: %s, hardware: %s",
                        ioio_.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                        ioio_.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                        ioio_.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                        ioio_.getImplVersion(IOIO.VersionType.HARDWARE_VER));

                Log.d(TAG, versionInformation);
            }
        };
    }

    @UiThread
    public void flashLed(DigitalOutput led, int waitInMillis) throws ConnectionLostException, InterruptedException {
        led.write(false);
        Thread.sleep(waitInMillis);
        led.write(true);
    }

    @UiThread
    public void runServos(PwmOutput leftServo, PwmOutput rightServo) throws ConnectionLostException, InterruptedException {
        leftServo.setPulseWidth((float) (Math.sin((double) servoStep)) + 1f);
        rightServo.setPulseWidth((float) (Math.cos((double) servoStep)) + 1f);

        servoStep++;

        Thread.sleep(DEFAULT_SLEEP_IN_MILLIS);
    }
}
