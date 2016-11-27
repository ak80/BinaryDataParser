package org.ak80.bdp

import com.squareup.javapoet.*
import org.ak80.bdp.annotations.Endian
import org.ak80.bdp.annotations.MappedByte
import org.ak80.bdp.annotations.MappedFlag
import org.ak80.bdp.annotations.MappedWord
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * Used to generate a source file for one byte mapped class
 */
interface Generator {

    fun generateFor(mappedClass: MappedClass)
}

/**
 * Generates source files
 */
class BdpGenerator(private var fileWriter: FileWriter) : Generator {

    val classSuffix = "Parser"
    val parseMethodPrefix = "parse"
    val serializeMethodPrefix = "serialize"

    private val mappingGenerator = MappingGenerator()

    private var instanceField: String = ""

    override fun generateFor(mappedClass: MappedClass) {
        var parserClassName = mappedClass.simpleName + classSuffix
        instanceField = mappedClass.simpleName.decapitalize()

        var parseMethodCode = StringBuilder()
        var serializeMethodCode = StringBuilder()

        for (mappingInfo in mappedClass.mappings) {
            parseMethodCode.append(createParseMapping(mappingInfo))
            serializeMethodCode.append(createSerializeMapping(mappingInfo))
        }

        var builder = createBuilder(parserClassName)

                .addMethod(MethodSpec.methodBuilder(parseMethodPrefix)
                        .addParameter(TypeName.get(mappedClass.classType), instanceField)
                        .addParameter(ArrayTypeName.of(TypeName.INT), "data")
                        .addCode(parseMethodCode.toString())
                        .build())
                .addMethod(MethodSpec.methodBuilder(serializeMethodPrefix)
                        .addParameter(TypeName.get(mappedClass.classType), instanceField)
                        .addParameter(ArrayTypeName.of(TypeName.INT), "data")
                        .addCode(serializeMethodCode.toString())
                        .build())

        fileWriter.write(mappedClass.packageName, builder)
    }

    private fun createBuilder(className: String) = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)


    private fun createParseMapping(mappingInfo: MappingInfo): String {
        var setterName = getSetterName(mappingInfo)
        return mappingGenerator.getParseCode(mappingInfo, setterName)
    }

    private fun createSerializeMapping(mappingInfo: MappingInfo): String {
        var getterName = getGetterName(mappingInfo)
        return mappingGenerator.getSerializeCode(mappingInfo, getterName)
    }


    private fun getSetterName(mappingInfo: MappingInfo) = "$instanceField.set${mappingInfo.name.capitalize()}"

    private fun getGetterName(mappingInfo: MappingInfo) = "$instanceField.get${mappingInfo.name.capitalize()}"

}

private class MappingGenerator() {

    fun getParseCode(mappingInfo: MappingInfo, setterName: String): String {
        when (mappingInfo.annotation) {
            is MappedByte -> return getParseByteMapping(mappingInfo.annotation, setterName)
            is MappedWord -> return getParseWordMapping(mappingInfo.annotation, setterName)
            is MappedFlag -> return getParseFlagMapping(mappingInfo.annotation, setterName)
            else -> throw IllegalStateException("Mapping ${mappingInfo.annotation.javaClass.name} is not known")
        }
    }

    fun getParseByteMapping(mappingInfo: MappedByte, setterName: String): String {
        return "$setterName(data[${mappingInfo.index}]);\n"
    }

    fun getParseWordMapping(mappingInfo: MappedWord, setterName: String): String {
        var code: String
        if (mappingInfo.endianess.equals(Endian.BIG_ENDIAN)) {
            code = "(data[${mappingInfo.index}] << BYTE_LENGTH) + data[${mappingInfo.index + 1}]"
        } else {
            code = "(data[${mappingInfo.index + 1}] << BYTE_LENGTH) + data[${mappingInfo.index}]"
        }
        return "$setterName($code);\n"
    }

    fun getParseFlagMapping(mappingInfo: MappedFlag, setterName: String): String {
        return "$setterName((data[${mappingInfo.index}] & ${mappingInfo.bit.name}.getMask()) == ${mappingInfo.bit.name}.getMask());\n"
    }

    fun getSerializeCode(mappingInfo: MappingInfo, getterName: String): String {
        when (mappingInfo.annotation) {
            is MappedByte -> return getSerializeByteMapping(mappingInfo.annotation, getterName)
            is MappedWord -> return getSerializeWordMapping(mappingInfo.annotation, getterName)
            is MappedFlag -> return getSerializeFlagMapping(mappingInfo.annotation, getterName)
            else -> throw IllegalStateException("Mapping ${mappingInfo.annotation.javaClass.name} is not known")
        }
    }

    fun getSerializeByteMapping(mappingInfo: MappedByte, getterName: String): String {
        return "data[${mappingInfo.index}] = $getterName();\n"
    }

    fun getSerializeWordMapping(mappingInfo: MappedWord, getterName: String): String {
        if (mappingInfo.endianess.equals(Endian.BIG_ENDIAN)) {
            return "data[${mappingInfo.index}] = ($getterName() >>> BYTE_LENGTH) & BYTE_MASK;\n" +
                    "data[${mappingInfo.index + 1}] = $getterName() & BYTE_MASK;\n"
        } else {
            return "data[${mappingInfo.index}] = $getterName() & BYTE_MASK;\n" +
                    "data[${mappingInfo.index + 1}] = ($getterName() >>> BYTE_LENGTH) & BYTE_MASK;\n"
        }
    }

    fun getSerializeFlagMapping(mappingInfo: MappedFlag, getterName: String): String {
        val flagGetterName = getterName.replace("get", "is")
        return "if($flagGetterName()) { data[${mappingInfo.index}] = data[${mappingInfo.index}] | ${mappingInfo.bit}.getMask(); } else { data[${mappingInfo.index}] = data[${mappingInfo.index}] & ~${mappingInfo.bit}.getMask(); }\n"
    }
}

/**
 * Write a Java file
 */
interface FileWriter {

    /**
     * Init the writer with the filer
     *
     * @param filer the [Filer] to use
     */
    fun init(filer: Filer)

    /**
     * Write specification from builder to the file in the given package
     * @param packageName the package name
     * @param builder the builder from JavaPoet
     */
    fun write(packageName: String, builder: TypeSpec.Builder)

}

/**
 * Concrete writer
 */
class BdpFileWriter() : FileWriter {

    private var filer: Filer? = null

    override fun init(filer: Filer) {
        this.filer = filer
    }

    override fun write(packageName: String, builder: TypeSpec.Builder) {
        val parserClass = builder.build()
        val javaFile = JavaFile
                .builder(packageName, parserClass)
                .addStaticImport(BinaryUtils::class.java, "*")
                .addStaticImport(Bit::class.java, "*")
                .build()
        javaFile.writeTo(filer);
    }

}