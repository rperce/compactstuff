package net.rperce.compactstuff;

/**
 * Created by Robert on 2/26/2016.
 */
public class Utilities {
    public static String enpackage(String s) {
        return "net.rperce.compactstuff."+s;
    }

    public static String colonize(String s, String x) {
        return String.format("%s:%s", s, x);
    }

    public static String colonVariant(String s, String x, String v) {
        return String.format("%s:%s_%s", s, x, v);
    }
}
