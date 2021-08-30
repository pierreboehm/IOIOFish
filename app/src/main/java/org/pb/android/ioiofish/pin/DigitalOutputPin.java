package org.pb.android.ioiofish.pin;

import org.pb.android.ioiofish.flow.FlowManager;

public class DigitalOutputPin extends IOIO_Pin {

    private final int pinNumber;
    private final FlowManager.PinConfiguration pinConfiguration;

    public DigitalOutputPin(FlowManager.PinConfiguration pinConfiguration) {
        this.pinNumber = pinConfiguration.pin;
        this.pinConfiguration = pinConfiguration;
    }

    @Override
    public int getPinNumber() {
        return pinNumber;
    }

    @Override
    public FlowManager.PinConfiguration getPinConfiguration() {
        return pinConfiguration;
    }

    @Override
    public FlowManager.PinType getPinType() {
        return FlowManager.PinType.DIGITAL_OUT;
    }

}
