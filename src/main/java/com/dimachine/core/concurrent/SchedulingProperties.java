package com.dimachine.core.concurrent;

public class SchedulingProperties {
    private final long initialDelay;
    private final long fixedRate;
    private final long fixedDelay;

    private SchedulingProperties(SchedulingPropertiesBuilder builder) {
        this.initialDelay = builder.initialDelay;
        this.fixedRate = builder.fixedRate;
        this.fixedDelay = builder.fixedDelay;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getFixedRate() {
        return fixedRate;
    }

    public long getFixedDelay() {
        return fixedDelay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchedulingProperties that = (SchedulingProperties) o;

        if (initialDelay != that.initialDelay) return false;
        if (fixedRate != that.fixedRate) return false;
        return fixedDelay == that.fixedDelay;
    }

    @Override
    public int hashCode() {
        int result = (int) (initialDelay ^ (initialDelay >>> 32));
        result = 31 * result + (int) (fixedRate ^ (fixedRate >>> 32));
        result = 31 * result + (int) (fixedDelay ^ (fixedDelay >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SchedulingProperties{" +
                "initialDelay=" + initialDelay +
                ", fixedRate=" + fixedRate +
                ", fixedDelay=" + fixedDelay +
                '}';
    }

    public static SchedulingPropertiesBuilder builder() {
        return new SchedulingPropertiesBuilder();
    }

    public static class SchedulingPropertiesBuilder {
        private long initialDelay = -1;
        private long fixedRate = -1;
        public long fixedDelay = -1;

        public SchedulingPropertiesBuilder initialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public SchedulingPropertiesBuilder fixedRate(long fixedRate) {
            this.fixedRate = fixedRate;
            return this;
        }

        public SchedulingPropertiesBuilder fixedDelay(long fixedDelay) {
            this.fixedDelay = fixedDelay;
            return this;
        }

        public SchedulingProperties build() {
            return new SchedulingProperties(this);
        }
    }
}
