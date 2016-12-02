package org.ak80.bdp;

import org.ak80.bdp.annotations.MappedByte;
import org.junit.Test;

import static org.ak80.bdp.testutils.Utils.createMappedByte;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappingInfoByteTest {

  @Test
  public void getMethodBodyGetter_produceMethodBody() {
    // Given
    MappedByte mappedByte = createMappedByte(1, "name");
    MappingInfo mappingInfo = new MappingInfoByte("field", "int", mappedByte);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = getFoo();\n"));
  }

  @Test
  public void getMethodBod_Setter_produceMethodBody() {
    // Given
    MappedByte mappedByte = createMappedByte(1, "name");
    MappingInfo mappingInfo = new MappingInfoByte("field", "int", mappedByte);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo(data[1]);\n"));
  }
}
