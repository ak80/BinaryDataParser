package org.ak80.ubyte.bdp

import org.ak80.ubyte.bdp.annotations.MappedByte
import org.ak80.ubyte.bdp.annotations.MappedWord
import org.ak80.ubyte.bdp.Generator
import org.ak80.ubyte.bdp.MappedClasses
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Processor for annotations
 */
interface BdpProcessor {

    /**
     * Init the processor with the environment
     *
     * @param processingEnvironment the processing environment
     */
    fun init(processingEnvironment: ProcessingEnvironment)

    /**
     * Process an annotation
     *
     * @param annotations the registered annotations
     * @param roundEnv the information for this round
     */
    fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean
}

/**
 * Core Processor
 *
 * @property mappedClasses the registry of mapped classes
 * @property generator the generator that generated and writes Java files
 */
class CoreProcessor(private val mappedClasses: MappedClasses, private val generator: Generator) : BdpProcessor {

    var fieldMappingAnnotations = setOf(MappedByte::class.java, MappedWord::class.java)
    var processingEnvironment: ProcessingEnvironment? = null

    private val messager: Messager by lazy {
        processingEnvironment?.messager ?: throw IllegalStateException("the CoreProcessor was not initialized")
    }

    override fun init(processingEnvironment: ProcessingEnvironment) {
        this.processingEnvironment = processingEnvironment
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        var exit: Boolean;

        for (annotatedElement in roundEnv.getElementsAnnotatedWith(MappedByte::class.java)) {
            exit = processMappedField(annotatedElement)
            if (exit) break
        }

        for (annotatedElement in roundEnv.getElementsAnnotatedWith(MappedWord::class.java)) {
            exit = processMappedField(annotatedElement)
            if (exit) break
        }

        for (byteMappedClass in mappedClasses.getClasses()) {
            generator.generateFor(byteMappedClass)
        }

        mappedClasses.clear()

        return true
    }

    private fun processMappedField(element: Element): Boolean {
        if (element.kind != ElementKind.FIELD) {
            error(element, "Only fields can be annotated with mapping annotation");
            return true;
        }
        val mappedClass = getMappedClass(element)

        val fieldName = element.simpleName.toString()
        val typeMirror = element.asType()
        val fieldType = typeMirror.kind.name.toString()

        val annotation = getAnnotation(element)
        mappedClass.addMapping(fieldName, fieldType, annotation)

        return false
    }

    private fun getMappedClass(element: Element): MappedClass {
        val classFullName = getClassFullName(element)
        val classSimpleName = getClassSimpleName(element)
        val classPackage = getClassPackage(classFullName, classSimpleName)
        val parentType = element.enclosingElement.asType()
        return mappedClasses.get(classSimpleName, classPackage, parentType)
    }

    private fun getAnnotation(element: Element): Annotation {
        var annotations: Set<Annotation> = mutableSetOf()

        for(annotationClass in fieldMappingAnnotations) {
            var annotation = element.getAnnotation(annotationClass)
            if(annotation != null) {
               annotations = annotations.plus(annotation)
            }
        }

        if(annotations.size > 1) {
            throw IllegalStateException("Only one mapping annotation allowed for element ${element.simpleName} but found: $annotations")
        }
        return annotations.single()
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