package org.ak80.ubyte.bdp.processor;

import com.squareup.javapoet.TypeName;
import org.ak80.ubyte.bdp.Utils;
import org.ak80.ubyte.bdp.annotations.MappedByte;
import org.ak80.ubyte.bdp.generator.Generator;
import org.ak80.ubyte.bdp.model.ByteMappedClass;
import org.ak80.ubyte.bdp.model.ByteMappedClasses;
import org.ak80.ubyte.bdp.model.ByteMappingInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CoreProcessorTest {


  private ByteMappedClasses byteMappedClasses = new ByteMappedClasses();

  @Mock
  private Generator generator;

  @Mock
  private Messager messenger;

  @Mock
  private Filer filer;

  @Mock
  private Elements elements;

  @Mock
  private Types typeUtils;

  private ProcessingEnvironment processingEnv = new TestingProcessingEnvironment();

  private Set<TypeElement> typeElements = Collections.emptySet();

  @Mock
  private RoundEnvironment roundEnv;

  @Captor
  private ArgumentCaptor<ByteMappedClass> byteMappedClassCaptor;

  @Test
  public void process_emptySet_returnsTrue() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);

    // When
    boolean result = coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(result, is(true));
  }

  @Test
  public void process_containsByteMapped_returnsTrue() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);

    Set<? extends Element> elements = Utils.setOf(element);
    setupElementsInRoundEnv(elements);

    // When
    boolean result = coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(result, is(true));
  }


  @Test
  public void process_containsByteMapped_callsGenerator() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);
    Set<? extends Element> elements = Utils.setOf(element);
    setupElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(generator).generateFor(any(), eq(filer));
  }

  @Test
  public void process_containsByteMappedComplex_callsGeneratorWithMappedByteInfo() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);


    MappedByte mappedByte1 = createMappedByte(0, "name1");
    Element element1 = ElementBuilder.createMappedField("foo1", TypeKind.INT, mappedByte1, CoreProcessor.class);

    MappedByte mappedByte2 = createMappedByte(1, "name2");
    Element element2 = ElementBuilder.createMappedField("foo2", TypeKind.INT, mappedByte2, CoreProcessor.class);

    MappedByte mappedByte3 = createMappedByte(0, "name");
    Element element3 = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte3, ElementBuilder.class);

    Set<? extends Element> elements = Utils.setOf(element1, element2, element3);
    setupElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(generator, times(2)).generateFor(byteMappedClassCaptor.capture(), eq(filer));
    List<ByteMappedClass> byteMappedClasses = byteMappedClassCaptor.getAllValues();

    assertThat(byteMappedClasses, hasSize(2));

    ByteMappedClass byteMappedClass = byteMappedClasses.get(0);
    verifyParent(byteMappedClass, CoreProcessor.class);
    Map<String, List<ByteMappingInfo>> map = byteMappedClass.getMappings().stream().collect(Collectors.groupingBy(ByteMappingInfo::getName));

    verifyMappedByte(map.get("foo1").get(0), "INT", mappedByte1);
    verifyMappedByte(map.get("foo2").get(0), "INT", mappedByte2);

    byteMappedClass = byteMappedClasses.get(1);
    verifyParent(byteMappedClass, ElementBuilder.class);
    verifyMappedByte(byteMappedClass.getMappings().get(0), "INT", mappedByte3);
  }

  private void verifyMappedByte(ByteMappingInfo byteMappingInfo, String type, MappedByte mappedByte) {
    assertThat("ByteMapping Type", byteMappingInfo.getType(), is(type));
    assertThat("ByteMapping Info", byteMappingInfo.getMappedByte(), is(mappedByte));
  }

  private void verifyParent(ByteMappedClass byteMappedClass, Class parentClass) {
    assertThat("SimpleName", byteMappedClass.getSimpleName(), is(parentClass.getSimpleName()));
    assertThat("QualifiedName", byteMappedClass.getQualifiedName(), is(parentClass.getName()));
    assertThat("PackageName", byteMappedClass.getPackageName(), is(parentClass.getPackage().getName()));
    assertThat(TypeName.get(byteMappedClass.getParentType()).toString(),is(parentClass.getName()));
  }

  @Test
  public void process_containsByteMapped_mappedClassesCleared() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);


    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);
    Set<? extends Element> elements = Utils.setOf(element);
    setupElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(byteMappedClasses.getClasses(), hasSize(0));
  }

  private MappedByte createMappedByte(int index, String name) {
    MappedByte mappedByte = mock(MappedByte.class);
    when(mappedByte.index()).thenReturn(index);
    when(mappedByte.name()).thenReturn(name);
    return mappedByte;
  }

  @Test
  public void process_elementIsClass_errorMessage() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(byteMappedClasses, generator);
    coreProcessor.init(processingEnv);

    Element element = ElementBuilder.newElement(ElementKind.CLASS);
    Set<? extends Element> elements = Utils.setOf(element);
    setupElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(messenger).printMessage(Diagnostic.Kind.ERROR, "Only fields can be annotated with @MappedByte", element);
  }

  private void setupElementsInRoundEnv(Set<? extends Element> elements) {
    doReturn(elements).when(roundEnv).getElementsAnnotatedWith(MappedByte.class);
  }

  private class TestingProcessingEnvironment implements ProcessingEnvironment {

    @Override
    public Map<String, String> getOptions() {
      return Collections.emptyMap();
    }

    @Override
    public Messager getMessager() {
      return messenger;
    }

    @Override
    public Filer getFiler() {
      return filer;
    }

    @Override
    public Elements getElementUtils() {
      return elements;
    }

    @Override
    public Types getTypeUtils() {
      return typeUtils;
    }

    @Override
    public SourceVersion getSourceVersion() {
      return SourceVersion.RELEASE_8;
    }

    @Override
    public Locale getLocale() {
      return Locale.ENGLISH;
    }
  }


}
