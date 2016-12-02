package org.ak80.bdp;

import org.ak80.bdp.annotations.MappedFlag;
import org.junit.Test;

import static org.ak80.bdp.testutils.Utils.createMappedFlag;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MappingInfoFlagTest {

  @Test
  public void getMethodBodyGetter_produceMethodBody() {
    // Given
    MappedFlag mappedFlag = createMappedFlag(1, Bit.BIT_0, "name");
    MappingInfo mappingInfo = new MappingInfoFlag("field", "int", mappedFlag);

    // When
    String body = mappingInfo.getMethodBodyGetter("getFoo");

    // Then
    assertThat(body, is("if(isFoo()) { data[1] = data[1] | BIT_0.getMask(); } else { data[1] = data[1] & ~BIT_0.getMask(); }\n"));
  }

  @Test
  public void getMethodBod_Setter_produceMethodBody() {
    // Given
    MappedFlag mappedFlag = createMappedFlag(1, Bit.BIT_0, "name");
    MappingInfo mappingInfo = new MappingInfoFlag("field", "int", mappedFlag);

    // When
    String body = mappingInfo.getMethodBodySetter("setFoo");

    // Then
    assertThat(body, is("setFoo((data[1] & BIT_0.getMask()) == BIT_0.getMask());\n"));
  }
}
