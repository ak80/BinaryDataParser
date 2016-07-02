package org.ak80.ubyte.bdp;

import org.ak80.ubyte.bdp.annotations.MappedByte;

public class TestClass {

  @MappedByte(index = 0, name = "byte 0")
  private int byte0;

  @MappedByte(index = 1, name = "byte 1")
  private int byte1;

  public int getByte0() {
    return byte0;
  }

  public void setByte0(int byte0) {
    this.byte0 = byte0;
  }

  public int getByte1() {
    return byte1;
  }

  public void setByte1(int byte1) {
    this.byte1 = byte1;
  }
}
