package com.dimachine.core.postprocessor;

public class SchedulingProperties {
    private final long initialDelay;
    private final long fixedRate;

    private SchedulingProperties(SchedulingPropertiesBuilder builder) {
        this.initialDelay = builder.initialDelay;
        this.fixedRate = builder.fixedRate;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getFixedRate() {
        return fixedRate;
    }

    @Override
    public String toString() {
        return "SchedulingProperties{" +
                "initialDelay=" + initialDelay +
                ", fixedRate=" + fixedRate +
                '}';
    }

    public static SchedulingPropertiesBuilder builder() {
        return new SchedulingPropertiesBuilder();
    }

    public static class SchedulingPropertiesBuilder {
        private long initialDelay;
        private long fixedRate;

        public SchedulingPropertiesBuilder initialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public SchedulingPropertiesBuilder fixedRate(long fixedRate) {
            this.fixedRate = fixedRate;
            return this;
        }

        public SchedulingProperties build() {
            return new SchedulingProperties(this);
        }
    }
}
