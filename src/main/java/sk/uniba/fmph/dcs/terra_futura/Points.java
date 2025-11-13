package sk.uniba.fmph.dcs.terra_futura;

public class Points implements Comparable<Points> {
    private final int val;

    public Points(int value)  {
        this.val = value;
    }

    public int getValue()  {
        return val;
    }

    public Points plus(Points other)  {
        return new Points(val + other.val);
    }
    public Points minus(Points other)  {
        return new Points(val - other.val);
    }

    @Override
    public int compareTo(Points p)  {
        return Integer.compare(this.val, p.getValue());
    }

    @Override
    public String toString()  {
        return "Points:" + val;
    }

}
