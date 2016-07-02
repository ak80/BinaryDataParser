package org.ak80.ubyte.bdp.processor

import org.ak80.ubyte.bdp.annotations.MappedByte
import org.ak80.ubyte.bdp.generator.Generator
import org.ak80.ubyte.bdp.model.ByteMappedClasses
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Processor
 */
interface BdpProcessor {

    fun init(processingEnvironment: ProcessingEnvironment)

    fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean
}

/**
 * Core Processor
 */
class CoreProcessor(private val byteMappedClasses: ByteMappedClasses, private val generator: Generator) : BdpProcessor {

    var processingEnvironment: ProcessingEnvironment? = null

    private val filer: Filer by lazy {
        processingEnvironment?.filer ?: throw IllegalStateException("the CoreProcessor was not initialized")
    }

    private val messager: Messager by lazy {
        processingEnvironment?.messager ?: throw IllegalStateException("the CoreProcessor was not initialized")
    }

    override fun init(processingEnvironment: ProcessingEnvironment) {
        this.processingEnvironment = processingEnvironment
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var exit = false;

        for (annotatedElement in roundEnv.getElementsAnnotatedWith(MappedByte::class.java)) {
            exit = processMappedByte(annotatedElement)
            if (exit) break
        }

        for (byteMappedClass in byteMappedClasses.getClasses()) {
            generator.generateFor(byteMappedClass, filer)
        }

        byteMappedClasses.clear()

        return true
    }

    private fun processMappedByte(element: Element): Boolean {
        if (element.kind != ElementKind.FIELD) {
            error(element, "Only fields can be annotated with @${MappedByte::class.simpleName}");
            return true;
        }
        val classFullName = getClassFullName(element)
        val classSimpleName = getClassSimpleName(element)
        val classPackage = getClassPackage(classFullName, classSimpleName)
        val parentType = element.enclosingElement.asType()
        val byteMappedClass = byteMappedClasses.get(classSimpleName, classPackage, parentType)

        val fieldName = element.simpleName.toString()
        val typeMirror = element.asType()
        val fieldType = typeMirror.kind.name.toString()

        val mappedByte = element.getAnnotation(MappedByte::class.java)

        byteMappedClass.addByteMapping(fieldName, fieldType, mappedByte)

        return false
    }

    private fun getClassFullName(element: Element): String {
        val parentTypeElement = element.enclosingElement as TypeElement
        return parentTypeElement.qualifiedName.toString()
    }

    private fun getClassSimpleName(element: Element): String {
        val parentTypeElement = element.enclosingElement as TypeElement
        return parentTypeElement.simpleName.toString()
    }

    private fun getClassPackage(fullName: String, simpleName: String): String {
        return fullName.subSequence(0, fullName.length - simpleName.length - 1).toString()

    }


    private fun error(element: Element, message: String) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }

}