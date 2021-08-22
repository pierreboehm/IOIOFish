package org.pb.android.ioiofish.flow;

public class FlowConfiguration {

    private FlowConfiguration(Builder builder) {

    }

    public static class Builder {

        public FlowConfiguration getConfiguration() {
            return new FlowConfiguration(this);
        }
    }

}
