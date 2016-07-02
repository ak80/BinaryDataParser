package org.ak80.ubyte.bdp.model

import org.ak80.ubyte.bdp.annotations.MappedByte
import javax.lang.model.type.TypeMirror

/**
 * Holds information about class fot which a parser is created
 */

class ByteMappedClass(val simpleName: String, val packageName: String, val parentType: TypeMirror) {

    val qualifiedName = combine(simpleName, packageName)

    private val mappings: MutableList<ByteMappingInfo> = mutableListOf()

    fun addByteMapping(name: String, type: String, byteMapping: MappedByte) {
        mappings.add(ByteMappingInfo(name, type, byteMapping))
    }

    fun getMappings(): List<ByteMappingInfo> {
        return mappings
    }
}

class ByteMappingInfo(val name: String, val type: String, val mappedByte: MappedByte) {

}

class ByteMappedClasses() {

    private val map: MutableMap<String, ByteMappedClass> = mutableMapOf()

    fun get(simpleName: String, packageName: String, parentType: TypeMirror): ByteMappedClass {
        return map.getOrPut(combine(simpleName, packageName), { ByteMappedClass(simpleName, packageName, parentType) })
    }

    fun getClasses(): List<ByteMappedClass> {
        return map.values.toList()
    }

    fun clear() {
        map.clear()
    }

}

fun combine(simpleName: String, packageName: String) = packageName + "." + simpleName