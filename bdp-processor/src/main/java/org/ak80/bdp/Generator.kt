package org.ak80.bdp

import com.squareup.javapoet.*
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

    fun getParseCode(mappingInfo: MappingInfo, setterName: String) = mappingInfo.getMethodBodySetter(setterName)


    fun getSerializeCode(mappingInfo: MappingInfo, getterName: String) = mappingInfo.getMethodBodyGetter(getterName)

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