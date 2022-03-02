package org.pb.android.ioiofish.wrapper;

import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

@EBean
public class IOIOLooperWrapper extends BaseIOIOLooper {

    private static final String TAG = IOIOLooperWrapper.class.getSimpleName();

    private DigitalOutput statusLed;
    private AnalogInput analogInputLeftEar, analogInputRightEar;

    public IOIOLooperWrapper() {
    }

    @Override
    protected void setup() throws ConnectionLostException, InterruptedException {
        Log.d(TAG, "setup");
        showVersionInformation(ioio_);

        statusLed = ioio_.openDigitalOutput(IOIO.LED_PIN);

        analogInputLeftEar = ioio_.openAnalogInput(35);
        analogInputRightEar = ioio_.openAnalogInput(36);
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        //flashLed(led_, 500);
        statusLed.write(true);
    }

    @Override
    public void disconnected() {
        Log.d(TAG, "disconnected");
    }

    @Override
    public void incompatible() {
        Log.d(TAG, "incompatible");
    }

    @UiThread
    public void flashLed(DigitalOutput led, int waitInMillis) {

        try {
            int halfWait = waitInMillis / 4;

            led.write(true);
            Thread.sleep(halfWait);
            led.write(false);
            Thread.sleep(halfWait);

            Thread.sleep(waitInMillis);
        } catch (ConnectionLostException connectionLostException) {

        } catch (InterruptedException interruptedException) {

        }
    }

    private void showVersionInformation(IOIO ioio) {
        String versionInformation = String.format("%s %s %s %s", ioio.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                ioio.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                ioio.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                ioio.getImplVersion(IOIO.VersionType.HARDWARE_VER));

        Log.d(TAG, versionInformation);
    }

}
