package org.ak80.bdp;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Use as Sample for Readme.md, see also @link{ClassToMap}
 */
public class ClassToMapTest {

  @Test
  public void testParse() {
    // Given
    ClassToMap classToMap = new ClassToMap();
    ClassToMapParser parser = new ClassToMapParser();

    int[] data = new int[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

    // When
    parser.parse(classToMap, data);

    // Then
    assertThat(classToMap.getByte0(), is(0x01));
    assertThat(classToMap.getByte1(), is(0x02));
    assertThat(classToMap.getWordBig(), is(0x0304));
    assertThat(classToMap.getWordLittle(), is(0x0605));
  }

  @Test
  public void testSerialize() {
    // Given
    ClassToMap classToMap = new ClassToMap();
    ClassToMapParser parser = new ClassToMapParser();

    classToMap.setByte0(0x01);
    classToMap.setByte1(0x02);
    classToMap.setWordBig(0x0304);
    classToMap.setWordLittle(0x0506);

    int[] data = new int[6];

    // When
    parser.serialize(classToMap, data);

    // Then
    assertThat(data, is(new int[]{0x01, 0x02, 0x03, 0x04, 0x06, 0x05}));
  }

}
