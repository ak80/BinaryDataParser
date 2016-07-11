package org.ak80.bdp.testutils;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.ak80.bdp.FileWriter;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class Verificator {

  public static TypeSpec getTypeSpec(FileWriter fileWriter, ArgumentCaptor<TypeSpec.Builder> typeSpecBuilderCaptor) {
    verify(fileWriter).write(any(), typeSpecBuilderCaptor.capture());
    TypeSpec.Builder builder = typeSpecBuilderCaptor.getValue();
    return builder.build();
  }

  public static void verifyMethodSignature(MethodSpec parseMethod, String name) {
    assertThat(parseMethod.name, is(name));
    assertThat(parseMethod.parameters.get(0).toString(), is("java.lang.String simpleName"));
    assertThat(parseMethod.parameters.get(1).toString(), is("int[] data"));
  }

}
