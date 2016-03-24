package net.rperce.compactstuff;

import java.util.stream.IntStream;

public class IntRange {
    private final int a, b;
    private IntRange(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public static IntRange open(int a, int b) {
        return new IntRange(a, b);
    }
    public static IntRange closed(int a, int b) {
        return new IntRange(a, b + 1);
    }
    public static IntRange only(int a) {
        return new IntRange(a, a + 1);
    }
    public boolean contains(int c) {
        return a <= c && c < b;
    }

    public int first() { return a; }
    public int last() { return b - 1; }
    public IntStream stream() {
        return IntStream.range(a, b);
    }
    public int count() {
        return b - a + 1;
    }
    public IntRange combineWith(IntRange other) {
        return IntRange.closed(a, other.last());
    }
    public int index(int in) {
        return a + in;
    }
}
