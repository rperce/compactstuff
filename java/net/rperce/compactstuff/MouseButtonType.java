package net.rperce.compactstuff;

public class MouseButtonType {
    public static final MouseButtonType
        LEFT   = new MouseButtonType(0b00),
        RIGHT  = new MouseButtonType(0b01),
        CENTER = new MouseButtonType(0b10);

    private final byte id;
    private MouseButtonType(int id) {
        this.id = (byte)id;
    }

    public MouseButtonType withShift(boolean b) {
        return new MouseButtonType(this.id | (b ? 0b1000 : 0));
    }
    public boolean hasShift() {
        return (id & 0b1000) > 0;
    }

    public static MouseButtonType fromByte(byte b) {
        return new MouseButtonType(b & 0b1111);
    }
    public byte toByte() {
        return id;
    }

    public static MouseButtonType from(int button, boolean shift) {
        return new MouseButtonType(button).withShift(shift);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MouseButtonType) &&
                (this.toByte() == ((MouseButtonType)o).toByte());
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        int button = this.id & 0b111;
        return  (button == 0 ? "LEFT" :
                 button == 1 ? "RIGHT" :
                 button == 2 ? "CENTER" : "WHAT") + (this.hasShift() ? "[SHIFT]" : "");
    }
}
