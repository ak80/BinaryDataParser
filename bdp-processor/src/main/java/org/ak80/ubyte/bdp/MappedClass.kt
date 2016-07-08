package org.ak80.ubyte.bdp

import javax.lang.model.type.TypeMirror

/**
 * Holds information about class for which a parser is created and its mapped fields
 *
 * @property simpleName the simple name of the class
 * @property packageName the package name for the class
 * @property classType the type of the class
 */
class MappedClass(val simpleName: String, val packageName: String, val classType: TypeMirror) {

    val qualifiedName = combine(packageName, simpleName)

    val mappings: MutableList<MappingInfo> = mutableListOf()

    /**
     * add a mapping for a filed
     *
     * @param name the name of the field
     * @param type the field type
     * @param annotation the mapping annotation
     */
    fun addMapping(name: String, type: String, annotation: Annotation) {
        mappings.add(MappingInfo(name, type, annotation))
    }

}

/**
 * Mapping information for one field
 *
 * @property name the name of the field
 * @property type the field type
 * @property annotation the mapping annotation
 */
class MappingInfo(val name: String, val type: String, val annotation: Annotation) {

}

/**
 * Holds all [MappedClass]es
 */
class MappedClasses() {

    private val map: MutableMap<String, MappedClass> = mutableMapOf()

    /**
     * Returns the [MappedClass] for the combination of [simpleName] and [packageName]
     * *
     * It will be implicitly created if it does no exist
     *
     * @param simpleName the simple name of the class
     * @param packageName the package name for the class
     * @param classType the type of the class
     */
    fun get(simpleName: String, packageName: String, classType: TypeMirror): MappedClass {
        return map.getOrPut(
                combine(packageName, simpleName),
                { MappedClass(simpleName, packageName, classType) })
    }

    /**
     * Returns all registered [MappedClass]es
     *
     * @return the list of [MappedClass]es
     */
    fun getClasses(): List<MappedClass> {
        return map.values.toList()
    }

    /**
     * Clears the information about [MappedClass]es
     */
    fun clear() {
        map.clear()
    }

}

/**
 * Combines [packageName] and [simpleName] to the fully qualified name
 *
 * @param packageName the class package name
 * @param simpleName the class simple name
 * @return the fully qualified name
 */
fun combine(packageName: String, simpleName: String) = packageName + "." + simpleName