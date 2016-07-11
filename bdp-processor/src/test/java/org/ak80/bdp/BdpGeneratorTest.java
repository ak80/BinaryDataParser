package org.ak80.bdp;


import com.squareup.javapoet.TypeSpec;
import org.ak80.bdp.testutils.ElementBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BdpGeneratorTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String packageName = "org.ak80.bdp";
  private static final String className = "SimpleName";
  private static final String parserClassName = className + "Parser";

  private static final int METHOD_INDEX_PARSE = 0;

  @Mock
  private FileWriter fileWriter;

  @Captor
  private ArgumentCaptor<TypeSpec.Builder> typeSpecBuilderCaptor;

  private TypeMirror type = ElementBuilder.createTypeMirror(TypeKind.DECLARED, String.class);
  private MappedClass mappedClass = new MappedClass(className, packageName, type);


  @Test
  public void writeFile_generate_useFileWriter() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    verify(fileWriter).write(eq(packageName), any());
  }

  @Test
  public void build_className_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    TypeSpec typeSpec = getTypeSpec();
    assertThat(typeSpec.name, is(parserClassName));
    assertThat(typeSpec.modifiers, hasItems(Modifier.PUBLIC));
  }

  @Test
  public void build_unknownAnnotation_throwsException() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", mock(Annotation.class));

    // Then
    expectedException.expect(IllegalStateException.class);

    // When
    bdpGenerator.generateFor(mappedClass);
  }

  private TypeSpec getTypeSpec() {
    verify(fileWriter).write(any(), typeSpecBuilderCaptor.capture());
    TypeSpec.Builder builder = typeSpecBuilderCaptor.getValue();
    return builder.build();
  }

}
