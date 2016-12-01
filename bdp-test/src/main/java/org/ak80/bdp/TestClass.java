package org.ak80.bdp;

import org.ak80.bdp.annotations.*;

public class TestClass {

  @MappedByte(index = 0, name = "byte 0")
  private int byte0;

  @MappedByte(index = 1, name = "byte 1")
  private int byte1;

  @MappedWord(index = 2, name = "word big", endianess = Endian.BIG_ENDIAN)
  private int wordBig;

  @MappedWord(index = 4, name = "word little", endianess = Endian.LITTLE_ENDIAN)
  private int wordLittle;

  @MappedFlag(index = 6, bit = Bit.BIT_0, name = "flag 0")
  private boolean flag0;

  @MappedFlag(index = 6, bit = Bit.BIT_4, name = "flag 1")
  private boolean flag1;

  @MappedEnum(index = 7, from = Bit.BIT_5, to = Bit.BIT_0, name = "mapped Enum")
  private SampleEnum sampleEnum = SampleEnum.VALUE_A;

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

  public int getWordBig() {
    return wordBig;
  }

  public void setWordBig(int wordBig) {
    this.wordBig = wordBig;
  }

  public int getWordLittle() {
    return wordLittle;
  }

  public void setWordLittle(int wordLittle) {
    this.wordLittle = wordLittle;
  }

  public boolean isFlag0() {
    return flag0;
  }

  public void setFlag0(boolean flag0) {
    this.flag0 = flag0;
  }

  public boolean isFlag1() {
    return flag1;
  }

  public void setFlag1(boolean flag1) {
    this.flag1 = flag1;
  }

  public SampleEnum getSampleEnum() {
    return sampleEnum;
  }

  public void setSampleEnum(SampleEnum sampleEnum) {
    this.sampleEnum = sampleEnum;
  }

}