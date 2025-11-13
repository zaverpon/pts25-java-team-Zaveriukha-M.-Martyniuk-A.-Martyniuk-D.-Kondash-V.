package sk.uniba.fmph.dcs.terra_futura;

/**
 * Datatype representing victory points.
 */
public final class Points implements Comparable<Points> {

    private final int value;

    /**
     * Creates a new Points value.
     */
    public Points(final int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value of these points.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a new Points equal to this + other.
     */
    public Points add(final Points other) {
        if (other == null) {
            throw new IllegalArgumentException("other is null");
        }
        return new Points(Math.addExact(this.value, other.value));
    }

    /**
     * Returns a new Points equal to this + delta.
     */
    public Points add(final int delta) {
        return new Points(Math.addExact(this.value, delta));
    }

    /**
     * Returns a new Points equal to this * factor.
     */
    public Points multiply(final int factor) {
        return new Points(Math.multiplyExact(this.value, factor));
    }

    @Override
    public int compareTo(final Points o) {
        if (o == null) {
            throw new IllegalArgumentException("o is null");
        }
        return Integer.compare(this.value, o.value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Points)) {
            return false;
        }
        Points other = (Points) obj;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
