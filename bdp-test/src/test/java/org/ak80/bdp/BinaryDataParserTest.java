package org.ak80.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("PMD.MethodNamingConventions")
public class BinaryDataParserTest {

  private int[] data = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
  private int[] dataExpected = new int[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

  @Test
  public void byteMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();

    data[0] = 1;
    data[1] = 2;

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
  public void wordMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser();

    data[2] = 1;
    data[3] = 2;
    data[3] = 3;
    data[4] = 4;

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
  public void flagMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();
    testClass.setFlag0(true);
    testClass.setFlag1(true);

    TestClassParser parser = new TestClassParser();
    data[6] = 0b00010000;

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
    dataExpected[6] = 0b00010001;

    // When
    TestClass testClass = new TestClass();
    testClass.setFlag0(true);
    testClass.setFlag1(true);

    // When
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }

  @Test
  public void enumMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    data[7] = 0x01;

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getSampleEnum(), is(SampleEnum.VALUE_B));
  }

  @Test
  public void enumMapping_serialize_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    dataExpected[7] = 0x01;

    // When
    testClass.setSampleEnum(SampleEnum.VALUE_B);
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }


  @Test
  public void customEnumMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    data[7] = 0b01000000;

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getSampleEnumCustomMethods(), is(SampleEnumCustomMethods.VALUE_1));
  }

  @Test
  public void customEnumMapping_serialize_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    dataExpected[7] = 0b01000000;

    // When
    testClass.setSampleEnumCustomMethods(SampleEnumCustomMethods.VALUE_1);
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }


  @Test
  public void combinedEnumMapping_parse_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    data[7] = 0b01000001;

    // When
    parser.parse(testClass, data);

    // Then
    assertThat(testClass.getSampleEnum(), is(SampleEnum.VALUE_B));
    assertThat(testClass.getSampleEnumCustomMethods(), is(SampleEnumCustomMethods.VALUE_1));
  }

  @Test
  public void combinedMapping_serialize_setsValue() {
    // Given
    TestClass testClass = new TestClass();

    TestClassParser parser = new TestClassParser();
    dataExpected[7] = 0b01000001;

    // When
    testClass.setSampleEnum(SampleEnum.VALUE_B);
    testClass.setSampleEnumCustomMethods(SampleEnumCustomMethods.VALUE_1);
    parser.serialize(testClass, data);

    // Then
    assertThat(data, is(dataExpected));
  }
}