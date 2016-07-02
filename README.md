# BinaryDataParser
A library to generate a parser / serializer for binary data

## Status
THIS PROJECT IS CURRENTLY A PROTOTYPE. Everything that works, works but some of the functionality is missing.
Also please be aware that there maybe some slight changes to the existing API

~~~~
        <dependency>
            <groupId>${project.groupId}</groupId>
            <artifactId>bdp-runtime</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>${project.groupId}</groupId>
            <artifactId>bdp-processor</artifactId>
            <version>${project.version}</version>
            <optional>true</optional>
        </dependency>
~~~~

The code is written in Kotlin but the unit tests are in Java. The software is licensed under the Apache License 2.0


## Goals
Here is the goal for the first relase: *Features still missing are printed cursive*

Generate parser and *serializer* for byte data based an annotations on properties of the class.

For each class with annotations a Parser class will be generated. The Parser *consumes an int array* and sets the
properties according to the definitions made with the annotations.

## Use of the AnnotationsProcessor
The AnnotationsProcessor is in the bdp-runtime JAR file and registers a Service. Just drop it into your classpath!

## Planned Features

* Mapping a signed or unsigned single byte value to an int
* Mapping a signed or unsigned multi byte value to an int or long using big or little endian
* Extracting a signed or unsigned numeric value with a bit mask
* Mapping a single bit to a boolean
* Mapping bits to a enum or constant
* Handling mask bits (bit flags)