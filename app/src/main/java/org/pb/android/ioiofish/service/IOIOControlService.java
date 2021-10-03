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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ioio.lib.api.Closeable;
import ioio.lib.api.DigitalInput;
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

    private List<Closeable> openedPins = new ArrayList<>();
    private Map<Integer, Closeable> sensors = new HashMap<>();
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

    public boolean isRunning() {
        return gyrometer.hasListener();     // FIXME
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

                // --- SETUP HARDWARE ---
                setupStatusLed();
                setupServos();
                setupSensors();
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                readSensorContact();

                if (rotationDetected) {
                    flashLed(statusLed, DEFAULT_SLEEP_IN_MILLIS);
                    runServos(leftServo, rightServo);
                    rotationDetected = false;
                }

                if (flowManager.hasContact()) {
                    handleSensorContact();
                    //runServos(leftServo, rightServo);
                }
            }

            @Override
            public void disconnected() {
                EventBus.getDefault().postSticky(new Events.PluggedStateChangedEvent(false));

                try {
                    // close all open pins before leave!
                    for (Closeable openPin : openedPins) {
                        openPin.close();
                    }
                } catch (IllegalStateException illegalStateException) {
                    Log.w(TAG, Objects.requireNonNull(illegalStateException.getLocalizedMessage()));
                }

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

            private void setupStatusLed() throws ConnectionLostException {
                statusLed = ioio_.openDigitalOutput(IOIO.LED_PIN, true);    // Note: true means OFF (wtf!)
                openedPins.add(statusLed);
            }

            private void setupServos() throws ConnectionLostException {
                leftServo = setupServo(FlowManager.PinConfiguration.LEFT_SERVO, FlowManager.SERVO_LEFT_DEFAULT_PIN);
                rightServo = setupServo(FlowManager.PinConfiguration.RIGHT_SERVO, FlowManager.SERVO_RIGHT_DEFAULT_PIN);
            }

            private void setupSensors() throws ConnectionLostException {
                setupSensor(FlowManager.PinConfiguration.TOUCH_FRONT_TOP, FlowManager.SENSOR_FRONT_TOP_DEFAULT_PIN);
                setupSensor(FlowManager.PinConfiguration.TOUCH_FRONT_BOTTOM, FlowManager.SENSOR_FRONT_BOTTOM_DEFAULT_PIN);

                setupSensor(FlowManager.PinConfiguration.TOUCH_FRONT_LEFT, FlowManager.SENSOR_FRONT_LEFT_DEFAULT_PIN);
                setupSensor(FlowManager.PinConfiguration.TOUCH_FRONT_RIGHT, FlowManager.SENSOR_FRONT_RIGHT_DEFAULT_PIN);

                setupSensor(FlowManager.PinConfiguration.TOUCH_SIDE_LEFT, FlowManager.SENSOR_SIDE_LEFT_DEFAULT_PIN);
                setupSensor(FlowManager.PinConfiguration.TOUCH_SIDE_RIGHT, FlowManager.SENSOR_SIDE_RIGHT_DEFAULT_PIN);
            }

            private PwmOutput setupServo(FlowManager.PinConfiguration pinConfiguration, int defaultPin) throws ConnectionLostException {
                IOIO_Pin pinDefinition = flowManager.getServo(pinConfiguration);
                int servoPin = pinDefinition == null ? defaultPin : pinDefinition.getPinNumber();

                PwmOutput pwmOutput = ioio_.openPwmOutput(servoPin, FlowManager.SERVO_DEFAULT_PULSE_WIDTH);
                openedPins.add(pwmOutput);

                return pwmOutput;
            }

            private void setupSensor(FlowManager.PinConfiguration pinConfiguration, int defaultPin) throws ConnectionLostException {
                IOIO_Pin pinDefinition = flowManager.getSensor(pinConfiguration);
                int sideLeftSensorPin = pinDefinition == null ? defaultPin : pinDefinition.getPinNumber();

                DigitalInput digitalInput = ioio_.openDigitalInput(sideLeftSensorPin, DigitalInput.Spec.Mode.PULL_UP);
                openedPins.add(digitalInput);
                sensors.put(sideLeftSensorPin, digitalInput);
            }
        };
    }

    @UiThread
    public void flashLed(DigitalOutput led, int waitInMillis) throws ConnectionLostException, InterruptedException {
        led.write(false);   // ON
        Thread.sleep(waitInMillis);
        led.write(true);    // OFF
    }

    @UiThread
    public void runServos(PwmOutput leftServo, PwmOutput rightServo) throws ConnectionLostException, InterruptedException {
        // TODO: servos will be used in two cases:
        //  1) keep balance (depends on current rotation-vector)
        //  2) react on obstacles (depends on haptic sensor states)

        leftServo.setPulseWidth((float) (Math.sin((double) servoStep)) + 1f);
        rightServo.setPulseWidth((float) (Math.cos((double) servoStep)) + 1f);

        servoStep++;

        Thread.sleep(DEFAULT_SLEEP_IN_MILLIS);
    }

    @UiThread
    public void readSensorContact() throws ConnectionLostException, InterruptedException {

        for (Map.Entry<Integer, Closeable> sensor : sensors.entrySet()) {
            boolean value = !((DigitalInput) sensor.getValue()).read();
            int pinNumber = sensor.getKey();

            flowManager.setSensorState(pinNumber, value);

            if (value) {
                EventBus.getDefault().post(new Events.SignalLevelReceivedEvent(pinNumber));
            }
        }

        Thread.sleep(DEFAULT_SLEEP_IN_MILLIS);
    }

    @UiThread
    public void handleSensorContact() throws ConnectionLostException, InterruptedException {
        // TODO: here I need an extra information which sensor (location!) has contact to be able to react on it
        List<Integer> leftSideSensors = flowManager.getLeftSideSensorsWithContact();
        List<Integer> rightSideSensors = flowManager.getRightSideSensorsWithContact();

        Thread.sleep(DEFAULT_SLEEP_IN_MILLIS);
    }
}
