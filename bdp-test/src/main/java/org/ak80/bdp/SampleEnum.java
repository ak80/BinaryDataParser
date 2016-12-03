package org.ak80.bdp;

/**
 * A sample mapped enum
 */
public enum SampleEnum {

  VALUE_A,

  VALUE_B,;

  public static SampleEnum mapFrom(int i) {
    switch (i) {
      case 0:
        return VALUE_A;
      case 1:
        return VALUE_B;
      default:
        return null;
    }
  }

  public int mapTo() {
    switch (this) {
      case VALUE_A:
        return 0;
      case VALUE_B:
        return 1;
      default:
        return 0;
    }
  }

}
