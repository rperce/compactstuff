package net.rperce.compactstuff;

public class ClickType {
    public static final ClickType
        LEFT   = new ClickType(0b00),
        RIGHT  = new ClickType(0b01),
        CENTER = new ClickType(0b10);

    private final byte id;
    private ClickType(int id) {
        this.id = (byte)id;
    }

    public ClickType withShift(boolean b) {
        return new ClickType(this.id | (b ? 0b1000 : 0));
    }
    public boolean hasShift() {
        return (id & 0b1000) > 0;
    }

    public static ClickType fromByte(byte b) {
        return new ClickType(b & 0b1111);
    }
    public byte toByte() {
        return id;
    }

    public static ClickType from(int button, boolean shift) {
        return new ClickType(button).withShift(shift);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ClickType) &&
                (this.toByte() == ((ClickType)o).toByte());
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
