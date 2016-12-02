# BinaryDataParser [![Travis master](https://img.shields.io/travis/ak80/BinaryDataParser/master.svg?maxAge=3600)](https://travis-ci.org/ak80/BinaryDataParser) [![Coverage Status](https://coveralls.io/repos/github/ak80/BinaryDataParser/badge.svg?maxAge=3600)](https://coveralls.io/github/ak80/BinaryDataParser?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/34e46b5a77694ea2a11227b915235218)](https://www.codacy.com/app/josef-koch/BinaryDataParser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ak80/BinaryDataParser&amp;utm_campaign=Badge_Grade) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.ak80.bdp/BinaryDataParser/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/org.ak80.bdp/BinaryDataParser/) [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

BinaryDataParser helps to parse and serialize binary data into POJOs. It generates custom parsers from annotations. It works by defining mappings on fields in an object with annotations.

## Status
THIS PROJECT IS CURRENTLY A PROTOTYPE. 

Everything that works, works but some of the functionality is missing.
Also please be aware that there maybe some slight changes to the existing API

~~~~
        <dependency>
            <groupId>org.ak80.bdp</groupId>
            <artifactId>bdp-runtime</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.ak80.bdp</groupId>
            <artifactId>bdp-processor</artifactId>
            <version>${project.version}</version>
            <optional>true</optional>
        </dependency>
~~~~

The code is written in Kotlin but the unit tests are in Java. The software is licensed under the Apache License 2.0


## Goals
Here is the goal for the first relase: *Features still missing are printed cursive*

Generate parser and serializer for byte data based an annotations on properties of the class.

For each class with annotations a Parser-Serializer class will be generated. When parsing it consumes an int array and sets the
properties according to the definitions made with the annotations. When serializing it produces an int array based on the properties of the given object.

## Use of the AnnotationsProcessor
The AnnotationsProcessor is in the bdp-runtime JAR file and registers a Service. Just drop it into your classpath!


## Implemented Features
* Mapping a signed or unsigned single byte value to an int
* Mapping a signed or unsigned multi byte value to an int or long using big or little endian
* Mapping a single bit to a boolean
* Mapping one or more bits to an enum, with utility methods defined on the enum

## Planned Features

* Extracting a signed or unsigned numeric value with a bit mask
* Mapping one or more bits to an enum, with a builder class
* Handling mask bits (bit flags)
* Mapping into an existing array
