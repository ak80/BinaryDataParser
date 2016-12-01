package org.ak80.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("PMD.MethodNamingConventions")
public class BinaryDataParserTest {

  @Test
  public void byteMapping_parse_hasGetMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x01, 0x02, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff};

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getByte0(), is(1));
    assertThat(testClass.getByte1(), is(2));
  }

  @Test
  public void byteMapping_serialize_serializeFromSetMethod() {
    // Given
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    int[] dataExpected = new int[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    TestClass testClass = new TestClass();
    testClass.setByte0(1);
    testClass.setByte1(2);

    // When
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }

  @Test
  public void wordMapping_parse_hasGetMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();

    int[] data = new int[]{0xff, 0xff, 0x01, 0x02, 0x03, 0x04, 0xff, 0xff};

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getWordBig(), is(0x0102));
    assertThat(testClass.getWordLittle(), is(0x0403));
  }

  @Test
  public void wordMapping_serialize_serializeFromSetMethod() {
    // Given
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    int[] dataExpected = new int[]{0x00, 0x00, 0x12, 0x34, 0x78, 0x56, 0x00, 0x00};

    TestClass testClass = new TestClass();
    testClass.setWordBig(0x1234);
    testClass.setWordLittle(0x5678);

    // When
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }

  @Test
  public void flagMapping_parse_hasIsMethod() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0b00010000};

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.isFlag0(), is(false));
    assertThat(testClass.isFlag1(), is(true));
  }

  @Test
  public void flagMapping_serialize_fromSetMethod() {
    // Given
    TestClassParser parser = new TestClassParser();
    int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    int[] dataExpected = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0b00010000};

    // When
    TestClass testClass = new TestClass();
    testClass.setFlag1(true);

    // When
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }

}