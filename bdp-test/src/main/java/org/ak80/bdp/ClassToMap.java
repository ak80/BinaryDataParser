package org.ak80.bdp;

import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.annotations.MappedByte;
import org.ak80.bdp.annotations.MappedWord;

/**
 * Use as Sample for Readme.md, see also @link{ClassToMapTest}
 */
public class ClassToMap {

  @MappedByte(index = 0)
  private int byte0;

  @MappedByte(index = 1)
  private int byte1;

  @MappedWord(index = 2, endianess = Endian.BIG_ENDIAN)
  private int wordBig;

  @MappedWord(index = 4, endianess = Endian.LITTLE_ENDIAN)
  private int wordLittle;


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
}
