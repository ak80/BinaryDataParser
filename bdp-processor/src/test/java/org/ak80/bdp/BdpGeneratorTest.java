package org.ak80.bdp;


import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.testutils.ElementBuilder;
import org.ak80.bdp.testutils.Utils;
import org.jetbrains.annotations.NotNull;
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
  public void build_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedByte(1, "name1"));
    mappedClass.addMapping("field2", "Foo", Utils.createMappedWord(3, "name2", Endian.BIG_ENDIAN));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec().methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod);
    assertThat(parseMethod.code.toString(), is(""
        + "simpleName.setField1(data[1]);\n"
        + "simpleName.setField2((data[3] << BYTE_LENGTH) + data[4]);\n"
    ));
  }

  @Test
  public void mappedByte_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedByte(1, "name1"));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec().methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod);
    assertThat(parseMethod.code.toString(), is("simpleName.setField1(data[1]);\n"));
  }


  @Test
  public void mappedBigEndianWord_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedWord(1, "name1", Endian.BIG_ENDIAN));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec().methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod);
    assertThat(parseMethod.code.toString(), is("simpleName.setField1((data[1] << BYTE_LENGTH) + data[2]);\n"));
  }

  @Test
  public void mappedLittleEndianWord_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedWord(1, "name1", Endian.LITTLE_ENDIAN));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec().methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod);
    assertThat(parseMethod.code.toString(), is("simpleName.setField1((data[2] << BYTE_LENGTH) + data[1]);\n"));
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

  private void verifyMethodSignature(MethodSpec parseMethod) {
    assertThat(parseMethod.name, is("parse"));
    assertThat(parseMethod.parameters.get(0).toString(), is("java.lang.String simpleName"));
    assertThat(parseMethod.parameters.get(1).toString(), is("int[] data"));
  }


}
