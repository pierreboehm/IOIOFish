package org.pb.android.ioiofish.flow;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.EBean;
import org.pb.android.ioiofish.pin.IOIO_Pin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final List<Integer> LEFT_SIDE_SENSORS = new ArrayList<>(Arrays.asList(
            PinConfiguration.TOUCH_FRONT_LEFT.pin,
            PinConfiguration.TOUCH_SIDE_LEFT.pin
    ));

    private static final List<Integer> RIGHT_SIDE_SENSORS = new ArrayList<>(Arrays.asList(
            PinConfiguration.TOUCH_FRONT_RIGHT.pin,
            PinConfiguration.TOUCH_SIDE_RIGHT.pin
    ));

    private FlowConfiguration flowConfiguration;
    private float azimuth, pitch, roll;
    private Map<Integer, Boolean> sensorState = new HashMap<>();

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

    public void setSensorState(int pinNumber, boolean state) {
        sensorState.put(pinNumber, state);  // will be replaced if exists
    }

    public boolean getSensorState(int pinNumber) {
        if (sensorState.containsKey(pinNumber)) {
            return sensorState.get(pinNumber);
        }
        return false;
    }

    public boolean hasContact() {
        for (Boolean sensorState : sensorState.values()) {
            if (sensorState) {
                return true;
            }
        }
        return false;
    }

    public void updateRotation(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }

    public List<Integer> getLeftSideSensorsWithContact() {
        return getSensorsWithContact(LEFT_SIDE_SENSORS);
    }

    public List<Integer> getRightSideSensorsWithContact() {
        return getSensorsWithContact(RIGHT_SIDE_SENSORS);
    }

    private List<Integer> getSensorsWithContact(List<Integer> sideSpecificSensorPinList) {
        List<Integer> resultList = new ArrayList<>();

        for (Integer sensorPin : sideSpecificSensorPinList) {
            if (sensorState.get(sensorPin)) {
                resultList.add(sensorPin);
            }
        }

        return resultList;
    }
}
