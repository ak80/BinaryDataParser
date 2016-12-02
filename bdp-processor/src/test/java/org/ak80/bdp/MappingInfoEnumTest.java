package org.ak80.bdp;

import org.ak80.bdp.annotations.MappedEnum;
import org.junit.Test;

import static org.ak80.bdp.testutils.Utils.createMappedEnum;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappingInfoEnumTest {

  @Test
  public void getMethodBodyGetter_produceMethodBody() {
    // Given
    MappedEnum anEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", anEnum);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = getFoo().mapTo();\n"));
  }

  @Test
  public void getMethodBod_Setter_produceMethodBody() {
    // Given
    MappedEnum anEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", anEnum);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo(MappedEnum.mapFrom(data[1] & 60));\n"));
  }

}
