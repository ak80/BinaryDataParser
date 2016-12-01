package org.ak80.bdp;

/**
 * Bit positions
 */
public enum Bit {

  BIT_0(0b00000001, 0),
  BIT_1(0b00000010, 1),
  BIT_2(0b00000100, 2),
  BIT_3(0b00001000, 3),
  BIT_4(0b00010000, 4),
  BIT_5(0b00100000, 5),
  BIT_6(0b01000000, 6),
  BIT_7(0b10000000, 7),;

  private final int mask;
  private final int index;

  Bit(int mask, int index) {
    this.mask = mask;
    this.index = index;
  }

  public int getMask() {
    return mask;
  }

  public int getIndex() {
    return index;
  }

  public static Bit fromIndex(int index) {
    switch (index) {
      case 7:
        return BIT_7;
      case 6:
        return BIT_6;
      case 5:
        return BIT_5;
      case 4:
        return BIT_4;
      case 3:
        return BIT_3;
      case 2:
        return BIT_2;
      case 1:
        return BIT_1;
      case 0:
        return BIT_0;
      default:
        return null;
    }
  }

}
