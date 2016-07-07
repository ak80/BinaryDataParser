package org.ak80.ubyte.bdp.generator

import com.squareup.javapoet.*
import org.ak80.ubyte.bdp.model.ByteMappedClass
import org.ak80.ubyte.bdp.model.ByteMappingInfo
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * Used to generate a source file for one byte mapped class
 */
interface Generator {

    fun generateFor(byteMappedClass: ByteMappedClass)

}

/**
 * Generates source files
 */
class BdpGenerator(private var fileWriter: FileWriter) : Generator {

    val SUFFIX = "Parser"
    var instanceField = ""

    override fun generateFor(byteMappedClass: ByteMappedClass) {
        var parserClassName = byteMappedClass.simpleName + SUFFIX

        var builder = createBuilder(parserClassName)

        instanceField = byteMappedClass.simpleName.decapitalize()

        builder = builder.addField(TypeName.get(byteMappedClass.parentType), instanceField, Modifier.PRIVATE)

        builder = builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(byteMappedClass.parentType), instanceField)
                .addCode("""
                  |this.$instanceField = $instanceField;
                """.trimMargin())
                .build())

        var parseMethodCode =StringBuilder()

        for (mappingInfo in byteMappedClass.getMappings()) {

            builder = createMethod(builder, mappingInfo)

            var setterName = getSetterName(mappingInfo)
            parseMethodCode.append("$setterName(data[${mappingInfo.mappedByte.index}]);\n")
        }

        builder = builder.addMethod(MethodSpec.methodBuilder("parse")
                .addParameter(ArrayTypeName.of(TypeName.INT), "data")
                .addCode(parseMethodCode.toString())
                .build())

        fileWriter.write(byteMappedClass.packageName, builder)
    }

    private fun createBuilder(className: String) = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)


    private fun createMethod(builder: TypeSpec.Builder, mappingInfo: ByteMappingInfo): TypeSpec.Builder {
        var builder1 = builder
        var setterName = getSetterName(mappingInfo)
        builder1 = builder1.addMethod(MethodSpec.methodBuilder(setterName)
                .addParameter(TypeName.INT, "value")
                .addCode("""
                  |// ${mappingInfo.type} ${mappingInfo.mappedByte.index} ${mappingInfo.mappedByte.name}
                  |$instanceField.$setterName(value);
                  |
                """.trimMargin())
                .build())
        return builder1
    }

    private fun getSetterName(mappingInfo: ByteMappingInfo) = "set${mappingInfo.name.capitalize()}"

}

/**
 * Write a Java file
 */
interface FileWriter {

    /**
     * Init the writer
     *
     * @param filer the [Filer] to use
     */
    fun init(filer: Filer)

    /**
     * Write spec from builder to file in given package
     * @param packageName the package name
     * @param the Builder from JavaPoet
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
        val javaFile = JavaFile.builder(packageName, parserClass).build()
        javaFile.writeTo(filer);
    }

}