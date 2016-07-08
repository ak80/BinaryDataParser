package org.ak80.ubyte.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BinaryDataParserTest {

  @Test
  public void byteMapping_parseMethod_parseClassWithParseMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();
    int[] data = new int[] { 0x01, 0x02,0xff,0xff,0xff,0xff };

    // When
    parser.parse(testClass,data);

    // Then
    assertThat(testClass.getByte0(), is(1));
    assertThat(testClass.getByte1(), is(2));
  }


  @Test
  public void wordMapping_parser_parserClassWithParser() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();

    int[] data = new int[] { 0xff, 0xff, 0x01, 0x02, 0x03, 0x04 };

    // When
    parser.parse(testClass,data);

    // Then
    assertThat(testClass.getWordBig(), is(0x0102));
    assertThat(testClass.getWordLittle(), is(0x0403));
  }

}