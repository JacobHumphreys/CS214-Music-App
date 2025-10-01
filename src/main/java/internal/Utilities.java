package internal;

public class Utilities {
    /**
     * converts a Double[] to double[]
     *
     * @param values the input Double[]
     * @return the output double[]
     */
    public static double[] doubleArrToPrimative(Double[] values) {
        var primatives = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            primatives[i] = values[i] != null ? values[i] : Double.NaN;
        }
        return primatives;
    }
}
