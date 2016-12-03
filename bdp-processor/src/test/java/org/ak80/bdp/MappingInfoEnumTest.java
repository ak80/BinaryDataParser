package org.ak80.bdp;

import org.ak80.bdp.annotations.MappedEnum;
import org.junit.Test;

import static org.ak80.bdp.testutils.Utils.createMappedEnum;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class MappingInfoEnumTest {

  @Test
  public void getMethodBodyGetter_produceMethodBody() {
    // Given
    MappedEnum mappedEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", mappedEnum);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = data[1] | ( (getFoo().mapTo() << 2 ) );\n"));
  }

  @Test
  public void getMethodBodySetter_produceMethodBody() {
    // Given
    MappedEnum mappedEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", mappedEnum);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo(MappedEnum.mapFrom((data[1] & 60) >>> 2));\n"));
  }

  @Test
  public void getMethodBodyGetter_customMapTo_produceMethodBody() {
    // Given
    MappedEnum mappedEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    when(mappedEnum.mapTo()).thenReturn("custom");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", mappedEnum);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("data[1] = data[1] | ( (getFoo().custom() << 2 ) );\n"));
  }

  @Test
  public void getMethodBodySetter_customMapFrom_produceMethodBody() {
    // Given
    MappedEnum mappedEnum = createMappedEnum(1, Bit.BIT_5, Bit.BIT_2, "name");
    when(mappedEnum.mapFrom()).thenReturn("custom");
    MappingInfo mappingInfo = new MappingInfoEnum("field", "MappedEnum", mappedEnum);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo(MappedEnum.custom((data[1] & 60) >>> 2));\n"));
  }


}
