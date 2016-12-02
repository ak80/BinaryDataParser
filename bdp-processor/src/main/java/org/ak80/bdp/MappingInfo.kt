package org.ak80.bdp

import org.ak80.bdp.annotations.*

/**
 * Mapping information for one field
 *
 * @property name the name of the field
 * @property type the field type
 * @property annotation the mapping annotation
 */
abstract class MappingInfo(val name: String, val type: String, val annotation: Annotation) {

    abstract fun getMethodBodyGetter(getterName: String): String

    abstract fun getMethodBodySetter(setterName: String): String

}

fun create(name: String, type: String, annotation: Annotation): MappingInfo {
    when (annotation) {
        is MappedByte -> return MappingInfoByte(name, type, annotation)
        is MappedWord -> return MappingInfoWord(name, type, annotation)
        is MappedFlag -> return MappingInfoFlag(name, type, annotation)
        is MappedEnum -> return MappingInfoEnum(name, type, annotation)
        else -> throw IllegalStateException("Mapping ${annotation.javaClass.name} is not known")
    }
}

class MappingInfoByte(name: String, type: String, val byteAnnotation: MappedByte) : MappingInfo(name, type, byteAnnotation) {

    override fun getMethodBodyGetter(getterName: String): String = "data[${byteAnnotation.index}] = $getterName();\n"

    override fun getMethodBodySetter(setterName: String): String = "$setterName(data[${byteAnnotation.index}]);\n"

}

class MappingInfoWord(name: String, type: String, val wordAnnotation: MappedWord) : MappingInfo(name, type, wordAnnotation) {

    override fun getMethodBodyGetter(getterName: String): String {
        if (wordAnnotation.endianess.equals(Endian.BIG_ENDIAN)) {
            return "data[${wordAnnotation.index}] = ($getterName() >>> BYTE_LENGTH) & BYTE_MASK;\n" +
                    "data[${wordAnnotation.index + 1}] = $getterName() & BYTE_MASK;\n"
        } else {
            return "data[${wordAnnotation.index}] = $getterName() & BYTE_MASK;\n" +
                    "data[${wordAnnotation.index + 1}] = ($getterName() >>> BYTE_LENGTH) & BYTE_MASK;\n"
        }
    }

    override fun getMethodBodySetter(setterName: String): String {
        var code: String
        if (wordAnnotation.endianess.equals(Endian.BIG_ENDIAN)) {
            code = "(data[${wordAnnotation.index}] << BYTE_LENGTH) + data[${wordAnnotation.index + 1}]"
        } else {
            code = "(data[${wordAnnotation.index + 1}] << BYTE_LENGTH) + data[${wordAnnotation.index}]"
        }
        return "$setterName($code);\n"
    }

}

class MappingInfoFlag(name: String, type: String, val flagAnnotation: MappedFlag) : MappingInfo(name, type, flagAnnotation) {

    override fun getMethodBodyGetter(getterName: String): String {
        val flagGetterName = getterName.replace("get", "is")
        return "if($flagGetterName()) { data[${flagAnnotation.index}] = data[${flagAnnotation.index}] | ${flagAnnotation.bit}.getMask(); } else { data[${flagAnnotation.index}] = data[${flagAnnotation.index}] & ~${flagAnnotation.bit}.getMask(); }\n"
    }

    override fun getMethodBodySetter(setterName: String): String = "$setterName((data[${flagAnnotation.index}] & ${flagAnnotation.bit.name}.getMask()) == ${flagAnnotation.bit.name}.getMask());\n"


}

class MappingInfoEnum(name: String, type: String, val enumAnnotation: MappedEnum) : MappingInfo(name, type, enumAnnotation) {

    override fun getMethodBodyGetter(getterName: String) = "data[${enumAnnotation.index}] = $getterName().${enumAnnotation.mapTo}();\n"

    override fun getMethodBodySetter(setterName: String) = "$setterName(${type}.${enumAnnotation.mapFrom}(data[${enumAnnotation.index}] & ${BinaryUtils.getRangeMask(enumAnnotation.from, enumAnnotation.to)}));\n"

}