package org.ak80.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BinaryDataParserTest {

  @Test
  public void parse_byteMapping_parserClassWithParseMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x01, 0x02, 0xff, 0xff, 0xff, 0xff};

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getByte0(), is(1));
    assertThat(testClass.getByte1(), is(2));
  }


  @Test
  public void parse_wordMapping_parserClassWithParseMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();

    int[] data = new int[]{0xff, 0xff, 0x01, 0x02, 0x03, 0x04};

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getWordBig(), is(0x0102));
    assertThat(testClass.getWordLittle(), is(0x0403));
  }

  @Test
  public void serialize_byteMapping_parserClassWithSerializeeMethod() {
    // Given
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    int[] dataExpected = new int[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00};

    TestClass testClass = new TestClass();
    testClass.setByte0(1);
    testClass.setByte1(2);

    // When
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }

}