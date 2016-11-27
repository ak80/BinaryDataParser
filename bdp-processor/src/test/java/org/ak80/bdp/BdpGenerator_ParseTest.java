package org.ak80.bdp;


import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.testutils.ElementBuilder;
import org.ak80.bdp.testutils.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.ak80.bdp.testutils.Verificator.getTypeSpec;
import static org.ak80.bdp.testutils.Verificator.verifyMethodSignature;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BdpGenerator_ParseTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String packageName = "org.ak80.bdp";
  private static final String className = "SimpleName";

  private static final int METHOD_INDEX_PARSE = 0;

  @Mock
  private FileWriter fileWriter;

  @Captor
  private ArgumentCaptor<TypeSpec.Builder> typeSpecBuilderCaptor;

  private TypeMirror type = ElementBuilder.createTypeMirror(TypeKind.DECLARED, String.class);
  private MappedClass mappedClass = new MappedClass(className, packageName, type);

  @Test
  public void build_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedByte(1, "name1"));
    mappedClass.addMapping("field2", "Foo", Utils.createMappedWord(3, "name2", Endian.BIG_ENDIAN));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec(fileWriter, typeSpecBuilderCaptor).methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod, bdpGenerator.getParseMethodPrefix());
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
    MethodSpec parseMethod = getTypeSpec(fileWriter, typeSpecBuilderCaptor).methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod, bdpGenerator.getParseMethodPrefix());
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
    MethodSpec parseMethod = getTypeSpec(fileWriter, typeSpecBuilderCaptor).methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod, bdpGenerator.getParseMethodPrefix());
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
    MethodSpec parseMethod = getTypeSpec(fileWriter, typeSpecBuilderCaptor).methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod, bdpGenerator.getParseMethodPrefix());
    assertThat(parseMethod.code.toString(), is("simpleName.setField1((data[2] << BYTE_LENGTH) + data[1]);\n"));
  }

  @Test
  public void mappedFlag_parserMethod_withBuilder() {
    // Given
    BdpGenerator bdpGenerator = new BdpGenerator(fileWriter);
    mappedClass.addMapping("field1", "Foo", Utils.createMappedFlag(1, Bit.BIT_3, "name1"));

    // When
    bdpGenerator.generateFor(mappedClass);

    // Then
    MethodSpec parseMethod = getTypeSpec(fileWriter, typeSpecBuilderCaptor).methodSpecs.get(METHOD_INDEX_PARSE);
    verifyMethodSignature(parseMethod, bdpGenerator.getParseMethodPrefix());
    assertThat(parseMethod.code.toString(), is("simpleName.setField1((data[1] & BIT_3.getMask()) == BIT_3.getMask());\n"));
  }

}
