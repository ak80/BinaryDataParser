package org.ak80.ubyte.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ByteDataParserTest {

  @Test
  public void byteMapping_setterGeneration_generatesSetter() {
    // Given
    TestClass testClass = new TestClass();
    TestClassParser parser = new TestClassParser(testClass);

    // When
    parser.setByte0(1);
    parser.setByte1(2);

    assertThat(testClass.getByte0(), is(1));
    assertThat(testClass.getByte1(), is(2));
  }

}