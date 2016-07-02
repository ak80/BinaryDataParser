package org.ak80.ubyte.bdp;

import com.google.auto.service.AutoService;
import org.ak80.ubyte.bdp.annotations.MappedByte;
import org.ak80.ubyte.bdp.generator.BdpGenerator;
import org.ak80.ubyte.bdp.model.ByteMappedClasses;
import org.ak80.ubyte.bdp.processor.BdpProcessor;
import org.ak80.ubyte.bdp.processor.CoreProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is the AnnotationsProcessor for the custom annotations of BinaryDataParser
 */
@AutoService(Processor.class)
public class BinaryDataParserAnnotationsProcessor extends AbstractProcessor {

  BdpProcessor bdpProcessor = new CoreProcessor(new ByteMappedClasses(), new BdpGenerator());

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.bdpProcessor.init(processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    return bdpProcessor.process(annotations, roundEnv);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.add(MappedByte.class.getCanonicalName());
    return annotations;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

}
