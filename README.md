# BinaryDataParser [![Travis master](https://img.shields.io/travis/ak80/BinaryDataParser/master.svg?maxAge=3600)](https://travis-ci.org/ak80/BinaryDataParser) [![Coverage Status](https://coveralls.io/repos/github/ak80/BinaryDataParser/badge.svg?maxAge=3600)](https://coveralls.io/github/ak80/BinaryDataParser?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/34e46b5a77694ea2a11227b915235218)](https://www.codacy.com/app/josef-koch/BinaryDataParser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ak80/BinaryDataParser&amp;utm_campaign=Badge_Grade) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.ak80.bdp/BinaryDataParser/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/org.ak80.bdp/BinaryDataParser/) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

BinaryDataParser helps to parse and serialize binary data into POJOs. It generates a custom
mapper (combined parser/serializer) for each class. The mapping for a property is
defined with an annotation.

The code is written in Kotlin but the unit tests are in Java. The software is
licensed under the Apache License 2.0

## Quickstart

You need to add a runtime and a compile time dependency and then you can add the annotations to
the class you want to map. When you compile, a custom mapper will be generated and can then be
used from you code.

### Add dependency

Add this dependency to you project

~~~~
        <dependency>
            <groupId>org.ak80.bdp</groupId>
            <artifactId>bdp-runtime</artifactId>
            <version>1.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.ak80.bdp</groupId>
            <artifactId>bdp-processor</artifactId>
            <version>1.0.1</version>
            <optional>true</optional>
        </dependency>
~~~~

### Add annotation to you class

```java

public class ClassToMap {

  @MappedByte(index = 0)
  private int byte0;

  @MappedByte(index = 1)
  private int byte1;

  @MappedWord(index = 2, endianess = Endian.BIG_ENDIAN)
  private int wordBig;

  @MappedWord(index = 4, endianess = Endian.LITTLE_ENDIAN)
  private int wordLittle;


  public int getByte0() {
    return byte0;
  }

  public void setByte0(int byte0) {
    this.byte0 = byte0;
  }

  public int getByte1() {
    return byte1;
  }

  public void setByte1(int byte1) {
    this.byte1 = byte1;
  }

  public int getWordBig() {
    return wordBig;
  }

  public void setWordBig(int wordBig) {
    this.wordBig = wordBig;
  }

  public int getWordLittle() {
    return wordLittle;
  }

  public void setWordLittle(int wordLittle) {
    this.wordLittle = wordLittle;
  }
}

```

### Use generated mapper

```java

public class ClassToMapTest {

  @Test
  public void testParse() {
    // Given
    ClassToMap classToMap = new ClassToMap();
    ClassToMapParser parser = new ClassToMapParser();

    int[] data = new int[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

    // When
    parser.parse(classToMap, data);

    // Then
    assertThat(classToMap.getByte0(), is(0x01));
    assertThat(classToMap.getByte1(), is(0x02));
    assertThat(classToMap.getWordBig(), is(0x0304));
    assertThat(classToMap.getWordLittle(), is(0x0605));
  }

  @Test
  public void testSerialize() {
    // Given
    ClassToMap classToMap = new ClassToMap();
    ClassToMapParser parser = new ClassToMapParser();

    classToMap.setByte0(0x01);
    classToMap.setByte1(0x02);
    classToMap.setWordBig(0x0304);
    classToMap.setWordLittle(0x0506);

    int[] data = new int[6];

    // When
    parser.serialize(classToMap, data);

    // Then
    assertThat(data, is(new int[]{0x01, 0x02, 0x03, 0x04, 0x06, 0x05}));
  }

}
```

## Purpose

Generate parser and serializer for byte data based an annotations on properties of the class.

For each class with annotations a Parser-Serializer class will be generated. When parsing it consumes an int array and sets the
properties according to the definitions made with the annotations. When serializing it produces an int array based on the properties of the given object.

## Use of the AnnotationsProcessor
The AnnotationsProcessor is in the bdp-runtime JAR file and registers a Service. Just drop it into your classpath!


## Supported mappings

### Mapping a single byte value

A single byte value is mapped to / from a numeric with the *@MappedByte* annotation:

```java

  @MappedByte(index = 0)
  private int byte0;

```
The parameter *index* defined the position of the byte in the array.

### Mapping a two byte value

A two byte value is mapped to / from a numeric with the *@MappedWord* annotation:

```java

  @MappedWord(index = 0)
  private int wordBig;

  @MappedWord(index = 2, endianess = Endian.LITTLE_ENDIAN)
  private int wordLittle;

```

The parameter *index* defined the position of the word in the array. The parameter *endianess* defined
the byte order, either *Endian.BIG_ENDIAN*, which is the default, or *Endian.LITTLE_ENDIAN*.

### Mapping a single bit to a boolean

** only supported in snapshot, please see in the tests or wait for the next release **

### Mapping a group of bits to an enum

** only supported in snapshot, please see in the tests or wait for the next release **

## Planned Features

* Mapping one or more bits to a numeric
* Mapping one or more bits to an enum, with a builder class
* Handling bit flags for a group of booleans
* Create a new array when serializing
* Generate new instance instead of need to pass an existing instance
* Generate documentation
* Sanity checking for overlapping definitions
* Sanity checking for unmapped parts
