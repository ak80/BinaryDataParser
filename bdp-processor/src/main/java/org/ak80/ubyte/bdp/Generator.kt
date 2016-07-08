package org.ak80.ubyte.bdp

import com.squareup.javapoet.*
import org.ak80.ubyte.bdp.annotations.Endian
import org.ak80.ubyte.bdp.annotations.MappedByte
import org.ak80.ubyte.bdp.annotations.MappedWord
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

    val SUFFIX = "Parser"
    private val mappingGenerator = MappingGenerator()

    private var instanceField: String = ""

    override fun generateFor(mappedClass: MappedClass) {
        var parserClassName = mappedClass.simpleName + SUFFIX
        instanceField = mappedClass.simpleName.decapitalize()

        var parseMethodCode = StringBuilder()

        for (mappingInfo in mappedClass.mappings) {
            val mappingCode = createMapping(mappingInfo)
            parseMethodCode.append(mappingCode)
        }

        var builder = createBuilder(parserClassName)
                .addMethod(MethodSpec.methodBuilder("parse")
                        .addParameter(TypeName.get(mappedClass.classType), instanceField)
                        .addParameter(ArrayTypeName.of(TypeName.INT), "data")
                        .addCode(parseMethodCode.toString())
                        .build())

        fileWriter.write(mappedClass.packageName, builder)
    }

    private fun createBuilder(className: String) = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)


    private fun createMapping(mappingInfo: MappingInfo): String {
        var setterName = getSetterName(mappingInfo)
        var code = mappingGenerator.getCode(mappingInfo)
        return "$instanceField.$setterName($code);\n";
    }

    private fun getSetterName(mappingInfo: MappingInfo) = "set${mappingInfo.name.capitalize()}"

}

private class MappingGenerator() {

    fun getCode(mappingInfo: MappingInfo): String {
        when (mappingInfo.annotation) {
            is MappedByte -> return getByteMapping(mappingInfo.annotation)
            is MappedWord -> return getWordMapping(mappingInfo.annotation)
            else -> throw IllegalStateException("Mapping ${mappingInfo.annotation.javaClass.name} is not known")
        }
    }

    fun getByteMapping(mappingInfo: MappedByte): String {
        return "data[${mappingInfo.index}]"
    }

    fun getWordMapping(mappingInfo: MappedWord): String {
        if (mappingInfo.endianess.equals(Endian.BIG_ENDIAN)) {
            return "(data[${mappingInfo.index}] << BYTE_LENGTH) + data[${mappingInfo.index + 1}]"
        } else {
            return "(data[${mappingInfo.index + 1}] << BYTE_LENGTH) + data[${mappingInfo.index}]"
        }
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
                .build()
        javaFile.writeTo(filer);
    }

}