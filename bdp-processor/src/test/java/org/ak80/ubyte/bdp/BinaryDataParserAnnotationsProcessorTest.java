package org.ak80.ubyte.bdp;

import org.ak80.ubyte.bdp.annotations.MappedByte;
import org.ak80.ubyte.bdp.processor.BdpProcessor;
import org.ak80.ubyte.bdp.processor.CoreProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BinaryDataParserAnnotationsProcessorTest {

  @Mock
  private BdpProcessor bdpProcessor;

  @Mock
  private ProcessingEnvironment processingEnv;

  @Mock
  private RoundEnvironment roundEnvironment;

  @Test
  public void coreProcessor_whenCreate_theCoreProcessorIsSet() {
    // When
    BinaryDataParserAnnotationsProcessor processor = new BinaryDataParserAnnotationsProcessor();

    // Then
    assertThat(processor.bdpProcessor, is(not(nullValue())));
    assertThat(processor.bdpProcessor, is(instanceOf(CoreProcessor.class)));
  }

  @Test
  public void init_whenInitWithProcessingEnv_initCoreProcessor() {
    // Given
    BinaryDataParserAnnotationsProcessor processor = new BinaryDataParserAnnotationsProcessor();
    processor.bdpProcessor = bdpProcessor;

    // When
    processor.init(processingEnv);

    // Then
    verify(bdpProcessor).init(processingEnv);
  }

  @Test
  public void annotations_getSupported_containsAnnotations() {
    // Given
    BinaryDataParserAnnotationsProcessor processor = new BinaryDataParserAnnotationsProcessor();

    // When
    Set<String> annotationTypes = processor.getSupportedAnnotationTypes();

    // Then
    assertThat(annotationTypes, hasItems(MappedByte.class.getName()));
  }

  @Test
  public void javaVersion_getSupported_isJava8() {
    // Given
    BinaryDataParserAnnotationsProcessor processor = new BinaryDataParserAnnotationsProcessor();

    // When
    SourceVersion sourceVersion = processor.getSupportedSourceVersion();

    // Then
    assertThat(sourceVersion, is(SourceVersion.RELEASE_8));
  }

  @Test
  public void process_whenCalled_delegate() {
    // Given
    BinaryDataParserAnnotationsProcessor processor = new BinaryDataParserAnnotationsProcessor();
    processor.bdpProcessor = bdpProcessor;
    Set<TypeElement> typeElements = Collections.emptySet();

    // When
    processor.process(typeElements, roundEnvironment);

    // Then
    verify(bdpProcessor).process(typeElements, roundEnvironment);
  }
}