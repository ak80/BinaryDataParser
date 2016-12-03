package org.ak80.bdp;

/**
 * A sample mapped enum
 */
public enum SampleEnumCustomMethods {

  VALUE_0,

  VALUE_1,;

  public static SampleEnumCustomMethods fromCode(int i) {
    switch (i) {
      case 0:
        return VALUE_0;
      case 1:
        return VALUE_1;
    }
    return null;
  }

  public int toCode() {
    switch (this) {
      case VALUE_0:
        return 0;
      case VALUE_1:
        return 1;
    }
    return -1;
  }

}
