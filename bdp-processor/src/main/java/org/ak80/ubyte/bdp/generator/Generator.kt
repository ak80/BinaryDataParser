package org.ak80.ubyte.bdp.generator

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.ak80.ubyte.bdp.model.ByteMappedClass
import org.ak80.ubyte.bdp.model.ByteMappingInfo
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * Used to generate a source file for one byte mapped class
 */
interface Generator {

    fun generateFor(byteMappedClass: ByteMappedClass, filer: Filer)

}

/**
 * Generates source files
 */
class BdpGenerator() : Generator {

    val SUFFIX = "Parser"
    var instanceField = ""

    override fun generateFor(byteMappedClass: ByteMappedClass, filer: Filer) {
        var parserClassName = byteMappedClass.simpleName + SUFFIX

        var builder = TypeSpec.classBuilder(parserClassName).addModifiers(Modifier.PUBLIC);

        instanceField = byteMappedClass.simpleName.decapitalize()

        builder = builder.addField(TypeName.get(byteMappedClass.parentType), instanceField, Modifier.PRIVATE)

        builder = builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(TypeName.get(byteMappedClass.parentType), instanceField)
                .addCode("""
                  |this.$instanceField = $instanceField;
                """.trimMargin())
                .build())

        for (mappingInfo in byteMappedClass.getMappings()) {
            builder = createMethod(builder, mappingInfo)
        }

        val parserClass = builder.build()

        val javaFile = JavaFile.builder(byteMappedClass.packageName, parserClass).build()

        javaFile.writeTo(filer);
    }

    private fun createMethod(builder: TypeSpec.Builder, mappingInfo: ByteMappingInfo): TypeSpec.Builder {
        var builder1 = builder
        var setterName = "set${mappingInfo.name.capitalize()}"
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

}
