package org.ak80.bdp;

/**
 * Utilities for binary handling
 */
public abstract class BinaryUtils {

  public static final int BYTE_LENGTH = 8;
  public static final int BYTE_MASK = 0x00ff;

  public static int getRangeMask(Bit from, Bit to) {
    int output = 0;
    for (int i = 7; i >= 0; i--) {
      Bit currentBit = Bit.fromIndex(i);
      if (inRange(currentBit, from, to)) {
        output = output | currentBit.getMask();
      }
    }
    return output;
  }

  private static boolean inRange(Bit currentBit, Bit from, Bit to) {
    boolean isLessOrEqualFrom = currentBit.getIndex() <= from.getIndex();
    boolean isGreaterOrEqualTo = currentBit.getIndex() >= to.getIndex();
    return isLessOrEqualFrom && isGreaterOrEqualTo;
  }

  private static boolean bitIsSet(final int intValue, final Bit bit) {
    return (intValue & bit.getMask()) == bit.getMask();
  }
}
