package org.ak80.ubyte.bdp.generator;


import com.squareup.javapoet.TypeSpec;
import org.ak80.ubyte.bdp.ElementBuilder;
import org.ak80.ubyte.bdp.model.ByteMappedClass;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.ak80.ubyte.bdp.Utils.createMappedByte;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BdpGeneratorTest {

  private static final String packageName = "org.ak80.ubyte.bdp";
  private static final String className = "SimpleName";
  private static final String parserClassName = className + "Parser";

  @Mock
  private FileWriter fileWriter;

  @Captor
  private ArgumentCaptor<TypeSpec.Builder> typeSpecBuilderCaptor;

  private TypeMirror type = ElementBuilder.createTypeMirror(TypeKind.DECLARED, String.class);
  private ByteMappedClass byteMappedClass = new ByteMappedClass(className, packageName, type);


  @Test
  public void writeFile_generate_useFileWriter() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(byteMappedClass);

    // Then
    verify(fileWriter).write(eq(packageName), any());
  }

  @Test
  public void build_className_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(byteMappedClass);

    // Then
    TypeSpec typeSpec = getTypeSpec();
    assertThat(typeSpec.name, is(parserClassName));
    assertThat(typeSpec.modifiers, hasItems(Modifier.PUBLIC));
  }

  @Test
  public void build_instanceFiled_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(byteMappedClass);

    // Then
    TypeSpec typeSpec = getTypeSpec();
    assertThat(typeSpec.fieldSpecs.get(0).type.toString(), is("java.lang.String"));
    assertThat(typeSpec.fieldSpecs.get(0).name, is("simpleName"));
  }

  @Test
  public void build_constructor_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);

    // When
    bdpGenerator.generateFor(byteMappedClass);

    // Then
    TypeSpec typeSpec = getTypeSpec();
    assertThat(typeSpec.methodSpecs.get(0).isConstructor(), is(true));
    assertThat(typeSpec.methodSpecs.get(0).parameters.get(0).toString(), is("java.lang.String simpleName"));
    assertThat(typeSpec.methodSpecs.get(0).code.toString(), is("this.simpleName = simpleName;"));
  }

  @Test
  public void build_byteMapping_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    byteMappedClass.addByteMapping("foo", "Foo", createMappedByte(1, "name"));

    // When
    bdpGenerator.generateFor(byteMappedClass);

    // Then
    TypeSpec typeSpec = getTypeSpec();
    assertThat(typeSpec.methodSpecs.get(1).isConstructor(), is(false));
    assertThat(typeSpec.methodSpecs.get(1).parameters.get(0).toString(), is("int value"));
    assertThat(typeSpec.methodSpecs.get(1).code.toString(), is("// Foo 1 name\nsimpleName.setFoo(value);\n"));
  }

  @NotNull
  private TypeSpec getTypeSpec() {
    verify(fileWriter).write(any(), typeSpecBuilderCaptor.capture());
    TypeSpec.Builder builder = typeSpecBuilderCaptor.getValue();
    return builder.build();
  }


}
