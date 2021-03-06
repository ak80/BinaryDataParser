package org.ak80.bdp.testutils;

import com.squareup.javapoet.ClassName;
import org.ak80.bdp.annotations.MappedByte;
import org.ak80.bdp.annotations.MappedEnum;
import org.ak80.bdp.annotations.MappedFlag;
import org.ak80.bdp.annotations.MappedWord;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.mockito.Mockito.*;

public class ElementBuilder {

  public static Element createMappedField(String fieldName, TypeKind fieldType, MappedByte annotation, Class parentClass) {
    Element element = getElement(fieldName, fieldType, parentClass);
    when(element.getAnnotation(MappedByte.class)).thenReturn(annotation);
    return element;
  }

  public static Element createMappedField(String fieldName, TypeKind fieldType, MappedWord annotation, Class parentClass) {
    Element element = getElement(fieldName, fieldType, parentClass);
    when(element.getAnnotation(MappedWord.class)).thenReturn(annotation);
    return element;
  }

  public static Element createMappedField(String fieldName, TypeKind fieldType, MappedFlag annotation, Class parentClass) {
    Element element = getElement(fieldName, fieldType, parentClass);
    when(element.getAnnotation(MappedFlag.class)).thenReturn(annotation);
    return element;
  }

  public static Element createMappedField(String fieldName, TypeKind fieldType, MappedEnum annotation, Class parentClass) {
    Element element = getElement(fieldName, fieldType, parentClass);
    when(element.getAnnotation(MappedEnum.class)).thenReturn(annotation);
    return element;
  }

  private static Element getElement(String fieldName, TypeKind fieldType, Class parentClass) {
    Element parent = newElement(ElementKind.CLASS);
    when(parent.getSimpleName()).thenReturn(createName(parentClass.getSimpleName()));
    when(parent.asType()).thenReturn(createTypeMirror(TypeKind.DECLARED, parentClass));
    when(((TypeElement) parent).getQualifiedName()).thenReturn(createName(parentClass.getName()));

    Element element = newElement(ElementKind.FIELD);
    when(element.getEnclosingElement()).thenReturn(parent);
    when(element.getSimpleName()).thenReturn(createName(fieldName));
    when(element.asType()).thenReturn(createTypeMirror(fieldType, null));
    return element;
  }


  public static Element newElement(ElementKind kind) {
    Element element = mock(Element.class, withSettings().extraInterfaces(TypeElement.class));
    when(element.getKind()).thenReturn(kind);
    return element;
  }

  private static Name createName(String name) {

    return new Name() {
      @Override
      public boolean contentEquals(CharSequence cs) {
        return cs.equals(name);
      }

      @Override
      public int length() {
        return name.length();
      }

      @Override
      public char charAt(int index) {
        return name.charAt(index);
      }

      @Override
      public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
      }

      @Override
      public String toString() {
        return name;
      }
    };
  }

  public static TypeMirror createTypeMirror(TypeKind kind, Class clazz) {
    return new TestingTypeElement(kind, clazz);
  }

  private static class TestingTypeElement implements TypeMirror {

    private final TypeKind kind;
    private final Class clazz;

    public TestingTypeElement(TypeKind kind, Class clazz) {
      this.kind = kind;
      this.clazz = clazz;
    }

    @Override
    public TypeKind getKind() {
      return kind;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
      return (R) ClassName.get(clazz);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
      return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
      return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
      return null;
    }
  }

}

