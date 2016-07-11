package org.ak80.bdp;

import com.squareup.javapoet.TypeName;
import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.annotations.MappedByte;
import org.ak80.bdp.annotations.MappedWord;
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

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static org.ak80.bdp.testutils.Utils.createMappedByte;
import static org.ak80.bdp.testutils.Utils.createMappedWord;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CoreProcessorTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private MappedClasses mappedClasses = new MappedClasses();

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
  private ArgumentCaptor<MappedClass> byteMappedClassCaptor;

  @Test
  public void process_emptySet_returnsTrue() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    // When
    boolean result = coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(result, is(true));
  }

  @Test
  public void process_containsByteMapped_returnsTrue() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);

    Set<? extends Element> elements = Utils.setOf(element);
    setupMappedByteElementsInRoundEnv(elements);

    // When
    boolean result = coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(result, is(true));
  }

  @Test
  public void process_containsByteMapped_callsGenerator() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);
    Set<? extends Element> elements = Utils.setOf(element);
    setupMappedByteElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(generator).generateFor(any());
  }

  @Test
  public void process_containsByteMappedComplex_callsGeneratorWithMappedByteInfo() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);


    MappedByte mappedByte1 = createMappedByte(0, "name1");
    Element element1 = ElementBuilder.createMappedField("foo1", TypeKind.INT, mappedByte1, CoreProcessor.class);

    MappedByte mappedByte2 = createMappedByte(1, "name2");
    Element element2 = ElementBuilder.createMappedField("foo2", TypeKind.INT, mappedByte2, CoreProcessor.class);

    MappedByte mappedByte3 = createMappedByte(0, "name");
    Element element3 = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte3, ElementBuilder.class);

    Set<? extends Element> elements = Utils.setOf(element1, element2, element3);
    setupMappedByteElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(generator, times(2)).generateFor(byteMappedClassCaptor.capture());
    List<MappedClass> mappedClasses = byteMappedClassCaptor.getAllValues();

    assertThat(mappedClasses, hasSize(2));

    Map<String, MappedClass> mappedBySimpleName = getMapBySimpleName(mappedClasses);

    MappedClass mappedClass = mappedBySimpleName.get(CoreProcessor.class.getSimpleName());
    verifyParent(mappedClass, CoreProcessor.class);
    Map<String, List<MappingInfo>> map = mappedClass.getMappings().stream().collect(Collectors.groupingBy(MappingInfo::getName));

    verifyMapped(map.get("foo1").get(0), "INT", mappedByte1);
    verifyMapped(map.get("foo2").get(0), "INT", mappedByte2);

    mappedClass = mappedBySimpleName.get(ElementBuilder.class.getSimpleName());
    verifyParent(mappedClass, ElementBuilder.class);
    verifyMapped(mappedClass.getMappings().get(0), "INT", mappedByte3);
  }

  @Test
  public void process_containsWordMapped_callsGenerator() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedWord mappedWord = createMappedWord(0, "name", Endian.BIG_ENDIAN);
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedWord, CoreProcessor.class);
    Set<? extends Element> elements = Utils.setOf(element);
    setupMappedWordElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(generator).generateFor(any());
  }

  @Test
  public void process_containsByteMapped_mappedClassesCleared() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);


    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);
    Set<? extends Element> elements = Utils.setOf(element);
    setupMappedByteElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    assertThat(mappedClasses.getClasses(), hasSize(0));
  }

  @Test
  public void process_elementIsClass_errorMessage() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    Element element = ElementBuilder.newElement(ElementKind.CLASS);
    Set<? extends Element> elements = Utils.setOf(element);
    setupMappedByteElementsInRoundEnv(elements);

    // When
    coreProcessor.process(typeElements, roundEnv);

    // Then
    verify(messenger).printMessage(Diagnostic.Kind.ERROR, "Only fields can be annotated with mapping annotation", element);
  }

  @Test
  public void process_containsIncompatibleAnnotations_throwsExceptiob() {
    // Given
    CoreProcessor coreProcessor = new CoreProcessor(mappedClasses, generator);
    coreProcessor.init(processingEnv);

    MappedByte mappedByte = createMappedByte(0, "name");
    Element element = ElementBuilder.createMappedField("foo", TypeKind.INT, mappedByte, CoreProcessor.class);
    MappedWord mappedWord = createMappedWord(1, "name", Endian.BIG_ENDIAN);
    when(element.getAnnotation(MappedWord.class)).thenReturn(mappedWord);

    setupMappedByteElementsInRoundEnv(Utils.setOf(element));

    // Then
    expectedException.expect(IllegalStateException.class);

    // When
    coreProcessor.process(typeElements, roundEnv);
  }

  private void setupMappedByteElementsInRoundEnv(Set<? extends Element> elements) {
    doReturn(elements).when(roundEnv).getElementsAnnotatedWith(MappedByte.class);
  }

  private void setupMappedWordElementsInRoundEnv(Set<? extends Element> elements) {
    doReturn(elements).when(roundEnv).getElementsAnnotatedWith(MappedWord.class);
  }

  private Map<String, MappedClass> getMapBySimpleName(List<MappedClass> mappedClasses) {
    Map<String, MappedClass> map = new HashMap<>();
    for (MappedClass mappedClass : mappedClasses) {
      map.put(mappedClass.getSimpleName(), mappedClass);
    }
    return map;
  }

  private void verifyMapped(MappingInfo mappingInfo, String type, Annotation annotation) {
    assertThat("Mapping Type", mappingInfo.getType(), is(type));
    assertThat("Mapping Info", mappingInfo.getAnnotation(), is(annotation));
  }

  private void verifyParent(MappedClass mappedClass, Class parentClass) {
    assertThat("SimpleName", mappedClass.getSimpleName(), is(parentClass.getSimpleName()));
    assertThat("QualifiedName", mappedClass.getQualifiedName(), is(parentClass.getName()));
    assertThat("PackageName", mappedClass.getPackageName(), is(parentClass.getPackage().getName()));
    assertThat(TypeName.get(mappedClass.getClassType()).toString(), is(parentClass.getName()));
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
