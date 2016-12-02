package org.ak80.bdp;

import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.annotations.MappedWord;
import org.junit.Test;

import static org.ak80.bdp.testutils.Utils.createMappedWord;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappingInfoWordTest {

  @Test
  public void getMethodBodyGetter_bigEndian_produceMethodBody() {
    // Given
    MappedWord mappedWord = createMappedWord(1, "name", Endian.BIG_ENDIAN);
    MappingInfo mappingInfo = new MappingInfoWord("field", "int", mappedWord);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = (getFoo() >>> BYTE_LENGTH) & BYTE_MASK;\ndata[2] = getFoo() & BYTE_MASK;\n"));
  }

  @Test
  public void getMethodBod_bigEndian_produceMethodBody() {
    // Given
    MappedWord mappedWord = createMappedWord(1, "name", Endian.BIG_ENDIAN);
    MappingInfo mappingInfo = new MappingInfoWord("field", "int", mappedWord);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo((data[1] << BYTE_LENGTH) + data[2]);\n"));
  }

  @Test
  public void getMethodBodyGetter_littleEndian_produceMethodBody() {
    // Given
    MappedWord mappedWord = createMappedWord(1, "name", Endian.LITTLE_ENDIAN);
    MappingInfo mappingInfo = new MappingInfoWord("field", "int", mappedWord);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = getFoo() & BYTE_MASK;\ndata[2] = (getFoo() >>> BYTE_LENGTH) & BYTE_MASK;\n"));
  }

  @Test
  public void getMethodBod_littleEndian_produceMethodBody() {
    // Given
    MappedWord mappedWord = createMappedWord(1, "name", Endian.LITTLE_ENDIAN);
    MappingInfo mappingInfo = new MappingInfoWord("field", "int", mappedWord);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo((data[2] << BYTE_LENGTH) + data[1]);\n"));
  }
}
