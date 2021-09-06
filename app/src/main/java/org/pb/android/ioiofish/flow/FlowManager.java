package org.pb.android.ioiofish.flow;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.EBean;
import org.pb.android.ioiofish.pin.IOIO_Pin;

import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class FlowManager {

    public static final int SERVO_DEFAULT_PULSE_WIDTH = 50;

    public static final int SERVO_LEFT_DEFAULT_PIN = 10;
    public static final int SERVO_RIGHT_DEFAULT_PIN = 11;

    public static final int SERVO_POSITION_LEFT = 1000;
    public static final int SERVO_POSITION_CENTER = 1500;
    public static final int SERVO_POSITION_RIGHT = 2000;

    public enum PinType {
        ANALOG_IN, DIGITAL_IN, DIGITAL_OUT
    }

    public enum PinConfiguration {
        LEFT_SERVO(SERVO_LEFT_DEFAULT_PIN, PinType.DIGITAL_OUT),
        RIGHT_SERVO(SERVO_RIGHT_DEFAULT_PIN, PinType.DIGITAL_OUT),
        LEFT_EAR(40, PinType.ANALOG_IN),
        RIGHT_EAR(41, PinType.ANALOG_IN),
        TOUCH_FRONT_LEFT(21, PinType.DIGITAL_IN),
        TOUCH_FRONT_RIGHT(20, PinType.DIGITAL_IN),
        TOUCH_FRONT_TOP(19, PinType.DIGITAL_IN),
        TOUCH_FRONT_BOTTOM(22, PinType.DIGITAL_IN),
        TOUCH_SIDE_LEFT(23, PinType.DIGITAL_IN),
        TOUCH_SIDE_RIGHT(18, PinType.DIGITAL_IN);

        public final int pin;
        private final PinType pinType;

        PinConfiguration(int pin, PinType pinType) {
            this.pin = pin;
            this.pinType = pinType;
        }
    }

    private FlowConfiguration flowConfiguration;
    private float azimuth, pitch, roll;

    public void setup(FlowConfiguration flowConfiguration) {
        this.flowConfiguration = flowConfiguration;
    }

    public FlowConfiguration getFlowConfiguration() {
        return flowConfiguration;
    }

    public List<IOIO_Pin> getAnalogInputs() {
        return flowConfiguration.analogInputPins;
    }

    public List<IOIO_Pin> getDigitalInputs() {
        return flowConfiguration.digitalInputPins;
    }

    public List<IOIO_Pin> getDigitalOutputs() {
        return flowConfiguration.digitalOutputPins;
    }

    @Nullable
    public IOIO_Pin getServo(PinConfiguration pinConfiguration) {
        for (IOIO_Pin ioioPin : getDigitalOutputs()) {
            if (ioioPin.getPinConfiguration().equals(pinConfiguration)) {
                return ioioPin;
            }
        }

        return null;
    }

    @Nullable
    public IOIO_Pin getSensor(PinConfiguration pinConfiguration) {
        for (IOIO_Pin ioioPin : getDigitalInputs()) {
            if (ioioPin.getPinConfiguration().equals(pinConfiguration)) {
                return ioioPin;
            }
        }

        return null;
    }

    public void updateRotation(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }
}
