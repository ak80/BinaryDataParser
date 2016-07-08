package org.ak80.ubyte.bdp;

import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.ak80.ubyte.bdp.BdpFileWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;


//FIXME review this

@RunWith(MockitoJUnitRunner.class)
public class BdpFileWriterTest {

  private static final String className = "ClassName";
  private static final String packageName = "org.ak80.ubyte.bdp";

  private Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);

  @Mock
  private Filer filer;

  @Mock
  private JavaFileObject javaFileObject;

  @Mock
  private java.io.Writer writer;

  @Captor
  private ArgumentCaptor<String> stringCaptor;

  @Test
  public void write_javaFile_writesStrings() throws IOException {
    // Given
    BdpFileWriter fileWriter = new BdpFileWriter();
    fileWriter.init(filer);
    when(filer.createSourceFile(any(), anyVararg())).thenReturn(javaFileObject);
    when(javaFileObject.openWriter()).thenReturn(writer);

    // When
    fileWriter.write(packageName, builder);

    // Then
    verify(writer, times(13)).append(stringCaptor.capture());

    List<String> stringList = stringCaptor.getAllValues();
    assertThat(stringList.get(0), is("package "));
    assertThat(stringList.get(1), is(packageName));
    assertThat(stringList.get(2), is(";"));
    assertThat(stringList.get(3), is("import static "));
    assertThat(stringList.get(4), is("org.ak80.ubyte.bdp.BinaryUtils.*"));
    assertThat(stringList.get(5), is(";"));
    assertThat(stringList.get(6), is("public"));
    assertThat(stringList.get(7), is(" "));
    assertThat(stringList.get(8), is("class"));
    assertThat(stringList.get(9), is(" "));
    assertThat(stringList.get(10), is(className));
    assertThat(stringList.get(11), is(" {"));
    assertThat(stringList.get(12), is("}"));
  }


}
